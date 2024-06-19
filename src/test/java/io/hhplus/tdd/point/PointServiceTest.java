package io.hhplus.tdd.point;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    private PointService pointService;
    private static final FakeUserPointRepository fakePointRepository = new FakeUserPointRepository();

    private long userId = 1L;

    @BeforeEach
    void setUp() {
        pointService = new PointService(fakePointRepository);
    }

    @AfterEach
    void tearDown() {
        fakePointRepository.clear();
        System.out.println("fake Repository 초기화");
    }


    /**
     * 등록되어있지 않는 사용자의 포인트를 조회할 경우
     * UserNotFoundException 처리
     */
    @Test
    @DisplayName("등록되어 있지 않는 사용자의 포인트를 조회")
    void getPointNoUser() {
        //given : id가 있을 경우를 대비해 삭제 처리
        fakePointRepository.remove(userId);

        //then : UserNotFoundException으로 처리되는지 확인
        assertThrows(NoUserException.class, () -> pointService.getPoint(1L));
    }

    /**
     * 등록된 사용자의 포인트를 조회한다.
     */
    @Test
    @DisplayName("사용자의 포인트를 조회")
    void getPoint() {
        //given
        UserPoint userPoint = new UserPoint(1, 10, 0);
        fakePointRepository.insertOrUpdate(userId, 10);

        //when
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
        long newUser = 2L;
        long amount = 10L;
        UserPoint expectedPoint = new UserPoint(newUser, amount, 0);
        fakePointRepository.remove(userId);

        //when
        UserPoint result = pointService.charge(userId, amount);

        //Then
        assertThat(result.id()).isEqualTo(expectedPoint.id());
        assertThat(result.point()).isEqualTo(expectedPoint.point());

    }

    /**
     * 기존 사용자로, 포인트가 있을 경우
     */
    @Test
    @DisplayName("기존 사용자의 포인트 적립")
    void chargeOldId() {
        //given
        long addAmount = 10L;
        long existPoint = 20L;

        UserPoint expectedPoint = new UserPoint(userId, addAmount + existPoint, 0);
        fakePointRepository.insertOrUpdate(userId, existPoint);

        //when
        UserPoint result = pointService.charge(userId, addAmount);

        //Then
        assertThat(result.id()).isEqualTo(expectedPoint.id());
        assertThat(result.point()).isEqualTo(expectedPoint.point());
    }

    /**
     * 기존 사용자의 포인트를 사용하는 경우
     */
    @Test
    @DisplayName("기존 사용자의 포인트를 사용하는 경우")
    void usePoint() {
        //given
        long useAmount = 10L;
        long existPoint = 100L;

        // 기존 사용자 데이터 생성
        fakePointRepository.insertOrUpdate(userId, existPoint);

        //when
        UserPoint result = pointService.usePoint(userId, useAmount);

        //Then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.point()).isEqualTo(existPoint - useAmount);

    }

    /**
     * 기존 사용자의 잔고가 부족할 경우
     * NoPointException 처리
     */
    @DisplayName("기존 사용자의 잔고가 부족할 경우 : NoPointException")
    @Test
    void noPoint() {
        // given
        long existPoint = 9L;
        long amount = 10L;

        fakePointRepository.insertOrUpdate(userId, existPoint);

        //then
        assertThrows(NoPointException.class, () -> pointService.usePoint(userId, amount));

    }


    /**
     * 등록되지 않은 사용자가 포인트를 사용하려고 하는 경우
     * NoUserException 처리
     */
    @DisplayName("등록되지 않은 사용자가 포인트를 사용하려고 하는 경우 : NoUserException")
    @Test
    void noUserTryUsePoint() {
        // given
        long amount = 10L;
        fakePointRepository.remove(userId);

        //then
        assertThrows(NoUserException.class, () -> pointService.usePoint(userId, amount));

    }


}