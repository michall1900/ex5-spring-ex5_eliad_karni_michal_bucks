package hac.repo.room;

import hac.repo.board.Board;
import hac.repo.player.Player;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Entity
public class Room {
    public enum RoomEnum {
        WAITING_FOR_NEW_PLAYER,
        WAITING_FOR_BOARDS,
        ON_GAME,
        GAME_OVER
    }
    public final static int SIZE = 2;
    final static int DEFAULT_INDEX = -1;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    private long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "update_objects", columnDefinition = "TEXT")
    private List<String> updateObjects;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Size(max=SIZE, message="The number of players should not exceed "+ SIZE)
    private List<Player> players = new ArrayList<>();

    @Column
    private int currentPlayerIndex = DEFAULT_INDEX;

    @Enumerated(EnumType.STRING)
    private RoomEnum status;

    @Enumerated(EnumType.STRING)
    private Board.Options option;


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

    public Board.Options getOption() {
        return option;
    }

    public void setOption(Board.Options option) {
        this.option = option;
    }

    public List<String> getUpdateObjects() {
        return updateObjects;
    }

    public void setUpdateObjects(List<String> updateObjects) {
        this.updateObjects = updateObjects;
    }

    public void add(Player player){
        if (player!=null){
            this.players.add(player);
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

    public Map<String,String> getInfo(){
        //TODO ask eliad about that - there is a change and after it redeclaration.
        StringBuilder players = new StringBuilder("[");
        for (Player player : this.players){
            players.append( "\"" + player.getUsername() + "\"").append(",");
        }
        players = new StringBuilder(players.substring(0, players.length() - 1) + "]");
        Map<String,String> info = new HashMap<String, String>();
        info.put("id", Long.toString(this.id));
        info.put("players", players.toString());
        info.put("type", this.option.name());
        return info;
    }

    public boolean full(){
        return this.players.size() == Room.SIZE;
    }
}
