package hac.services;

import hac.beans.RoomLockHandler;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class PlayerService {

    final static String NO_PLAYER = "Username don't exists in the DB";
    final static String NO_ROOM = "There is no room for the player";
    @Autowired
    private PlayerRepository playersRepo;

    @Resource(name = "getLockForAllDb")
    private ReentrantReadWriteLock DBLock;

    @Resource(name = "getRoomLock")
    private RoomLockHandler roomsLock;

    public Player createNewPlayer(String username){
        Player player = new Player();
        player.setUsername(username);
        player.setStatus(Player.PlayerStatus.NOT_READY);
        return player;
    }

    public Player getPlayerByUsername(String username, Boolean needToLockDB, Boolean needToLockRoom) throws RuntimeException{
        try {
            if(needToLockDB)
                DBLock.readLock().lock();
            Player player = playersRepo.findByUsername(username);
            if(player == null){
                System.out.println("Player not found");
                throw new RuntimeException(NO_PLAYER);
            }
            return player;
        }finally {
            if(needToLockDB)
                DBLock.readLock().unlock();
        }

    }

    public Room getRoomByUsername(String username, Boolean toLockDB) throws RuntimeException{
        try{
            if(toLockDB)
                DBLock.readLock().lock();
            Room room = getPlayerByUsername(username, false).getRoom();
            if (room == null) {
                System.out.println("Room not found");
                throw new RuntimeException(NO_ROOM);
            }
            return room;
        }
        finally {
            if (toLockDB)
                DBLock.readLock().unlock();
        }
    }

    public Room.RoomEnum getRoomStatusByUserName(String username){
        return getRoomByUsername(username, true).getStatus();
    }

//    public Player.PlayerStatus getPlayerStatusByUsername(String username){
//        return getPlayerByUsername(username, true).getStatus();
//    }

    @Transactional
    public void removePlayer(String username) throws RuntimeException{
        try {
            DBLock.writeLock().lock();
            Player player = getPlayerByUsername(username, false);
            Room room = player.getRoom();
            if (room!=null) {
                room.getPlayers().remove(player);
                //TODO handle with more cases, it's not good to do this.
                room.setStatus(Room.RoomEnum.WAITING_FOR_NEW_PLAYER);
            }
            player.setRoom(null);
            playersRepo.delete(player);
        }finally {
            DBLock.writeLock().unlock();
        }
    }
}
