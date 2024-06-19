package io.hhplus.tdd.point;

import org.apache.catalina.User;

import java.util.HashMap;
import java.util.Map;

public class FakeUserPointRepository implements UserPointRepository{
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

    public void clear(long id){
        fakeTable.remove(id);
    }
}
