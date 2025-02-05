package com.ftcs.transportation.chat.repository;

import com.ftcs.transportation.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByBookingId(Long bookingId);
}
