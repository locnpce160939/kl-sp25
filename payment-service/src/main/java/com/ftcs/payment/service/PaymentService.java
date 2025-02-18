package com.ftcs.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.payment.constant.PaymentNotification;
import com.ftcs.payment.model.Payment;
import com.ftcs.payment.repository.PaymentRepository;
import com.ftcs.realtimeservice.socket.contants.Message;
import com.ftcs.realtimeservice.socket.contants.MessageType;
import com.ftcs.realtimeservice.socket.service.SocketService;
import com.ftcs.transportation.trip_booking.constant.PaymentMethod;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import com.ftcs.transportation.trip_matching.constant.PaymentStatusType;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@EnableScheduling
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final TripBookingsRepository tripBookingsRepository;
    private final ObjectMapper objectMapper;
    private final SocketService socketService;

    private static final String VIETQR_API_URL = "https://api.vietqr.io/v2/generate";
    private static final String TRANSACTION_CHECK_URL = "http://localhost/lsgd.php";
    private static final String DEFAULT_ACCOUNT_NO = "0791000055332";
    private static final String DEFAULT_ACCOUNT_NAME = "Le Tan Quoc";
    private static final int DEFAULT_ACQ_ID = 970436;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BadRequestException("Payment not found with ID: " + paymentId));
    }

    public void createPayment(Long bookingId) {
        validatePaymentRequest(bookingId);
        TripBookings tripBookings = findTripBookingByBookingId(bookingId);
        if (tripBookings.getPaymentMethod() != PaymentMethod.ONLINE_PAYMENT) {
            throw new BadRequestException("Chỉ hỗ trợ tạo thanh toán cho đơn hàng có phương thức thanh toán ONLINE_PAYMENT.");
        }
            Payment payment = Payment.builder()
                    .amount(tripBookings.getPrice())
                    .bookingId(tripBookings.getBookingId())
                    .paymentDate(LocalDateTime.now())
                    .paymentStatus(PaymentStatusType.PENDING)
//                .content(generatePaymentContent(tripBookings))
                    .build();
            paymentRepository.save(payment);
    }

    public Payment updatePaymentStatus(Long paymentId, PaymentStatusType newStatus) {
        Payment payment = getPaymentById(paymentId);
        validateStatusTransition(payment.getPaymentStatus(), newStatus);
        payment.setPaymentStatus(newStatus);
        return paymentRepository.save(payment);
    }

    public String getQrBank(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (PaymentStatusType.PAIR.equals(payment.getPaymentStatus())) {
            throw new BadRequestException("Payment already completed");
        }

        try {
            HttpPost httpPost = createHttpPost(payment);
            HttpResponse response = HttpClients.createDefault().execute(httpPost);
            String qrDataUrl = extractQrDataUrl(response);
            payment.setQrData(qrDataUrl);
            paymentRepository.save(payment);
            return qrDataUrl;
        } catch (IOException | JSONException e) {
            log.error("Error generating QR code for payment ID: {}", paymentId, e);
            throw new BadRequestException("Failed to generate QR code: " + e.getMessage());
        }
    }

    public String checkTransactionStatus(Long paymentId) {
        try {
            Payment payment = getPaymentById(paymentId);

            if (PaymentStatusType.PAIR.equals(payment.getPaymentStatus())) {
                return "Payment already completed";
            }

            String jsonData = restTemplate.getForObject(TRANSACTION_CHECK_URL, String.class);
            if (verifyTransaction(jsonData, payment)) {
                updatePaymentStatus(payment);
                return "Bill " + payment.getPaymentId() + " Paid";
            }

            return "Payment verification failed - No matching transaction found";
        } catch (JsonProcessingException e) {
            log.error("Error processing transaction data for payment ID: {}", paymentId, e);
            throw new BadRequestException("Failed to verify payment: " + e.getMessage());
        }
    }

    @SneakyThrows
    private HttpPost createHttpPost(Payment payment) {
        HttpPost httpPost = new HttpPost(VIETQR_API_URL);
        httpPost.setHeader("Content-Type", "application/json");

        JSONObject jsonPayload = new JSONObject()
                .put("accountNo", DEFAULT_ACCOUNT_NO)
                .put("accountName", DEFAULT_ACCOUNT_NAME)
                .put("acqId", DEFAULT_ACQ_ID)
                .put("amount", payment.getAmount())
                .put("addInfo", generatePaymentContent(payment))
                .put("format", "text")
                .put("template", generatePaymentContent(payment));
        httpPost.setEntity(new StringEntity(jsonPayload.toString()));
        return httpPost;
    }


    private String extractQrDataUrl(HttpResponse response) throws IOException, JSONException {
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity == null) {
            throw new BadRequestException("Empty response from QR generation service");
        }

        String jsonResponse = EntityUtils.toString(responseEntity);
        JSONObject jsonObject = new JSONObject(jsonResponse);
        return jsonObject.getJSONObject("data").getString("qrDataURL");
    }

    private boolean verifyTransaction(String jsonData, Payment payment) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(jsonData);
        JsonNode transactionsNode = jsonNode.get("transactions");

        String expectedContent = generatePaymentContent(payment);

        for (JsonNode transactionNode : transactionsNode) {
            String amountStr = transactionNode.get("Amount").asText();
            int amount = Integer.parseInt(amountStr.replace(",", ""));
            String description = transactionNode.get("Description").asText();

            if (description.contains(expectedContent) && amount >= payment.getAmount()) {
                return true;
            }
        }
        return false;
    }

    private void updatePaymentStatus(Payment payment) {
        payment.setPaymentStatus(PaymentStatusType.PAIR);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private void validatePaymentRequest(Long bookingId) {
        TripBookings tripBookings = findTripBookingByBookingId(bookingId);
        Payment payment = paymentRepository.findPaymentByBookingId(bookingId);
        if(payment != null) {
            throw new BadRequestException("Payment with bookingId" + bookingId + " already exists");
        }
        if (tripBookings.getPrice() <= 0) {
            throw new BadRequestException("Invalid payment amount");
        }
    }

    private void validateStatusTransition(PaymentStatusType currentStatus, PaymentStatusType newStatus) {
        if (PaymentStatusType.PAIR.equals(currentStatus)) {
            throw new BadRequestException("Cannot update completed payment");
        }
    }

    private String generatePaymentContent(Payment payment) {
        return "Pay for TripBookingId " + payment.getBookingId();
    }

    private TripBookings findTripBookingByBookingId(Long bookingId){
        return  tripBookingsRepository.findTripBookingsByBookingId(bookingId).
                orElseThrow(() -> new BadRequestException("Invalid booking ID: " + bookingId));
    }

    @Scheduled(fixedRate = 3000)
    public void scanPendingPayments() {
        log.info("Starting scan of pending payments");
        List<Payment> pendingPayments = paymentRepository.findPaymentByPaymentStatus(PaymentStatusType.PENDING);

        for (Payment payment : pendingPayments) {
            try {
                String jsonData = restTemplate.getForObject(TRANSACTION_CHECK_URL, String.class);
                if (verifyTransaction(jsonData, payment)) {
                    updatePaymentStatus(payment);
                    notifyPaymentSuccess(payment);
                    log.info("Scan" + payment.getPaymentId() + "success");
                }
                else{
                    log.info("Scan" + payment.getPaymentId() + "fail");
                }
            } catch (JsonProcessingException e) {
                log.error("Error processing transaction for payment ID: {}", payment.getPaymentId(), e);
            }
        }
    }

    private void notifyPaymentSuccess(Payment payment) {
        TripBookings booking = tripBookingsRepository.findById(payment.getBookingId())
                .orElseThrow(() -> new BadRequestException("Booking not found: " + payment.getBookingId()));

        PaymentNotification notification = PaymentNotification.builder()
                .paymentId(payment.getPaymentId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .status(PaymentStatusType.PAIR)
                .message("Payment successful")
                .timestamp(LocalDateTime.now())
                .build();

        String jsonContent;
        try {
            jsonContent = objectMapper.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            log.error("Error converting notification to JSON", e);
            jsonContent = "Payment " + payment.getPaymentId() + " successful";
        }

        Message socketMessage = Message.builder()
                .messageType(MessageType.NOTIFICATION)
                .content(jsonContent)
                .room(booking.getAccountId().toString())
                .username("SYSTEM")
                .build();

        socketService.sendSocketMessage(socketMessage);
        log.info("Payment success notification sent to user: {}", booking.getAccountId());
    }

}