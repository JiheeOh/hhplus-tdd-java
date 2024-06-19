package io.hhplus.tdd.point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    private PointService pointService;
    private final FakeUserPointRepository fakePointRepository = new FakeUserPointRepository();

    @BeforeEach
    void setUp() {
        pointService = new PointService(fakePointRepository);
    }

    /**
     * 등록되어있지 않는 사용자의 포인트를 조회할 경우
     * UserNotFoundException 처리
     */
    @Test
    @DisplayName("등록되어 있지 않는 사용자의 포인트를 조회")
    void getPointNoUser() {
        //given
        long userId = 1L;

        //when : id가 있을 경우를 대비해 삭제 처리
        fakePointRepository.clear(userId);

        //then : UserNotFoundException으로 처리되는지 확인
       assertThrows(UserNotFoundException.class,()-> pointService.getPoint(1L));
    }

    /**
     * 등록된 사용자의 포인트를 조회한다.
     */
    @Test
    @DisplayName("사용자의 포인트를 조회")
    void getPoint() {
        //given
        long userId = 1L;
        UserPoint userPoint = new UserPoint(1, 10, 0);

        //when
        fakePointRepository.insertOrUpdate(userId,10);
        UserPoint result = pointService.getPoint(1L);

        //then
        assertThat(result.id()).isEqualTo(userPoint.id());
        assertThat(result.point()).isEqualTo(userPoint.point());
    }

    /**
     * 사용자의 포인트를 등록
     * 기존에 포인트가 없는 신규 사용자일 경우
     */
    @Test
    @DisplayName("신규 사용자의 포인트 적립")
    void chargeNewId() {
        //given
        long userId = 2L;
        long amount = 10L;
        UserPoint expectedPoint = new UserPoint(userId, amount, 0);

        //when
        fakePointRepository.clear(userId);
        UserPoint result = pointService.charge(userId, amount);

        //Then
        assertThat(result.id()).isEqualTo(expectedPoint.id());
        assertThat(result.point()).isEqualTo(expectedPoint.point());

    }

    /**
     * 기존 사용자로, 포인트가 있을 경우
     */
    @Test
    @DisplayName("기존사용자의 포인트 적립")
    void chargeOldId() {
        //given
        long userId = 3L;
        long addAmount = 10L;
        long existPoint = 20L;

        UserPoint expectedPoint = new UserPoint(userId, addAmount + existPoint, 0);

        //when
        fakePointRepository.insertOrUpdate(userId,existPoint);
        UserPoint result = pointService.charge(userId, addAmount);

        //Then
        assertThat(result.id()).isEqualTo(expectedPoint.id());
        assertThat(result.point()).isEqualTo(expectedPoint.point());
    }
}