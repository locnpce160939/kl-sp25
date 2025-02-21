package com.ftcs.balanceservice.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.balanceservice.balance_history.service.BalanceHistoryService;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.balanceservice.payment.constant.PaymentNotification;
import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.balanceservice.payment.repository.PaymentRepository;
import com.ftcs.realtimeservice.socket.contants.Message;
import com.ftcs.realtimeservice.socket.contants.MessageType;
import com.ftcs.realtimeservice.socket.service.SocketService;
import com.ftcs.transportation.trip_booking.constant.PaymentMethod;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import com.ftcs.transportation.trip_matching.constant.PaymentStatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class PaymentService {
    private static final String VIETQR_API_URL = "https://api.vietqr.io/v2/generate";
    private static final String TRANSACTION_CHECK_URL = "https://payment.ftcs.online/history";
    private static final String DEFAULT_BANK_ACCOUNT = "9073399999";
    private static final String DEFAULT_ACCOUNT_HOLDER = "NGUYEN PHUOC LOC";
    private static final int DEFAULT_BANK_ID = 970436;
    private static final int PAYMENT_SCAN_INTERVAL = 3000;

    private final PaymentRepository paymentRepository;
    private final TripBookingsRepository tripBookingsRepository;
    private final BalanceHistoryService balanceHistoryService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final SocketService socketService;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BadRequestException("Payment not found with ID: " + paymentId));
    }

    public Payment createPayment(Long bookingId, Integer accountId) {
        TripBookings booking = validateAndGetBooking(bookingId);
        validateOnlinePayment(booking);

        Payment payment = Payment.builder()
                .amount(booking.getPrice())
                .accountId(accountId)
                .bookingId(booking.getBookingId())
                .paymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatusType.PENDING)
                .build();

        paymentRepository.save(payment);

        return payment;
    }

    public String generateQrCode(Long paymentId) {
        Payment payment = validatePendingPayment(paymentId);

        try {
            HttpPost request = createQrCodeRequest(payment);
            HttpResponse response = HttpClients.createDefault().execute(request);
            String qrDataUrl = extractQrDataUrl(response);

            payment.setQrData(qrDataUrl);
            paymentRepository.save(payment);

            return qrDataUrl;
        } catch (Exception e) {
            log.error("QR code generation failed for payment ID: {}", paymentId, e);
            throw new BadRequestException("QR code generation failed: " + e.getMessage());
        }
    }

    public String verifyPayment(Long paymentId) {
        Payment payment = validatePendingPayment(paymentId);

        try {
            String transactionData = restTemplate.getForObject(TRANSACTION_CHECK_URL, String.class);
            if (isTransactionValid(transactionData, payment)) {
                completePayment(payment);
                return "Payment " + paymentId + " completed successfully";
            }
            return "Payment verification failed - No matching transaction found";
        } catch (Exception e) {
            log.error("Payment verification failed for ID: {}", paymentId, e);
            throw new BadRequestException("Payment verification failed: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = PAYMENT_SCAN_INTERVAL)
    public void scanPendingPayments() {
        log.info("Scanning pending payments");
        List<Payment> pendingPayments = paymentRepository.findPaymentByPaymentStatus(PaymentStatusType.PENDING);

        for (Payment payment : pendingPayments) {
            try {
                String transactionData = restTemplate.getForObject(TRANSACTION_CHECK_URL, String.class);
                if (isTransactionValid(transactionData, payment)) {
                    completePayment(payment);
                    notifyPaymentSuccess(payment);
                    log.info("Payment {} processed successfully", payment.getPaymentId());
                }
            } catch (Exception e) {
                log.error("Error processing payment ID: {}", payment.getPaymentId(), e);
            }
        }
    }

    private TripBookings validateAndGetBooking(Long bookingId) {
        TripBookings booking = tripBookingsRepository.findTripBookingsByBookingId(bookingId)
                .orElseThrow(() -> new BadRequestException("Invalid booking ID: " + bookingId));

        if (paymentRepository.findPaymentByBookingId(bookingId) != null) {
            throw new BadRequestException("Payment already exists for booking: " + bookingId);
        }

        if (booking.getPrice() <= 0) {
            throw new BadRequestException("Invalid payment amount");
        }

        return booking;
    }

    private void validateOnlinePayment(TripBookings booking) {
        if (booking.getPaymentMethod() != PaymentMethod.ONLINE_PAYMENT) {
            throw new BadRequestException("Only ONLINE_PAYMENT method is supported");
        }
    }

    private Payment validatePendingPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (PaymentStatusType.PAIR.equals(payment.getPaymentStatus())) {
            throw new BadRequestException("Payment already completed");
        }
        return payment;
    }

    private HttpPost createQrCodeRequest(Payment payment) throws Exception {
        HttpPost request = new HttpPost(VIETQR_API_URL);
        request.setHeader("Content-Type", "application/json");
        String paymentContent = "Pay for TripBookingId " + payment.getBookingId();
        JSONObject payload = new JSONObject()
                .put("accountNo", DEFAULT_BANK_ACCOUNT)
                .put("accountName", DEFAULT_ACCOUNT_HOLDER)
                .put("acqId", DEFAULT_BANK_ID)
                .put("amount", payment.getAmount())
                .put("addInfo", paymentContent)
                .put("format", "text")
                .put("template", paymentContent);

        request.setEntity(new StringEntity(payload.toString()));
        return request;
    }

    private String extractQrDataUrl(HttpResponse response) throws Exception {
        String jsonResponse = EntityUtils.toString(response.getEntity());
        JSONObject responseObj = new JSONObject(jsonResponse);

        // Log the complete response to understand its structure
        log.debug("API Response: {}", jsonResponse);

        // Check if response has data field
        if (!responseObj.has("data")) {
            log.error("Response doesn't contain 'data' field: {}", jsonResponse);
            throw new BadRequestException("Invalid QR API response format");
        }

        // Handle the actual response structure
        JSONObject dataObj = responseObj.getJSONObject("data");
        if (!dataObj.has("qrDataURL")) {
            log.error("Response data doesn't contain 'qrDataURL' field: {}", dataObj);
            throw new BadRequestException("QR data URL not found in response");
        }

        return dataObj.getString("qrDataURL");
    }

    private boolean isTransactionValid(String transactionData, Payment payment) throws Exception {
        JsonNode transactions = objectMapper.readTree(transactionData).get("transactions");
        String expectedContent = "Pay for TripBookingId " + payment.getBookingId();

        for (JsonNode transaction : transactions) {
            int amount = parseAmount(transaction.get("Amount").asText());
            String description = transaction.get("Description").asText();

            if (description.contains(expectedContent) && amount >= payment.getAmount()) {
                return true;
            }
        }
        return false;
    }

    private int parseAmount(String amountStr) {
        return Integer.parseInt(amountStr.replace(",", ""));
    }

    private void completePayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatusType.PAIR);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private void notifyPaymentSuccess(Payment payment) {
        TripBookings booking = getTripBooking(payment);
        PaymentNotification notification = createPaymentNotification(payment);

        try {
            String jsonContent = objectMapper.writeValueAsString(notification);
            Message socketMessage = Message.builder()
                    .messageType(MessageType.NOTIFICATION)
                    .content(jsonContent)
                    .room(booking.getAccountId().toString())
                    .username("SYSTEM")
                    .build();

            socketService.sendSocketMessage(socketMessage);
            log.info("Payment success notification sent to user: {}", booking.getAccountId());
        } catch (Exception e) {
            log.error("Failed to send payment notification", e);
        }
    }

    private TripBookings getTripBooking(Payment payment) {
        return tripBookingsRepository.findById(payment.getBookingId())
                .orElseThrow(() -> new BadRequestException("Booking not found: " + payment.getBookingId()));
    }

    private PaymentNotification createPaymentNotification(Payment payment) {
        return PaymentNotification.builder()
                .paymentId(payment.getPaymentId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .status(PaymentStatusType.PAIR)
                .message("Payment successful")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
