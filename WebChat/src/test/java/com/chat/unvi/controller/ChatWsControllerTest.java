package com.chat.unvi.controller;

import org.junit.jupiter.api.Test;
import com.chat.unvi.persistence.Message;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.chat.unvi.Constants.WebSocketConfig.DESTINATION_PREFIX;
import static com.chat.unvi.Constants.WebSocketConfig.REGISTRY;
import static com.chat.unvi.controller.ChatWsController.convertFetchGroupMassages;
import static com.chat.unvi.controller.ChatWsController.convertSendMessage;

@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatWsControllerTest {

    @Value("${local.server.port}")
    private int port;

    private WebClient client;

    @BeforeAll
    void setup() throws Exception {
        RunStopFrameHandler runStopFrameHandler = new RunStopFrameHandler(new CompletableFuture<>());
        String wsURL = "ws://localhost:" + port + REGISTRY;
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession stompSession = stompClient
                .connectAsync(wsURL, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        client = WebClient.builder()
                .stompClient(stompClient)
                .stompSession(stompSession)
                .handler(runStopFrameHandler)
                .build();
    }

    @Test
    @SneakyThrows
    void shouldSendAndGetMessage() {
        final var id = "1";
        final var message = "Message";
        final var sender = "Sender";
        final var stompSession = client.getStompSession();
        final var handler = client.getHandler();

        stompSession.subscribe(convertFetchGroupMassages(id), handler);
        stompSession.send(DESTINATION_PREFIX + convertSendMessage(id), Message.builder()
                .sender(sender)
                .content(message)
                .build());

        Message chatMessage = (Message) handler.future.get();
        Assertions.assertEquals(message, chatMessage.getContent());
        Assertions.assertEquals(sender, chatMessage.getSender());
    }

    List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    @Data
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RunStopFrameHandler implements StompFrameHandler {

        CompletableFuture<Object> future;

        @Override
        public @NonNull Type getPayloadType(StompHeaders stompHeaders) {
            log.info(stompHeaders.toString());
            return Message.class;
        }

        @Override
        public void handleFrame(@NonNull StompHeaders stompHeaders, Object o) {
            log.info(o);
            future.complete(o);
            future = new CompletableFuture<>();
        }
    }

    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class WebClient {
        WebSocketStompClient stompClient;
        StompSession stompSession;
        String sessionToken;
        RunStopFrameHandler handler;
    }
}