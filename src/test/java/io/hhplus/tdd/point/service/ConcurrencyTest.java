package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.dto.UserPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    PointService pointService;

    /**
     * 동일 사용자가 여러번 요청을 보낼경우
     * 순차적으로 처리하여
     * 전제 조건 : 등록된 사용자여야 한다.
     */
    @DisplayName("한명의 사용자가 여러번 요청을 보낼 경우 ")
    @Test
    void oneUserMultiRequest() throws ExecutionException, InterruptedException {
        // given : 사용자 포인트 100 등록
        long userId = 2L;
        pointService.charge(userId, 100);

        // when
        // 포인트 50 사용
        CompletableFuture<UserPoint> resultActions1 = CompletableFuture.supplyAsync(() -> pointService.usePoint(userId, 50));
        // 포인트 200 충전
        CompletableFuture<UserPoint> resultActions2 = CompletableFuture.supplyAsync(() -> pointService.charge(userId, 200));
        // 포인트 100 사용
        CompletableFuture<UserPoint> resultActions3 = CompletableFuture.supplyAsync(() -> pointService.usePoint(userId, 100));
        // 포인트 50 사용
        CompletableFuture<UserPoint> resultActions4 = CompletableFuture.supplyAsync(() -> pointService.usePoint(userId, 50));
        // 포인트 100 충전
        CompletableFuture<UserPoint> resultActions5 = CompletableFuture.supplyAsync(() -> pointService.charge(userId, 100));

        List<CompletableFuture<UserPoint>> futures = List.of(resultActions1, resultActions2, resultActions3, resultActions4, resultActions5);

        CompletableFuture<List<UserPoint>> result = CompletableFuture.allOf(futures.toArray((new CompletableFuture[futures.size()])))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));


        //then
        assertThat(pointService.getPoint(1).point()).isEqualTo(200);

    }


    /**
     * 실행하고 난 다음에 이력을 잘 쌓고 있는지도 확인
     * 포인트 사용 및 충전 이력을 순차적으로 잘 기록하는지 테스트
      */

    @DisplayName("포인트 사용/충전 이력을 순차적으로 잘 기록하는지 테스트 ")
    @Test
    void pointHistoryCheckTest() throws ExecutionException, InterruptedException {
        // given : 사용자 포인트 100 등록
        long userId = 1L;
        pointService.charge(userId, 100);

        long[] pointHistory = {50, 250, 150, 100, 200};

        // when
        // 포인트 50 사용
        CompletableFuture<UserPoint> resultActions1 = CompletableFuture.supplyAsync(() -> pointService.usePoint(userId, 50));
        // 포인트 200 충전
        CompletableFuture<UserPoint> resultActions2 = CompletableFuture.supplyAsync(() -> pointService.charge(userId, 200));
        // 포인트 100 사용
        CompletableFuture<UserPoint> resultActions3 = CompletableFuture.supplyAsync(() -> pointService.usePoint(userId, 100));
        // 포인트 50 사용
        CompletableFuture<UserPoint> resultActions4 = CompletableFuture.supplyAsync(() -> pointService.usePoint(userId, 50));
        // 포인트 100 충전
        CompletableFuture<UserPoint> resultActions5 = CompletableFuture.supplyAsync(() -> pointService.charge(userId, 100));

        List<CompletableFuture<UserPoint>> futures = List.of(resultActions1, resultActions2, resultActions3, resultActions4, resultActions5);

        CompletableFuture<List<UserPoint>> result = CompletableFuture.allOf(futures.toArray((new CompletableFuture[futures.size()])))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));


        //then
        for (int i = 0; i < result.get().size(); i++) {
            assertThat(result.get().get(i).point()).as("[point %d]", i).isEqualTo(pointHistory[i]);
        }

    }


}
