package com.chat.unvi;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    @UtilityClass
    public class WebSocketConfig {
        public static final String REGISTRY = "/ws";
        public static final String DESTINATION_PREFIX = "/app";

    }
    @UtilityClass
    public class WebSocketController {
        public static final String SEND_MESSAGE = "/chat/{id}/send";
        public static final String FETCH_GROUP_MESSAGES = "/topic/chat/{id}/messages";
    }
}