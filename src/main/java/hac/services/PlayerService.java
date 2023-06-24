package hac.services;

import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playersRepo;

    @Resource(name="getPlayerLock")
    ReentrantReadWriteLock playerLock;

    public Player createNewPlayer(String username){
        Player player = new Player();
        player.setUsername(username);
        player.setStatus(Player.PlayerStatus.NOT_READY);
        return player;
    }

    public Player getPlayerByUsername(String username) throws Exception{
        try {
            playerLock.readLock().lock();
            Player player = playersRepo.findByUsername(username);
            if(player == null){
                throw new Exception("username don't exists in the DB");
            }
            return player;
        }finally {
            playerLock.readLock().unlock();
        }
    }
}
