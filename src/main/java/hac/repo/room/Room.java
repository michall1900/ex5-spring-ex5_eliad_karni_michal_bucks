package hac.repo.room;

import hac.classes.GameBoard;
import hac.repo.player.Player;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Entity
public class Room {
    public enum RoomEnum {
        WAITING_FOR_NEW_PLAYER,
        WAITING_FOR_BOARDS,
        ON_GAME
    }
    final static int SIZE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    private long id;


    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Size(max=SIZE, message="The number of players should not exceed "+ SIZE)
    private List<Player> players = new ArrayList<>();

    @OneToOne
    private Player currentPlayer;

    @Enumerated(EnumType.ORDINAL)
    private RoomEnum status;

    @Enumerated(EnumType.ORDINAL)
    private GameBoard.Options option;


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

    public RoomEnum getStatus() {
        return status;
    }

    public void setStatus(RoomEnum status) {
        this.status = status;
    }

    public GameBoard.Options getOption() {
        return option;
    }

    public void setOption(GameBoard.Options option) {
        this.option = option;
    }

    public void add(Player player){
        if (player!=null){
            players.add(player);
            player.setRoom(this);
        }
    }

    @Override
    public String toString(){
        AtomicReference<String> playersId = new AtomicReference<>("[");
        if (getPlayers()!= null)
            getPlayers().forEach((Player player)->{
                if(player!= null) {
                    playersId.updateAndGet(v -> v + player.getId()+", ");
                }
        });
        playersId.updateAndGet(v -> v + "]");
        return "Room{" + "id = " + getId() + ", players ids = " + playersId + ", status = " + getStatus() +
                ", current player id = "+ ((getCurrentPlayer()!=null)?getCurrentPlayer().getId(): null) + "}";
    }
}
