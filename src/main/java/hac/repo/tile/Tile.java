package hac.repo.tile;

import hac.classes.GameBoard;
import hac.repo.subamrine.Submarine;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Tile {

    public enum TileStatus{
        Miss,
        Hit,
        Empty,
        Submarine
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutorial_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Submarine submarine;

    @Enumerated (EnumType.ORDINAL)
    private TileStatus status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public TileStatus getStatus() {
        return status;
    }

    public void setStatus(TileStatus status) {
        this.status = status;
    }

    public Tile(){
    }

    public Tile(TileStatus status, Submarine submarine){
        setStatus(status);
    }
}
