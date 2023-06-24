package hac.repo.player;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import hac.repo.board.Board;
import hac.repo.room.Room;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashMap;

@Entity
public class Player {

    public enum PlayerStatus{
        NOT_READY,
        READY,
        ON_GAME
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;

//    @OneToOne
//    private Board board;

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

//    public Board getBoard() {
//        return board;
//    }

//    public void setBoard(Board board) {
//        this.board = board;
//    }

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

    public HashMap<String, String> getInfo(){
        HashMap map = new HashMap<String,String>();
        map.put("name", this.username);
        map.put("status", this.status);
        return map;
    }
}
