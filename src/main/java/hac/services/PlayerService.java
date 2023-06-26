package hac.services;

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

//    @Resource(name="getPlayerLock")
//    ReentrantReadWriteLock playerLock;
//
//    @Resource(name="getRoomLock")
//    ReentrantReadWriteLock roomLock;

    public Player createNewPlayer(String username){
        Player player = new Player();
        player.setUsername(username);
        player.setStatus(Player.PlayerStatus.NOT_READY);
        return player;
    }
    @Transactional(readOnly = true)
    public Player getPlayerByUsername(String username, Boolean NeedToLock) throws RuntimeException{
       // try {
//            if(NeedToLock)
//                playerLock.readLock().lock();
            Player player = playersRepo.findByUsername(username);
            if(player == null){
                System.out.println("Player not found");
                throw new RuntimeException(NO_PLAYER);
            }
            return player;
//        }finally {
//            if(NeedToLock)
//                playerLock.readLock().unlock();
//        }
    }
    @Transactional(readOnly = true)
    public Room getRoomByUsername(String username) throws RuntimeException{
//        try{
//            roomLock.readLock().lock();
//            playerLock.readLock().lock();
            Room room = getPlayerByUsername(username, false).getRoom();
            if (room == null) {
                System.out.println("Room not found");
                throw new RuntimeException(NO_ROOM);
            }
            return room;
//        }
//        finally {
//            roomLock.readLock().unlock();
//            playerLock.readLock().unlock();
//        }
    }

    @Transactional(readOnly = true)
    public Room.RoomEnum getRoomStatusByUserName(String username){
        return getRoomByUsername(username).getStatus();
    }
    @Transactional(readOnly = true)
    public List<Player> getAllPlayers(){
//        try{
//            playerLock.readLock().lock();
            return playersRepo.findAll();
//        }
//        finally {
//            playerLock.readLock().unlock();
//        }
    }

    @Transactional
    public Player.PlayerStatus getPlayerStatusByUsername(String username){
        return getPlayerByUsername(username, true).getStatus();
    }

    @Transactional
    public void removePlayer(String username) throws RuntimeException{
        Player player = getPlayerByUsername(username, false);
        Room room = player.getRoom();
        if (room!=null) {
            room.getPlayers().remove(player);
            room.setStatus(Room.RoomEnum.WAITING_FOR_NEW_PLAYER);
        }
        player.setRoom(null);
        playersRepo.delete(player);
    }
}
