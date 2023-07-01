package hac.beans;

import hac.classes.customErrors.DbError;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The component manages a dict of locks so each room in the database will have its own lock.
 */
@Component
public class RoomLockHandler implements Serializable {
    /**
     * The rooms' lock dict.
     */
    @Resource(name = "getRoomLocksMap")
    private HashMap<Long, ReentrantReadWriteLock> roomsLock;

    /**
     * Ctor.
     */
    public RoomLockHandler() {
    }

    /**
     * The rooms' locks dict getter.
     * @return The rooms' locks dict.
     */
    public HashMap<Long, ReentrantReadWriteLock> getRoomsLock() {
        return roomsLock;
    }

    /**
     * The function is a setter of the rooms' locks dict.
     * @param roomsLock The new rooms' locks dict.
     */
    public void setRoomsLock(HashMap<Long, ReentrantReadWriteLock> roomsLock) {
        this.roomsLock = roomsLock;
    }

    /**
     * The function returns the room's lock which the received room id belongs to.
     * @param id The id of the room lock belongs to.
     * @return The room's lock which the id belongs to.
     */
    public ReentrantReadWriteLock getRoomLock(Long id){
        ReentrantReadWriteLock readWriteLock = roomsLock.get(id);
        if (readWriteLock == null)
            throw new DbError();
        return roomsLock.get(id);
    }

    /**
     * The function adds to the dict a new lock which its key in the dict is the room's id.
     * @param id The room's id.
     */
    public void setNewRoomLock(Long id){
        ReentrantReadWriteLock readWriteLock = roomsLock.get(id);
        if (readWriteLock != null)
            throw new RuntimeException("The lock already exist");
        roomsLock.put(id, new ReentrantReadWriteLock());
    }

    /**
     * The function removes the lock which it's key in the dict is the received parameter.
     * @param id The id of the room its lock need to get removed.
     */
    public void removeLock(Long id){
        roomsLock.remove(id);
    }
}
