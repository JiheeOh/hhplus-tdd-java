package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service

public class PointService {

    private final UserPointTable userPointTable;

    public PointService(UserPointTable userPointTable){
        this.userPointTable = userPointTable;
    }
    public UserPoint getPoint(long id) {
        return userPointTable.selectById(id);
    }
}
