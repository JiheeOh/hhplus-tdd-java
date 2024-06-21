package io.hhplus.tdd.point.dto;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    // 등록이 안되어있던 사용자를 구분하기 위해 updateMillis를 0으로 설정
    public static UserPoint empty(long id) {
        return new UserPoint(id,0,0);
    }
}
