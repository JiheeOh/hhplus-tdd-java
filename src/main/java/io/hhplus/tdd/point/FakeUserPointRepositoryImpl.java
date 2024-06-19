package io.hhplus.tdd.point;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

public class FakeUserPointRepositoryImpl implements UserPointRepository{
    public static Map<Long , UserPoint> fakeTable = new HashMap<>();

    @Override
    public UserPoint selectById(long id) {
        return fakeTable.getOrDefault(id,UserPoint.empty(id));
    }

    @Override
    public UserPoint insertOrUpdate(long id, long amount) {
        UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
        fakeTable.put(id,userPoint);
        return fakeTable.get(id);
    }

    public void remove(long id){
        fakeTable.remove(id);
    }

    public void clear(){
        fakeTable.clear();
    }
}
