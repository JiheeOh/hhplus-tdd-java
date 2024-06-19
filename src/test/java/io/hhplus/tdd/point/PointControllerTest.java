package io.hhplus.tdd.point;

import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.util.NoUserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PointController.class)
@ExtendWith(MockitoExtension.class)
class PointControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    private final String url = "/point";


    /**
     * id가 null 상태인 경우,
     * Controller에서 BadRequest인 것을 알려줘야한다.
     * 기본적으로 항상 성공이라서 괜히 쓴 코드인가 싶음..
     */
    @Test
    @DisplayName("Id가 null일 경우 ")
    void nullUserId() throws Exception {
        //given
        Long userId = null;

        //when
        ResultActions resultActions = mockMvc.perform(get(String.format("%s/%d", url, userId))
                .contentType(MediaType.APPLICATION_JSON));

        //given
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());

    }

    /**
     * 등록되지 않은 유저의 포인트를 조회시
     * response로 UserNotFoundException을 던져줘야한다.
     */
    @Test
    @DisplayName("등록되지 않은 유저의 포인트 조회 ")
    void pointNoUser() throws Exception {
        //given
        long userId = 1L;

        //when
        when(pointService.getPoint(userId)).thenThrow(NoUserException.class);

        ResultActions resultActions = mockMvc.perform(get(String.format("%s/%d", url, userId))
                .contentType(MediaType.APPLICATION_JSON));


        // then
        resultActions.andExpect(status().is5xxServerError())
                .andDo(print());

    }


    /**
     * 특정 유저의 포인트를 조회하고
     * response로 UserPoint를 json화한 데이터를 넘겨준다.
     */
    @Test
    @DisplayName("특정 유저 포인트 정상 조회 API")
    void point() throws Exception {
        //given
        long userId = 1L;
        long updateMillis = System.currentTimeMillis();
        UserPoint userPoint = new UserPoint(userId, 1, updateMillis);

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
                .andExpect(jsonPath("updateMillis").value(updateMillis));

    }


    /**
     * id와 point가 넘어올 시 충전된 값을 json형식으로 return하는 방식
     * @throws Exception
     */
    @Test
    @DisplayName("포인트 충전 API")
    void charge() throws Exception {
        // given
        long userId = 1L;
        long amount = 10L;
        long updateMillis = System.currentTimeMillis();
        UserPoint userPoint = new UserPoint(userId,amount,updateMillis);

        // when
        when(pointService.charge(userId,amount))
                .thenReturn(userPoint);

        ResultActions resultActions = mockMvc.perform(patch(String.format("%s/%d/charge", url, userId))
                        .content(String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON));


        //then
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("id").value(userId))
                .andExpect(jsonPath("point").value(amount))
                .andExpect(jsonPath("updateMillis").value(updateMillis));
    }

    // 등록되지 않은 사용자 / 사용할 수 있는 포인트가 없을 경우 발생시키는 exception을 굳이 해야할까 싶다...
    // 로직이 없으니까 안하기로 생각함

}