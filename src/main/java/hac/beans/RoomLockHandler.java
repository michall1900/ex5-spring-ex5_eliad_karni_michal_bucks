package hac.beans;

import hac.classes.customErrors.DbError;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class RoomLockHandler implements Serializable {
    @Resource(name = "getRoomLocksMap")
    private HashMap<Long, ReentrantReadWriteLock> roomsLock;

    public RoomLockHandler() {
    }

    public HashMap<Long, ReentrantReadWriteLock> getRoomsLock() {
        return roomsLock;
    }

    public void setRoomsLock(HashMap<Long, ReentrantReadWriteLock> roomsLock) {
        this.roomsLock = roomsLock;
    }

    public ReentrantReadWriteLock getRoomLock(Long id){
        ReentrantReadWriteLock readWriteLock = roomsLock.get(id);
        if (readWriteLock == null)
            throw new DbError();
        return roomsLock.get(id);
    }

    public void setNewRoomLock(Long id){
        ReentrantReadWriteLock readWriteLock = roomsLock.get(id);
        if (readWriteLock != null)
            throw new RuntimeException("The lock already exist");
    }
}
