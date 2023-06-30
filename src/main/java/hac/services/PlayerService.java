package hac.services;

import hac.beans.RoomLockHandler;
import hac.classes.customErrors.DbError;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.repo.room.RoomRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

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
    @Autowired
    private RoomRepository roomRepository;

    public Player createNewPlayer(String username){
        Player player = new Player();
        player.setUsername(username);
        player.setStatus(Player.PlayerStatus.NOT_READY);
        return player;
    }

    public Player getPlayerByUsername(String username, Boolean needToLockDB) throws RuntimeException{
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

    /**
     *
     * Assumption - the function who called this function locked the needed locks.
     * @param username
     * @return
     */
    public Room.RoomEnum getRoomStatusByUserName(String username){
        return getRoomByUsername(username, true).getStatus();
    }

    /**
     * Assumption - the function who called this function locked the needed locks.
     * @param username
     * @return
     */
    public Player.PlayerStatus getPlayerStatusByUsername(String username){
        return getPlayerByUsername(username, false).getStatus();
    }



    public Boolean setWinnersInModelReturnIfNotFound(String username, Model model){
        try {
            DBLock.readLock().lock();
            Player player = getPlayerByUsername(username, false);
            roomsLock.getRoomLock(player.getRoom().getId()).readLock().lock();
            try {
                if (player.getRoom().getStatus()!= Room.RoomEnum.GAME_OVER)
                    throw new DbError();
                if (player.getStatus() == Player.PlayerStatus.WIN) {
                    model.addAttribute("status", "WIN");
                    return true;
                }
                if (player.getStatus() == Player.PlayerStatus.LOSE) {
                    Room room = player.getRoom();
                    for (Player checkedPlayer : room.getPlayers()) {
                        if (checkedPlayer.getStatus() == Player.PlayerStatus.WIN) {
                            model.addAttribute("winner", checkedPlayer.getUsername());
                            break;
                        }
                    }
                    model.addAttribute("status", "LOSE");
                    return true;
                }
                return false;
            }
            finally {
                roomsLock.getRoomLock(player.getRoom().getId()).readLock().unlock();
            }
        }
        finally {
            DBLock.readLock().unlock();
        }
    }
}
