package hac.services;

import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.repo.room.RoomRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Service
public class RoomService {

    static final String ROOM_NOT_FOUND ="Room not found";
    static final String PLAYER_IN_ROOM = "Player is already in the room";

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private PlayerRepository playersRepo;

    @Resource(name="getRoomLock")
    ReentrantReadWriteLock roomLock;

    @Resource(name="getPlayerLock")
    ReentrantReadWriteLock playerLock;

    @Transactional
    public Room saveRoom(Room room) {
        try {
            roomLock.writeLock().lock();
            playerLock.writeLock().lock();
            return roomRepo.save(room);
        }
        finally {
            roomLock.writeLock().unlock();
            playerLock.writeLock().unlock();
        }
    }

    public Room createNewRoom(){
        Room room = new Room();
        room.setStatus(Room.RoomEnum.WAITING_FOR_NEW_PLAYER);
        return room;
    }


    @Transactional
    public void addPlayerToRoom(long roomId, Player newPlayer) {
        //TODO be careful with the locks, and be aware of a dead lock that could happened if in any case it happened in opposite way.
        // TODO - check whats happened if player change an attribute.
        try{
            roomLock.writeLock().lock();
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new RuntimeException(ROOM_NOT_FOUND));

            playerLock.writeLock().lock();
            if (playersRepo.findByPlayerIdAndRoomId(newPlayer.getId(), roomId).isPresent()) {
                throw new RuntimeException(PLAYER_IN_ROOM);
            }

            newPlayer.setRoom(room);
            room.add(newPlayer);
            roomRepo.save(room);
        }
        finally {
            roomLock.writeLock().unlock();
            if (playerLock.isWriteLockedByCurrentThread())
                playerLock.writeLock().unlock();
        }
    }

    public void changeRoomStatus(long roomId, Room.RoomEnum status){
        try{
            roomLock.writeLock().lock();
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new RuntimeException(ROOM_NOT_FOUND));
            room.setStatus(status);
            roomRepo.save(room);
        }
        finally {
            roomLock.writeLock().unlock();
        }
    }

    public List<Room> getAllRooms(){
        try{
            roomLock.readLock().lock();
            return roomRepo.findAll();
        }
        finally {
            roomLock.readLock().unlock();
        }
    }
}

