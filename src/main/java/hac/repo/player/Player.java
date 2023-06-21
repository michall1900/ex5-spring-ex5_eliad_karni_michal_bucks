package hac.repo.player;
import hac.repo.board.Board;
import hac.repo.room.Room;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Player {

    public enum PlayerStatus{
        NOT_READY,
        READY,
        ON_GAME,
        LEFT_GAME
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    @ManyToOne
    private Room room;

    @OneToOne
    private Board board;

    @Column(unique = true)
    @NotNull(message = "Username is required.")
    private String username;

    @Column
    @Enumerated(EnumType.STRING)
    private PlayerStatus status;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }
}
