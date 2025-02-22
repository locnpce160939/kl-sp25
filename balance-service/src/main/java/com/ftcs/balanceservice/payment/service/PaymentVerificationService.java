package com.ftcs.balanceservice.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.balanceservice.payment.constant.PaymentStatus;
import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.balanceservice.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentVerificationService {

    @Value("${payment.transaction.check.url}")
    private String transactionCheckUrl;

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentNotificationService paymentNotificationService;

    public String verifyPayment(Payment payment, String transactionData) {
        if (transactionData == null) {
            transactionData = fetchTransactionData();
        }

        if (isTransactionValid(transactionData, payment)) {
            completePayment(payment);
            paymentNotificationService.notifyPaymentSuccess(payment);
            return "Payment " + payment.getPaymentId() + " completed successfully";
        }
        return "Payment verification failed - No matching transaction found";
    }

    @Scheduled(fixedRateString = "${payment.scan.interval}")
    public void scanPendingPayments() {
        log.info("Scanning pending payments...");

        List<Payment> pendingPayments = paymentRepository.findPaymentByPaymentStatus(PaymentStatus.PENDING);
        if (pendingPayments.isEmpty()) {
            log.info("No pending payments found. Skipping transaction check.");
            return;
        }

        String transactionData = fetchTransactionData();
        if (transactionData == null) {
            log.warn("Skipping payment scan due to missing transaction data.");
            return;
        }

        for (Payment payment : pendingPayments) {
            try {
                String result = verifyPayment(payment, transactionData);
                if (result.contains("completed successfully")) {
                    log.info("Payment {} processed successfully", payment.getPaymentId());
                }
            } catch (Exception e) {
                log.error("Error processing payment ID: {}", payment.getPaymentId(), e);
            }
        }
    }


    private String fetchTransactionData() {
        try {
            return restTemplate.getForObject(transactionCheckUrl, String.class);
        } catch (HttpClientErrorException e) {
            log.error("Error calling transaction API: {}", e.getStatusCode(), e);
            return null;
        } catch (Exception e) {
            log.error("Failed to fetch transaction data", e);
            return null;
        }
    }

    private boolean isTransactionValid(String transactionData, Payment payment) {
        try {
            JsonNode transactions = objectMapper.readTree(transactionData).path("transactions");
            String expectedContent = "Pay for TripBookingId " + payment.getBookingId();

            for (JsonNode transaction : transactions) {
                int amount = parseAmount(transaction.path("Amount").asText());
                String description = transaction.path("Description").asText();

                if (description.contains(expectedContent) && amount >= payment.getAmount()) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("Error parsing transaction data", e);
        }
        return false;
    }

    private void completePayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
        log.info("Payment {} marked as PAID", payment.getPaymentId());
    }

    private int parseAmount(String amountStr) {
        try {
            return Integer.parseInt(amountStr.replace(",", "").trim());
        } catch (NumberFormatException e) {
            log.error("Failed to parse amount: {}", amountStr, e);
            return 0;
        }
    }
}
