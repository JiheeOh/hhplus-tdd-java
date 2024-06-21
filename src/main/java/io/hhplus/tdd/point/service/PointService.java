package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.util.NoPointException;
import io.hhplus.tdd.point.util.NoUserException;
import io.hhplus.tdd.point.util.TransactionType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class PointService {

    private final UserPointRepository userPointRepository;

    private final PointHistoryRepository pointHistoryRepository;

//     재진입이 가능한 읽기-쓰기 락 구성
//     재진입 : history update 시 락 재진입이 가능해야한다. -> 근데 현재 구현한건 재진입이 필요없는 것 같아서 괜히 했나 싶다...
//     다수의 스레드가 읽기는 가능해야하지만 쓰기는 단일 스레드로만 수행하고자 한다.

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public PointService(UserPointRepository userPointRepository, PointHistoryRepository pointHistoryRepository) {
        this.userPointRepository = userPointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    public UserPoint getPoint(long id) {
        readLock.lock();
        try{
            UserPoint result = userPointRepository.selectById(id);
            // update한 시각이 없으므로 등록된 사용자가 아니다
            if (result.updateMillis() == 0) {
                throw new NoUserException();
            }
            return result;
        }finally {
            readLock.unlock();
        }

    }

    public UserPoint charge(long id, long amount) {
        writeLock.lock();
        try{
            UserPoint existData = userPointRepository.selectById(id);
            // 기존 사용자일 경우 기존에 있던 포인트와 합산
            if (existData.updateMillis() != 0) {
                amount = existData.point() + amount;
            }
            UserPoint result = userPointRepository.insertOrUpdate(id, amount);
            pointHistoryRepository.insert(id, amount, TransactionType.CHARGE, result.updateMillis());

            return result;
        }finally {
            writeLock.unlock();
        }

    }

    public UserPoint usePoint(long id, long amount) {
        writeLock.lock();
        try{
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
        }finally {
            {
                writeLock.unlock();
            }
        }

    }

    public List<PointHistory> history(long id) {
        readLock.lock();
        try{
            // 등록되어 있는 사용자인지 확인
            UserPoint userPoint = userPointRepository.selectById(id);
            if (userPoint.updateMillis() == 0) {
                throw new NoUserException();
            }

            return pointHistoryRepository.selectAllByUserId(id);
        }finally {
            readLock.unlock();
        }

    }
}
