package com.ftcs.balanceservice.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.balanceservice.payment.constant.PaymentNotification;
import com.ftcs.balanceservice.payment.constant.PaymentStatus;
import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.realtimeservice.socket.contants.Message;
import com.ftcs.realtimeservice.socket.contants.MessageType;
import com.ftcs.realtimeservice.socket.service.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentNotificationService {
    private final SocketService socketService;
    private final ObjectMapper objectMapper;

    public void notifyPaymentSuccess(Payment payment) {
        PaymentNotification notification = PaymentNotification.builder()
                .paymentId(payment.getPaymentId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .status(PaymentStatus.PAID)
                .message("Payment successful")
                .timestamp(LocalDateTime.now())
                .build();

        try {
            String jsonContent = objectMapper.writeValueAsString(notification);
            Message socketMessage = Message.builder()
                    .messageType(MessageType.NOTIFICATION)
                    .content(jsonContent)
                    .room(payment.getAccountId().toString())
                    .username("SYSTEM")
                    .build();

            socketService.sendSocketMessage(socketMessage);
            log.info("Payment success notification sent to user: {}", payment.getAccountId());
        } catch (Exception e) {
            log.error("Failed to send payment notification", e);
        }
    }
}
