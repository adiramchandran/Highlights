import com.google.gson.stream.JsonReader;
import com.google.gson.Gson;
import com.sun.xml.internal.ws.util.StringUtils;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by adarshr2 on 2/12/17.
 * File being parsed: siebel.json
 *
 *
 */

public class Main {

    public static Layout layout = new Layout(); //Layout object that holds all Json info
    public static int roomIndex = 0; //will tell index of room array that holds info of current location
    public static String chosenRoom = ""; //returned room after user chooses direction
    public static String prevRoom = ""; //holds previous room so that floor-validating check can occur
    public static final String INVALID = "INVALID ENTRY, TRY AGAIN"; //constant in case user inputs invalid entry
    public static final String EXIT = "Exit"; //constant for exiting application

    /**
     * parses all info into Layout object
     * @throws IOException
     */
    public static void parseJson() throws IOException{
        try {
            FileReader reader = new FileReader("siebel.json");
            JsonReader jsonReader = new JsonReader(reader);
            Gson gson = new Gson();
            layout = gson.fromJson(jsonReader, Layout.class); //loads full 1st page of movies into collection of movies
        }
        catch(IOException IOE){
            System.out.println("IO Exception Found. -parseJson");
            System.exit(0);
        }
    }

    /**
     * returns a capitalized version of whatever string is passed into the parameter
     * helps to put the user-inputted direction into Json format
     * @param direction
     * @return capDirection
     */
    public static String capitalizeStr(String direction){
        String capDirection = StringUtils.capitalize(direction.toLowerCase());
        return capDirection;
    }

    /**
     * refactored code that offers user options for directions and returns name of room chosen based on direction given
     * @param chosenDir - user input of direction
     * @return chosenRoom
     */
    public static String nextRoom(String chosenDir){

        //check for roomIndex in bounds
        if (roomIndex < 0 || roomIndex > layout.getRooms().length){
            return INVALID;
        }

        //manipulate user input to match Json format
        chosenDir = capitalizeStr(chosenDir);

        //check if user wants to exit
        if (chosenDir.equals(EXIT)){
            return EXIT;
        }

        String chosenRoom = ""; //string of room user will choose to go to

        for(Direction direction : layout.getRooms()[roomIndex].getDirections()) {

            //tried using switch -- cases require constants, not elements from for loops
            if (chosenDir.equals(direction.getDirection())){
                chosenRoom = direction.getRoom();
                return chosenRoom;
            }
        }

        return INVALID;
    }

    /**
     * used to see where current location is
     * @return description of current room
     */
    public static String youAreHere(){
        //check for roomIndex in bounds
        if (roomIndex < 0 || roomIndex > layout.getRooms().length){
            return INVALID;
        }

        return layout.getRooms()[roomIndex].getDescription();
    }

    /**
     * runs through current room's direction array to check if there is a way to the previous room
     * @return true or false based on whether map is logical or not
     */
    public static Boolean floorPlanValidator(){

        //iterate through directions array to check for pathway from current chosen room to previous room
        for (Direction direction : layout.getRooms()[roomIndex].getDirections()){
            if (prevRoom.equals(direction.getRoom())){
                return true;
            }
        }

        return false;
    }

    public static void main(String [] args) throws IOException{
        try {
            Scanner sc = new Scanner(System.in);

            //load all Json info into Layout object
            parseJson();

            //Welcome user to navigation application
            System.out.println("Welcome to Gogle maps (avoiding that copyright suit)! Please type one of the offered directions" +
                    " to navigate through Siebel Center. Enter 'exit' to leave the game");

            //Display initial room
            System.out.println(youAreHere());

            while (true){
                //load room-to-be-updated into prevRoom
                prevRoom = layout.getRooms()[roomIndex].getName();

                //Tell user what direction(s) are possible
                System.out.print("From here you can go: ");
                for (int dirCounter = 0; dirCounter < layout.getRooms()[roomIndex].getDirections().length; dirCounter++){

                    //check if last direction
                    if ((dirCounter + 1 ) == layout.getRooms()[roomIndex].getDirections().length){
                        System.out.println(layout.getRooms()[roomIndex].getDirections()[dirCounter].getDirection());
                        break;
                    }

                    System.out.print(layout.getRooms()[roomIndex].getDirections()[dirCounter].getDirection() + ", ");
                }

                String chosenDir = sc.next();

                //prompt for next room
                chosenRoom = nextRoom(chosenDir);

                if (chosenRoom.equals(INVALID)) {
                    System.out.println(INVALID);
                    continue;
                }

                if (chosenRoom.equals(EXIT)){
                    System.out.println("Thanks for using Gogle maps!");
                    System.exit(0);
                }

                //update roomIndex
                //using a for loop is a weakness in the code; would be more cost-efficient to use ArrayList, but Json only gives us arrays
                for (int roomCounter = 0; roomCounter < layout.getRooms().length; roomCounter++){
                    if (layout.getRooms()[roomCounter].getName().equals(chosenRoom)){
                        roomIndex = roomCounter;
                        break;
                    }
                }

                //call floor validating check
                if (!floorPlanValidator()){
                    System.out.println("Map is faulty, you have entered a room you cannot leave; you will be trapped until our next update.");
                    System.exit(0);
                }

                System.out.println(youAreHere());

            }



        }
        catch (IOException IOE){
            System.out.println("IO Exception found. -main method");
            System.exit(0);
        }
    }

}
