package hac.repo.room;

import hac.repo.player.Player;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Room {
    public enum RoomEnum {
        WAITING_FOR_NEW_PLAYER,
        WAITING_FOR_BOARDS,
        ON_GAME
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    @OneToMany(mappedBy = "room")
    @Size (max=2, message="The number of players should not exceed 2.")
    private List<Player> players = new ArrayList<>();

    @OneToOne
    private Player currentPlayer;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }



}