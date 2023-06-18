package hac.classes;

import javax.swing.text.html.Option;
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

    public final static Map <Integer,HashMap<Integer,Integer>> options = new HashMap<Integer, HashMap<Integer,Integer>>(){{
        put(1,new HashMap<Integer, Integer>(){{
            put (5,1);
            put(4,1);
            put(3,2);
            put(2,1);
        }});
        put(2,new HashMap<Integer, Integer>(){{
            put (4,1);
            put(3,2);
            put(2,3);
            put(1,4);
        }});
    }};
}
