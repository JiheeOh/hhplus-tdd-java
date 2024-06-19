package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.point.util.TransactionType;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}