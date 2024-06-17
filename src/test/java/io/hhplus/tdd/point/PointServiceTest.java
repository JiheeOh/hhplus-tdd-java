package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.*;

class PointServiceTest {

//    @Mock
//    UserPointTable userPointTable;
//
    private Long userId = 1L;
    @Test
    @DisplayName("사용자 이름이 없을 경우")
    void getPointWhenNoUser() {
        //given


        //when

        //then
    }
}