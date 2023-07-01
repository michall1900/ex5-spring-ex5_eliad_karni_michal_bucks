package hac.services;

import hac.beans.RoomLockHandler;
import hac.classes.customErrors.DbError;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * The member service acts like an api to the players DB.
 */
@Service
public class PlayerService {

    /**
     * Error message in case the username is not exists in the DN.
     */
    final static String NO_PLAYER = "Username don't exists in the DB";
    /**
     * Error message in case the room is full.
     */
    final static String NO_ROOM = "There is no room for the player";

    /**
     * The players repository.
     */
    @Autowired
    private PlayerRepository playersRepo;

    /**
     * The resource is a lock to all the DB.
     */
    @Resource(name = "getLockForAllDb")
    private ReentrantReadWriteLock DBLock;

    /**
     * The resource is an object of all the rooms locks.
     */
    @Resource(name = "getRoomLock")
    private RoomLockHandler roomsLock;

    /**
     * The method creates a new player at the DB.
     * @param username the new user's username
     * @return The new player's object.
     */
    public Player createNewPlayer(String username){
        Player player = new Player();
        player.setUsername(username);
        player.setStatus(Player.PlayerStatus.NOT_READY);
        return player;
    }

    /**
     * The function returns the player object by its username.
     * @param username The searched username.
     * @param needToLockDB If the DB is needed.
     * @return The found user.
     * @throws RuntimeException Thrown in case the search has failed.
     */
    public Player getPlayerByUsername(String username, Boolean needToLockDB) throws RuntimeException{
        try {
            if(needToLockDB)
                DBLock.readLock().lock();
            Player player = playersRepo.findByUsername(username);
            if(player == null){
                throw new RuntimeException(NO_PLAYER);
            }
            return player;
        }finally {
            if(needToLockDB)
                DBLock.readLock().unlock();
        }

    }

    /**
     * The function gets the room the received player in.
     * @param username The player's username.
     * @param toLockDB If the DB lock action is needed.
     * @return The room the player at.
     * @throws RuntimeException when there is an error to find the room.
     */
    public Room getRoomByUsername(String username, Boolean toLockDB) throws RuntimeException{
        try{
            if(toLockDB)
                DBLock.readLock().lock();
            Room room = getPlayerByUsername(username, false).getRoom();
            if (room == null) {
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
     * The method returns the status of the room which the player at.
     * @param username The player's username.
     * @return The  status of the room which the player at.
     */
    public Room.RoomEnum getRoomStatusByUserName(String username){
        return getRoomByUsername(username, true).getStatus();
    }

    /**
     * The function returns the player's status.
     * Assumption - the function who called this function locked the needed locks.
     * @param username The player's username.
     * @return The player's status.
     */
    public Player.PlayerStatus getPlayerStatusByUsername(String username){
        return getPlayerByUsername(username, false).getStatus();
    }


    /**
     * The function sets the received user as winner in the received model and returns if the user not found.
     * @param username The winner user.
     * @param model The reset model.
     * @return If the user been found in the DB.
     */
    public Boolean setWinnersInModelReturnIfNotFound(String username, Model model){
        try {
            DBLock.readLock().lock();
            Player player = getPlayerByUsername(username, false);
            roomsLock.getRoomLock(player.getRoom().getId()).readLock().lock();
            try {
                player = getPlayerByUsername(username, false);
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
