package com.hhp.concert.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QueueType {
    WAITING("waiting"),
    PROCESSING("processing");

    private final String str;
}
