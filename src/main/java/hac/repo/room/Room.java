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
    final static int DEFAULT_INDEX = -1;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    private long id;


    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Size(max=SIZE, message="The number of players should not exceed "+ SIZE)
    private List<Player> players = new ArrayList<>();

    @Column
    private int currentPlayerIndex = DEFAULT_INDEX;

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

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayer) {
        this.currentPlayerIndex = currentPlayer;
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
        AtomicReference<String> playersName = new AtomicReference<>("[");
        if (getPlayers()!= null)
            getPlayers().forEach((Player player)->{
                if(player!= null) {
                    playersName.updateAndGet(v -> v + player.getUsername()+", ");
                }
        });
        playersName.updateAndGet(v -> v + "]");
        return "Room{" + "id = " + getId() + ", players ids = " + playersName + ", status = " + getStatus() +
                ", current player id = "+ ((getCurrentPlayerIndex()!=DEFAULT_INDEX)?getCurrentPlayerIndex(): null) + "}";
    }
}
