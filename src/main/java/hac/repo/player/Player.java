package hac.repo.player;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import hac.repo.board.Board;
import hac.repo.room.Room;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.concurrent.atomic.AtomicReference;


import java.util.HashMap;

@Entity
public class Player {

    public enum PlayerStatus{
        NOT_READY,
        READY,
        ON_GAME,
        WIN,
        LOSE
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;


    @ManyToOne
    @JoinColumn(name="room_id", nullable = false)
    @NotNull
    private Room room;


    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private Board board;

    @Column(unique = true)
    @NotNull(message = "Username is mandatory")
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
        return this.room;
    }

    public void setRoom(Room room) {
//        if (room == null) {
//            if (this.room != null) {
//                this.room.getPlayers().remove(this);
//            }
//        } else if{
//            room.getPlayers().add(this);
//        }
        this.room = room;
    }

    public Board getBoard() {
        return this.board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PlayerStatus getStatus() {
        return this.status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Player{" + "id = " + getId() + ", name = " + getUsername() + ", status = " + getStatus() +
                getRoom().getId() + "}";
    }

    public HashMap<String, String> getInfo(){
        HashMap<String, String> map = new HashMap<>();
        map.put("name", this.username);
        map.put("status", this.status.toString());
        return map;
    }
}
