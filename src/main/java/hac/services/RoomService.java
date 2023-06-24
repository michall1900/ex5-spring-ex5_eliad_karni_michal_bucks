package hac.services;

import hac.repo.board.Board;
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

//    @Resource(name="getRoomLock")
//    ReentrantReadWriteLock roomLock;
//
//    @Resource(name="getPlayerLock")
//    ReentrantReadWriteLock playerLock;

//    synchronized private void lockAll(){
//        roomLock.writeLock().lock();
//        playerLock.writeLock().lock();
//    }

    @Transactional
    public Room saveRoom(Room room) {
        //try {
            //lockAll();
            return roomRepo.save(room);
        //}
//        finally {
//            roomLock.writeLock().unlock();
//            playerLock.writeLock().unlock();
//        }
    }
    @Transactional
    public Room createNewRoom(Player player, int type){
        Room room = new Room();
        room.setStatus(Room.RoomEnum.WAITING_FOR_NEW_PLAYER);
        room.setOption(Board.Options.values()[type]);
        room.add(player);
        return saveRoom(room);
    }

    @Transactional
    public void addPlayerToRoom(long roomId, Player newPlayer) {
        // TODO check transaction lock
        // TODO - check whats happened if player change an attribute.
        //try{
            //lockAll();
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new RuntimeException(ROOM_NOT_FOUND));
            if (playersRepo.findByUsername(newPlayer.getUsername()) != null) {
                throw new RuntimeException(PLAYER_IN_ROOM);
            }
            room.add(newPlayer);
            //roomRepo.save(room);
        //}
//        finally {
//            roomLock.writeLock().unlock();
//            playerLock.writeLock().unlock();
//        }
    }
    @Transactional
    public void changeRoomStatus(long roomId, Room.RoomEnum status){
    //    try{
      //      roomLock.writeLock().lock();
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new RuntimeException(ROOM_NOT_FOUND));
            System.out.println(room.getId());
            System.out.println(room.getStatus());
            System.out.println(status);
            room.setStatus(status);
            //roomRepo.save(room);
  //      }
//        finally {
//            roomLock.writeLock().unlock();
//        }
    }
    @Transactional(readOnly = true)
    public List<Room> getAllRooms(){
        //try{
            //roomLock.readLock().lock();
            return roomRepo.findAll();
        //}
//        finally {
//            roomLock.readLock().unlock();
//        }
    }
}

