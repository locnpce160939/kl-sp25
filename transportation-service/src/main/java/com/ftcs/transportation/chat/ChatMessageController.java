package com.ftcs.transportation.chat;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.transportation.TransportationURL;
import com.ftcs.transportation.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(TransportationURL.CHAT_MESSAGE)
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @GetMapping("/{tripBookingId}")
    public ApiResponse<?> createSchedule(@PathVariable("tripBookingId") Long tripBookingId,
                                                @RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(chatMessageService.getHistoryChat(tripBookingId, accountId));
    }
}
