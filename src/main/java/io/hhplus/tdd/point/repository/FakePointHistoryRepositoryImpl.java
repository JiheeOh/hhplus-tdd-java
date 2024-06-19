package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.util.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class FakePointHistoryRepositoryImpl implements PointHistoryRepository {

    public static List<PointHistory>  fakeTable = new ArrayList<>();
    private long cursor = 1;
    @Override
    public PointHistory insert(long userId, long amount, TransactionType type, long updateMillis) {
        PointHistory pointHistory = new PointHistory(cursor++, userId, amount, type, updateMillis);
        fakeTable.add(pointHistory);
        return pointHistory;
    }

    @Override
    public List<PointHistory> selectAllByUserId(long userId) {
        return fakeTable.stream().filter(pointHistory -> pointHistory.userId() == userId).toList();
    }
}
