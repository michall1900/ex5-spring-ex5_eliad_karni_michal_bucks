package hac.embeddables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hac.classes.customErrors.DbError;

import java.util.ArrayList;
import java.util.HashMap;


public class UpdateObject {
    private HashMap<String, String> attackDetails;

    private ArrayList<HashMap<String, String>> boardChanges;

    public UpdateObject() {
    }

    public UpdateObject(HashMap<String, String> attackDetails, ArrayList<HashMap<String, String>> boardChanges) {
        this.attackDetails = attackDetails;
        this.boardChanges = boardChanges;
    }

    public HashMap<String, String> getAttackDetails() {
        return attackDetails;
    }

    public void setAttackDetails(HashMap<String, String> attackDetails) {
        this.attackDetails = attackDetails;
    }

    public ArrayList<HashMap<String, String>> getBoardChanges() {
        return boardChanges;
    }

    public void setBoardChanges(ArrayList<HashMap<String, String>> boardChanges) {
        this.boardChanges = boardChanges;
    }

    public String convertObjectToString(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        }
        catch (JsonProcessingException e){
            throw new DbError();
        }
    }

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
