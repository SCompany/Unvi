package com.chat.unvi.persistence;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat implements Serializable {
    @Builder.Default
    String id = String.valueOf(UUID.randomUUID());

    String name;
}
