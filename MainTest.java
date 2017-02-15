import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.junit.Before;
import org.junit.Test;
import java.io.FileReader;
import java.io.IOException;
import static org.junit.Assert.*;

/**
 * Created by adarshr2 on 2/14/17.
 */
public class MainTest {

    public Main main = new Main();

    /**
     * loads all Json info into Layout object
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        try {
            FileReader reader = new FileReader("siebel.json");
            JsonReader jsonReader = new JsonReader(reader);
            Gson gson = new Gson();
            main.layout = gson.fromJson(jsonReader, Layout.class); //loads full 1st page of movies into collection of movies
        }
        catch(IOException IOE){
            System.out.println("IO Exception Found. -setUp");
            System.exit(0);
        }
    }

    /**
     * tests that room user enters is correct
     * @throws Exception
     */
    @Test
    public void nextRoom() throws Exception {
        //test 1: check "east" from Matthews; resulting room should be SiebelEntry
        main.roomIndex = 0;
        assertEquals(main.nextRoom("east"),"SiebelEntry");

        //test 2: check "north" from SiebelEntry; resulting room should be SiebelNorthHallway
        main.roomIndex = 1;
        assertEquals(main.nextRoom("noRtH"), "SiebelNorthHallway");

        //test 3: check some invalid entry; resulting should be main.INVALID
        assertEquals(main.nextRoom("Easttt"), main.INVALID);

        //test 4: check exit
        assertEquals(main.nextRoom("exIT"), main.EXIT);

        //test 5: check null, result should be invalid
        assertEquals(main.nextRoom(""), main.INVALID);
    }

    /**
     * tests for correct current location
     * @throws Exception
     */
    @Test
    public void youAreHere() throws Exception {
        //test 1: check roomIndex of 0
        main.roomIndex = 0;
        assertEquals(main.youAreHere(), main.layout.getRooms()[0].getDescription());

        //test 2: check roomIndex of 3
        main.roomIndex = 3;
        assertEquals(main.youAreHere(), main.layout.getRooms()[3].getDescription());

        //test 3: check roomIndex of MIN_VALUE
        main.roomIndex = Integer.MIN_VALUE;
        assertEquals(main.youAreHere(), main.INVALID);

        //test 4: check roomIndex of MAX_VALUE
        main.roomIndex = Integer.MAX_VALUE;
        assertEquals(main.youAreHere(), main.INVALID);

    }

    /**
     * makes sure the map has a pathway to each visited room from current location
     * @throws Exception
     */
    @Test
    public void floorPlanValidator() throws Exception {
        //test 1: check for previous room being Matthews Street and current room being SiebelEntry
        main.prevRoom = "MatthewsStreet";
        main.roomIndex = 1;
        assertTrue(main.floorPlanValidator());

        //test 2: check for previous room being SiebelEntry and current room being SiebelNorthHallway
        main.prevRoom = "SiebelEntry";
        main.roomIndex = 3;
        assertTrue(main.floorPlanValidator());

        //test 3: check for previous room being SiebelBasement and current room being SiebelEastHallway
        main.prevRoom = "SiebelBasement";
        main.roomIndex = 5;
        assertTrue(main.floorPlanValidator());

        //test 4: check for previous room being SiebelEastHallway and current room being SiebelBasement
        //opposite of test 3 - checks that doors to rooms go both ways
        main.prevRoom = "SiebelEastHallway";
        main.roomIndex = 7;
        assertTrue(main.floorPlanValidator());
    }


}