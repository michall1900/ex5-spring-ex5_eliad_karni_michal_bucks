package hac.repo.room;

import hac.repo.board.Board;
import hac.repo.player.Player;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The Room class represents a game room where players can join and play a game.
 */
@Entity
public class Room {
    /**
     * Enumerates the possible states of the room.
     */
    public enum RoomEnum {
        WAITING_FOR_NEW_PLAYER,
        WAITING_FOR_BOARDS,
        ON_GAME,
        GAME_OVER
    }

    /**
     * The maximum number of players allowed in the room.
     */
    public final static int SIZE = 2;

    /**
     * The default index value used for the current player index.
     */
    final static int DEFAULT_INDEX = -1;

    /**
     * The unique identifier of the room.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    /**
     * A list of strings representing update objects associated with the room.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "update_objects", columnDefinition = "TEXT")
    private List<String> updateObjects;

    /**
     * A list of players currently in the room.
     */
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Size(max = SIZE, message = "The number of players should not exceed " + SIZE)
    private List<Player> players = new ArrayList<>();

    /**
     * The index of the current player in the list of players.
     */
    @Column
    private int currentPlayerIndex = DEFAULT_INDEX;

    /**
     * The status of the room, represented by the RoomEnum.
     */
    @Enumerated(EnumType.STRING)
    private RoomEnum status;

    /**
     * The option associated with the board in the room.
     */
    @Enumerated(EnumType.STRING)
    private Board.Options option;

    /**
     * Gets the unique identifier of the room.
     * @return The room's unique identifier.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the room.
     * @param id The room's unique identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the list of players currently in the room.
     * @return A list of players in the room.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Sets the list of players in the room.
     * @param players A list of players to set.
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Gets the index of the current player in the list of players.
     * @return The index of the current player.
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Sets the index of the current player in the list of players.
     * @param currentPlayer The index of the current player.
     */
    public void setCurrentPlayerIndex(int currentPlayer) {
        this.currentPlayerIndex = currentPlayer;
    }

    /**
     * Gets the status of the room.
     * @return The status of the room.
     */
    public RoomEnum getStatus() {
        return status;
    }

    /**
     * Sets the status of the room.
     * @param status The status to set.
     */
    public void setStatus(RoomEnum status) {
        this.status = status;
    }

    /**
     * Returns the option associated of the board.
     * @return The option.
     */
    public Board.Options getOption() {
        return option;
    }

    /**
     * Sets the option for the board.
     * @param option The option to set.
     */
    public void setOption(Board.Options option) {
        this.option = option;
    }

    /**
     * Returns the list of update objects of with the board.
     * @return The list of update objects.
     */
    public List<String> getUpdateObjects() {
        return updateObjects;
    }

    /**
     * Sets the list of update objects for the board.
     * @param updateObjects The list of update objects to set.
     */
    public void setUpdateObjects(List<String> updateObjects) {
        this.updateObjects = updateObjects;
    }

    /**
     * Adds a player to the room.
     * @param player The player to add.
     */
    public void add(Player player) {
        if (player != null) {
            this.players.add(player);
            player.setRoom(this);
        }
    }

    /**
     * Returns a string representation of the Room object.
     *
     * @return The string representation of the Room object.
     */
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

    /**
     * Returns a map containing information about the room.
     *
     * @return A map containing information about the room.
     */
    public Map<String, String> getInfo(){
        StringBuilder players = new StringBuilder("[");
        for (Player player : this.players){
            players.append( "\"" + player.getUsername() + "\"").append(",");
        }
        players = new StringBuilder(players.substring(0, players.length() - 1) + "]");
        Map<String,String> info = new HashMap<>();
        info.put("id", Long.toString(this.id));
        info.put("players", players.toString());
        info.put("type", this.option.name());
        return info;
    }

    /**
     * Checks if the room is full.
     *
     * @return true if the room is full, false otherwise.
     */
    public boolean full(){
        return this.players.size() == Room.SIZE;
    }
}
