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
}