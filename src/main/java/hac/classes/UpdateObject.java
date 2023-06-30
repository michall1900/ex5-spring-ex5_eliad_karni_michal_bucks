package hac.classes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hac.classes.customErrors.DbError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * An update object includes maps for the update array in room.
 * It looks like that:
 * {attackDetails:{attackerName:___, opponentName:___, row:___, col:___},
 * boardChanges:[{row:___, col:___, status:"Hit"/"Miss"}, {..}, ...]}, ...]
 * Rows and columns need to be 0 &lt;= integer &lt; Board.Size.
 * attackerName is the username of the attacker, opponentName is the attacked player's username.
 */
public class UpdateObject implements Serializable {
    /**Holds key and values as described*/
    private HashMap<String, String> attackDetails;

    /**Holds an array of key and values as described*/
    private ArrayList<HashMap<String, String>> boardChanges;

    /**
     * c-tor
     */
    public UpdateObject() {
    }

    /**
     * Another c-tor receives both members.
     * @param attackDetails {attackerName:___, opponentName:___, row:___, col:___}
     * @param boardChanges [{row:___, col:___, status:"Hit"/"Miss"}, {..}, ...]}, ...]
     */
    public UpdateObject(HashMap<String, String> attackDetails, ArrayList<HashMap<String, String>> boardChanges) {
        this.attackDetails = attackDetails;
        this.boardChanges = boardChanges;
    }

    /**
     *
     * @return attackDetails
     */
    public HashMap<String, String> getAttackDetails() {
        return attackDetails;
    }

    /**
     * @param attackDetails {attackerName:___, opponentName:___, row:___, col:___}
     */
    public void setAttackDetails(HashMap<String, String> attackDetails) {
        this.attackDetails = attackDetails;
    }

    /**
     *
     * @return boardChanges
     */
    public ArrayList<HashMap<String, String>> getBoardChanges() {
        return boardChanges;
    }

    /**
     *
     * @param boardChanges [{row:___, col:___, status:"Hit"/"Miss"}, {..}, ...]}, ...]
     */
    public void setBoardChanges(ArrayList<HashMap<String, String>> boardChanges) {
        this.boardChanges = boardChanges;
    }

    /**
     * To convert this object to a string.
     * @return A string of this object.
     */
    public String convertObjectToString(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        }
        catch (JsonProcessingException e){
            throw new DbError();
        }
    }

    /**
     *
     * @param jsonString the UpdateObject as a string
     * @return UpdateObject
     */
    public static UpdateObject convertStringToObject(String jsonString){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, UpdateObject.class);
        }
        catch (JsonProcessingException e){
            throw new DbError();
        }
    }
}
