package com.ftcs.transportation.chat.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.realtimeservice.socket.contants.Message;
import com.ftcs.realtimeservice.socket.contants.MessageType;
import com.ftcs.realtimeservice.socket.service.SocketService;
import com.ftcs.transportation.chat.dto.ChatMessageDto;
import com.ftcs.transportation.chat.model.ChatMessage;
import com.ftcs.transportation.chat.repository.ChatMessageRepository;
import com.ftcs.transportation.trip_agreement.model.TripAgreement;
import com.ftcs.transportation.trip_agreement.repository.TripAgreementRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ftcs.common.utils.JsonUtils.toJson;
import static com.ftcs.realtimeservice.socket.service.SocketUtils.getRoomSocket;

@Service
@AllArgsConstructor
@Slf4j
public class ChatMessageService {
    private final ObjectMapper objectMapper;
    private final SocketIOServer server;
    private final SocketService socketService;

    private final TripAgreementRepository tripAgreementRepository;
    private final ChatMessageRepository chatMessageRepository;
    @PostConstruct
    public void init() {
        this.server.addEventListener(MessageType.MESSAGE_SEND.toString(), Message.class, onSendMessage());
    }

    private DataListener<Message> onSendMessage() {
        return (client, data, ackSender) -> {
            //if (isAuthenticated(client)) {
            ChatMessageDto chatMessage = objectMapper.readValue(data.getContent(), ChatMessageDto.class);
            log.info("Event: MESSAGE_SEND, Data: TripBookings id {} send {}", chatMessage.getTripBookingId(), chatMessage.getMessage());
            saveAndSendMessageToClient(chatMessage, getRoomSocket(client));
            //}
        };
    }

    public void saveAndSendMessageToClient(ChatMessageDto dto, Integer senderId) {
        ChatMessage chatMessage = createChatMessage(dto, senderId);
        chatMessageRepository.save(chatMessage);

        Integer recipientId = getRecipientId(dto, senderId);
        sendMessageToClient(dto, recipientId);
    }

    private ChatMessage createChatMessage(ChatMessageDto dto, Integer senderId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageContent(dto.getMessage());
        chatMessage.setBookingId(dto.getTripBookingId());
        chatMessage.setSenderId(senderId);
        return chatMessage;
    }

    private Integer getRecipientId(ChatMessageDto dto, Integer senderId) {
        TripAgreement tripAgreement = getTripAgreement(dto.getTripBookingId());
        assert tripAgreement != null : "TripAgreement not found for booking ID " + dto.getTripBookingId();

        return tripAgreement.getDriverId().equals(senderId)
                ? tripAgreement.getCustomerId()
                : tripAgreement.getDriverId();
    }

    private void sendMessageToClient(ChatMessageDto dto, Integer recipientId) {
        Message message = Message.builder()
                .messageType(MessageType.MESSAGE_RECEIVED)
                .room(recipientId.toString())
                .content(toJson(dto))
                .username("system")
                .build();

        log.info("Event: MESSAGE_RECEIVED, Data: Schedule id {} send to {} message {}",
                dto.getTripBookingId(), recipientId, dto.getMessage());

        socketService.sendSocketMessage(message);
    }

    public List<ChatMessage> getHistoryChat(Long tripBookingId, Integer accountId) {
        TripAgreement tripAgreement = getTripAgreement(tripBookingId);
        if(!tripAgreement.getDriverId().equals(accountId) && !tripAgreement.getCustomerId().equals(accountId)){
            throw new BadRequestException("No permission to access this chat");
        }
        return chatMessageRepository.findAllByBookingId(tripBookingId);
    }

    private TripAgreement getTripAgreement(Long tripBookingId) {
        return tripAgreementRepository.findByBookingId(tripBookingId);
    }
}
