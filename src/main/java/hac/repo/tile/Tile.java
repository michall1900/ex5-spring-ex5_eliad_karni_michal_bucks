package hac.repo.tile;

import hac.classes.customErrors.InvalidChoiceError;
import hac.repo.subamrine.Submarine;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Tile {
    static final String HIT_ERROR = "Someone already hit this index";
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

    @ManyToOne
    @JoinColumn(name = "submarine_id")
    private Submarine submarine;

    @Enumerated (EnumType.ORDINAL)
    @NotNull
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

    public void hitTile(){
        if (status==TileStatus.Empty){
            setStatus(TileStatus.Miss);
        }
        else if(status==TileStatus.Submarine){
            setStatus(TileStatus.Hit);
            submarine.hitSubmarine();
        }
        else
            throw new InvalidChoiceError(HIT_ERROR);
    }

    public void setStatusWithoutChangeTheSubmarine(){
        if (status == TileStatus.Submarine){
            setStatus(TileStatus.Hit);
        }
        else if(status == TileStatus.Empty)
            setStatus(TileStatus.Miss);
    }

    @Override
    public String toString(){
        return "Tile {id = " + getId() + ((submarine!= null)? "submarine's id = "+ submarine.getId(): "")+ "}";
    }
}
