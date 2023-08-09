package com.chat.unvi.controller;

import com.chat.unvi.persistence.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import static com.chat.unvi.Constants.WebSocketController.FETCH_GROUP_MESSAGES;
import static com.chat.unvi.Constants.WebSocketController.SEND_MESSAGE;


@Controller
public class ChatWsController {

//    @MessageMapping(SEND_MESSAGE)
//    @SendTo(FETCH_GROUP_MASSAGES)
//    public void createChat(@DestinationVariable String chatName) {
//        Chat chat = Chat.builder()
//                .name(chatName)
//                .build();
//
//        return ;
//    }

    @MessageMapping(SEND_MESSAGE)
    @SendTo(FETCH_GROUP_MESSAGES)
    public Message sendMessage(@DestinationVariable("id") String id,
                               @Payload Message webSocketChatMessage) {
        // TODO add to database
        return webSocketChatMessage;
    }

    public static String convertFetchGroupMassages (String  chatId) {
        return FETCH_GROUP_MESSAGES.replace("{id}", chatId);
    }

    public static String convertSendMessage (String chatId) {
        return SEND_MESSAGE.replace("{id}", chatId);
    }
}
