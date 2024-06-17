package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PointController.class)
@ExtendWith(MockitoExtension.class)
class PointControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    private final String url = "/point";

    @Test
    @DisplayName("특정 유저 포인트 정상 조회 API")
    void point() throws Exception {
        //given
        Long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 1, 0);

        //when
        when(pointService.getPoint(userId))
                .thenReturn(userPoint);

        ResultActions resultActions = mockMvc.perform(get(String.format("%s/%d", url, userId))
                .contentType(MediaType.APPLICATION_JSON));


        //then
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("id").value(userId))
                .andExpect(jsonPath("point").value(1))
                .andExpect(jsonPath("updateMillis").value(0));

    }

    @Test
    @DisplayName("포인트 충전/이용 내역 조회 API")
    void history() {
    }

    @Test
    @DisplayName("포인트 충전 API")
    void charge() {
    }

    @Test
    @DisplayName("포인트 사용 API")
    void use() {
    }
}