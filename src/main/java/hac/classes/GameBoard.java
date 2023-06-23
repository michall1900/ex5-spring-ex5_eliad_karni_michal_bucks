package hac.classes;

import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@Component
public class GameBoard implements Serializable {

    public enum Options{
        BASIC,
        ALTERNATIVE
    }
    public final static int SIZE = 10;
    public final static Map<String, String> imgType = new HashMap<String, String>(){{
        put("noShip", "noShip.png");
        put("submarineCell", "submarineCell.png");
        put("explodeShip", "explodeShip.jpg");
        put("empty", "empty.png");
    }};

    public final static Map <Integer,HashMap<Integer,Integer>> options = new HashMap<Integer, HashMap<Integer,Integer>>(){{
        put(Options.BASIC.ordinal(),new HashMap<Integer, Integer>(){{
            put (5,1);
            put(4,1);
            put(3,2);
            put(2,1);
        }});
        put(Options.ALTERNATIVE.ordinal(),new HashMap<Integer, Integer>(){{
            put (4,1);
            put(3,2);
            put(2,3);
            put(1,4);
        }});
    }};

    GameBoard(){

    }
}
