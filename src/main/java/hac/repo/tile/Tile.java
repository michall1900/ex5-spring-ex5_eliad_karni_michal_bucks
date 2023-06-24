package hac.repo.tile;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submarine_id")
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

    public Submarine getSubmarine() {
        return submarine;
    }

    public void setSubmarine(Submarine submarine) {
        this.submarine = submarine;
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
