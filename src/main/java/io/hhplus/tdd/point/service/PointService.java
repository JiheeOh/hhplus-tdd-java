package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.util.NoPointException;
import io.hhplus.tdd.point.util.NoUserException;
import io.hhplus.tdd.point.util.TransactionType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PointService {

    private final UserPointRepository userPointRepository;

    private final PointHistoryRepository pointHistoryRepository;

    public PointService(UserPointRepository userPointRepository, PointHistoryRepository pointHistoryRepository) {
        this.userPointRepository = userPointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    public UserPoint getPoint(long id) {
        UserPoint result = userPointRepository.selectById(id);
        // update한 시각이 없으므로 등록된 사용자가 아니다
        if (result.updateMillis() == 0) {
            throw new NoUserException();
        }
        return result;
    }

    public UserPoint charge(long id, long amount) {
        UserPoint existData = userPointRepository.selectById(id);
        // 기존 사용자일 경우 기존에 있던 포인트와 합산
        if (existData.updateMillis() != 0) {
            amount = existData.point() + amount;
        }
        UserPoint result = userPointRepository.insertOrUpdate(id, amount);
        pointHistoryRepository.insert(id, amount, TransactionType.CHARGE, result.updateMillis());
        return result;
    }

    public UserPoint usePoint(long id, long amount) {
        UserPoint existPoint = userPointRepository.selectById(id);

        // 기존 사용자가 아닐 경우
        if (existPoint.updateMillis() == 0) {
            throw new NoUserException();
        } else if (existPoint.point() - amount < 0) { // 포인트 부족시
            throw new NoPointException();
        }
        // 사용하고 남은 잔액 update
        long newAmount = existPoint.point() - amount;
        UserPoint result = userPointRepository.insertOrUpdate(id, newAmount);
        // 사용한 이력 이력 table에 적재
        pointHistoryRepository.insert(id, newAmount, TransactionType.USE, result.updateMillis());

        // 새로운 포인트로 update 필요
        return result;
    }

    public List<PointHistory> history(long id) {
        List<PointHistory> histories = new ArrayList<>();

        // 등록되어 있는 사용자인지 확인
        UserPoint userPoint = userPointRepository.selectById(id);
        if (userPoint.updateMillis() == 0) {
            throw new NoUserException();
        }

        return pointHistoryRepository.selectAllByUserId(id);
    }
}
