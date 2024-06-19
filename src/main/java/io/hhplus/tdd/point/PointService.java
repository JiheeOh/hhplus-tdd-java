package io.hhplus.tdd.point;

import org.springframework.stereotype.Service;

@Service
public class PointService {

    private final UserPointRepository userPointRepository;

    public PointService(UserPointRepository userPointRepository) {
        this.userPointRepository = userPointRepository;
    }

    public UserPoint getPoint(long id) {
        UserPoint result = userPointRepository.selectById(id);
        if (result.updateMillis() == 0) {
            throw new NoUserException();
        }
        return result;
    }

    public UserPoint charge(long id, long amount) {
        UserPoint result = userPointRepository.selectById(id);
        // 기존 사용자임
        if (result.updateMillis() != 0) {
            amount = result.point() + amount;
        }

        return userPointRepository.insertOrUpdate(id, amount);
    }

    public UserPoint usePoint(long id, long amount) {
        UserPoint existPoint = userPointRepository.selectById(id);

        if (existPoint.updateMillis() == 0) {
            throw new NoUserException();
        } else if (existPoint.point() - amount < 0) {
            throw new NoPointException();
        }

        UserPoint result = new UserPoint(id, existPoint.point() - amount, System.currentTimeMillis());
        userPointRepository.insertOrUpdate(result.id(), result.point());

        return result;
    }
}
