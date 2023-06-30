package hac.repo.player;
import hac.repo.board.Board;
import hac.repo.room.Room;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Player {

    /**
     * The enum is the available player's statuses.
     */
    public enum PlayerStatus{
        NOT_READY,
        READY,
        ON_GAME,
        WIN,
        LOSE
    }

    /**
     * The id of the player.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * The room the player at.
     */
    @ManyToOne
    @JoinColumn(name="room_id", nullable = false)
    @NotNull
    private Room room;

    /**
     * The player's room.
     */
    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private Board board;

    /**
     * The username.
     */
    @Column(unique = true)
    @NotNull(message = "Username is mandatory")
    private String username;

    /**
     * The player's status.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private PlayerStatus status;

    /**
     * The method is the id getter.
     * @return The player's id
     */
    public long getId() {
        return id;
    }

    /**
     * The function is the id setter.
     * @param id The new id value.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * The method is the room getter.
     * @return The player's room.
     */
    public Room getRoom() {
        return this.room;
    }

    /**
     * The function is the room setter.
     * @param room The new room value.
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * The method is the board getter.
     * @return The player's board.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * The function is the board setter.
     * @param board The new board value.
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * The method is the id username.
     * @return The player's username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * The function is the username setter.
     * @param username The new username value.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * The method is the id status.
     * @return The player's status.
     */
    public PlayerStatus getStatus() {
        return this.status;
    }

    /**
     * The function is the status setter.
     * @param status The new status value.
     */
    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    /**
     * The String casting of the object.
     * @return The String casting of the object value.
     */
    @Override
    public String toString() {
        return "Player{" + "id = " + getId() + ", name = " + getUsername() + ", status = " + getStatus() +
                getRoom().getId() + "}";
    }
}
