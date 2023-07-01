package hac.repo.tile;

import hac.classes.customErrors.InvalidChoiceError;
import hac.repo.subamrine.Submarine;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * This class represents a Tile entity in the game, which can be part of a Submarine or empty.
 * Each Tile has a status that indicates whether it has been hit, missed, is empty, or contains a Submarine.
 * The Tile class is mapped to a database entity via the @Entity annotation.
 * A tile entity can be in one of four states: Miss, Hit, Empty, or Submarine.
 */
@Entity
public class Tile {
    /**The hit error - when it is invalid hit place*/
    static final String HIT_ERROR = "Someone already hit this index";
    /**The status for each tile*/
    public enum TileStatus{
        Miss,
        Hit,
        Empty,
        Submarine
    }


    /**
     * The tile's id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    private long id;

    /**
     * The submarine's name
     */
    @ManyToOne
    @JoinColumn(name = "submarine_id")
    private Submarine submarine;

    /**
     * The status of the tile
     */
    @Enumerated (EnumType.ORDINAL)
    @NotNull
    private TileStatus status;

    /**
     * Getter for the unique identifier of this tile.
     * @return The unique identifier of this tile.
     */
    public long getId() {
        return id;
    }

    /**
     * Setter for the unique identifier of this tile.
     * @param id The unique identifier to be set for this tile.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter for the status of this tile.
     * @return The status of this tile.
     */
    public TileStatus getStatus() {
        return status;
    }
    /**
     * Getter for the submarine associated with this tile.
     * @return The submarine associated with this tile.
     */
    public Submarine getSubmarine() {
        return submarine;
    }
    /**
     * Setter for the submarine to be associated with this tile.
     * @param submarine The submarine to be associated with this tile.
     */
    public void setSubmarine(Submarine submarine) {
        this.submarine = submarine;
    }
    /**
     * Setter for the status of this tile.
     * @param status The status to be set for this tile.
     */
    public void setStatus(TileStatus status) {
        this.status = status;
    }
    /**
     * Default constructor for the Tile class.
     */
    public Tile(){
    }
    /**
     * Constructor for the Tile class with the specified status and associated submarine.
     * @param status The status of the tile.
     * @param submarine The submarine to be associated with the tile.
     */
    public Tile(TileStatus status, Submarine submarine){
        setStatus(status);
    }
    /**
     * Marks this tile as hit and changes its status accordingly.
     * If the tile is part of a submarine, it also marks the submarine as hit.
     * @throws  InvalidChoiceError if the tile is not empty or part of a submarine.
     */
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

    /**
     * Changes the status of this tile without changing the status of the submarine (if any).
     * The tile status can be changed to Hit (if it was a Submarine) or Miss (if it was Empty).
     */
    public void setStatusWithoutChangeTheSubmarine(){
        if (status == TileStatus.Submarine){
            setStatus(TileStatus.Hit);
        }
        else if(status == TileStatus.Empty)
            setStatus(TileStatus.Miss);
    }
    /**
     * Provides a string representation of this tile, including its ID and the ID of its associated submarine (if any).
     * @return A string representation of this tile.
     */
    @Override
    public String toString(){
        return "Tile {id = " + getId() + ((submarine!= null)? "submarine's id = "+ submarine.getId(): "")+ "}";
    }
}
