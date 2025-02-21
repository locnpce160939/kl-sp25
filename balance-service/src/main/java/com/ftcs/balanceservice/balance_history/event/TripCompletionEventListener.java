package com.ftcs.balanceservice.balance_history.event;

import com.ftcs.transportation.trip_booking.dto.TripCompletedEvent;
import com.ftcs.balanceservice.balance_history.service.BalanceHistoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TripCompletionEventListener {
    private final BalanceHistoryService balanceHistoryService;

    @EventListener
    @Transactional
    public void handleTripCompletedEvent(TripCompletedEvent event) {
        log.info("Received trip completion event: {}", event);  // Add log để debug
        try {
            balanceHistoryService.recordPaymentCredit(
                    event.getBookingId(),
                    event.getAccountId(),
                    event.getAmount()
            );
            log.info("Successfully created balance history for booking: {}", event.getBookingId());
        } catch (Exception e) {
            log.error("Failed to create balance history for booking {}: {}",
                    event.getBookingId(), e.getMessage());
            throw e;  // Re-throw để transaction rollback
        }
    }
}
