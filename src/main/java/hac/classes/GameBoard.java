package hac.classes;

import java.util.HashMap;
import java.util.Map;



public class GameBoard {
    public final static int SIZE = 10;
    public final static Map<String, String> imgType = new HashMap<String, String>(){{
        put("noShip", "noShip.png");
        put("submarineCell", "submarineCell.png");
        put("explodeShip", "explodeShip.jpg");
        put("empty", "empty.png");
    }};
}
