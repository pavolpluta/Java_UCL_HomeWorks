package cz.unicorncollege.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sun.xml.internal.ws.util.StringUtils;
import cz.unicorncollege.bt.model.MeetingCentre;
import cz.unicorncollege.bt.model.MeetingRoom;
import cz.unicorncollege.bt.utils.Choices;
import cz.unicorncollege.bt.utils.FileParser;

public class MeetingController {
    private List<MeetingCentre> meetingCentres;

    /**
     * Method to initialize data from the saved datafile.
     */
    public void init() {

        //TODO: nacist z ulozeneho souboru vsechna meeting centra a vypsat je na obrazovku

       // meetingCentres = FileParser.loadDataFromFile();
        meetingCentres = FileParser.loadDataFromXml();
        System.out.println("MEETING CENTRES:");
        System.out.println("-----------------------");
        try {
            for (MeetingCentre mc : meetingCentres) {
                System.out.println("[" + mc.getName() + ", " + mc.getCode() + "]");
            }
        } catch (NullPointerException e) {
            System.err.println("Problem with initialization. You might have removed Meeting Centre manually without removing depending Meeting Rooms.");
            e.printStackTrace();
        }

        System.out.println();
    }

    /**
     * Method to list all meeting centres to user and give him some options what to do next.
     */

    public void listAllMeetingCentres() {

        //TODO: vypsat data o meeting centrech
        meetingCentres = getMeetingCentres();
        System.out.println();
        try {
            System.out.println("MEETING CENTRES:");
            System.out.println("-----------------------");

            for (MeetingCentre mc : meetingCentres) {
                System.out.println("[" + mc.getName() + ", " + mc.getCode() + "]");
            }
            System.out.println();

            //String chosenOption = Choices.getInput("Choose option (including code after '-', example 1-M01): ");
            // int option = chosenOption.contains("-") ? Integer.parseInt(chosenOption.substring(0, 1)) : Integer.parseInt(chosenOption);
            // String code = chosenOption.contains("-") ? chosenOption.substring(2, chosenOption.length()) : "";

            boolean correctCentre = false;
            String chosenOption = Choices.getInput("Choose Meeting Centre to work with (Enter the code after the 'EBC-MC_'. Example: for EBC-MC_PAR, enter 'PAR'). For return to menu, enter 'EXIT' :");
            if (chosenOption.toLowerCase().equals("exit")) {
                System.out.println();
                return;
            }
            for (MeetingCentre mc : meetingCentres) {
                if (mc.getCode().equals("EBC-MC_" + chosenOption.toUpperCase())) {
                    correctCentre = true;
                }
            }
            String code = chosenOption.toUpperCase();

            List<String> choices = new ArrayList<String>();
            choices.add("Show Details of Meeting Centre with code: " + code);
            choices.add("Edit Meeting Centre with code: " + code);
            choices.add("Delete Meeting Centre with code: " + code);
            choices.add("Choose another meeting centre");
            choices.add("Go Back to Home");

            if (correctCentre) {
                while (true) {
                    switch (Choices.getChoice("", choices)) {
                        case 1:
                            showMeetingCentreDetails(code);
                            break;
                        case 2:
                            editMeetingCentre(code);
                            break;
                        case 3:
                            deleteMeetingCentre(code);
                            listAllMeetingCentres();
                            return;
                        case 4:
                            listAllMeetingCentres();
                        case 5:
                            return;
                    }
                }
            } else {
                System.err.println("Meeting centre with code " + code + " doesn't exist. Try again.");
                System.out.println();
                listAllMeetingCentres();
            }

        } catch (NullPointerException e) {
            System.err.println("Data not found!");
            e.printStackTrace();
        }


    }

    /**
     * NEW
     * Checks whether the input is blank or 'exit'.
     */

    private String inputChecker(String input) {
        if (input.toLowerCase().equals("exit")) {
            return "exit";
        } else if (input.isEmpty()) {
            while (input.isEmpty()) {
                System.err.println("Field cannot remain blank! Try again:");
                input = Choices.getInput("");
                if (input.toLowerCase().equals("exit")) {
                    return "exit";
                }
            }
        }
        return input;
    }

    /**
     * Method to add a new meeting centre.
     */
    public void addMeetingCentre() {
        //TODO: doplnit zadavani vsech dalsich hodnot + naplneni do noveho objektu
        MeetingCentre newCentre = new MeetingCentre();
        List<MeetingRoom> meetingRooms = new ArrayList<>();

        System.out.println("*** IF YOU WISH TO RETURN TO MENU, ENTER 'EXIT' ANYTIME DURING THE MEETING CENTRE CREATION. ***");
        System.out.println();

        //MEETING CENTRES
        String name = Choices.getInput("Enter name of MeetingCentre ('EBC' will be automatically added as prefix) : ");
        name = inputChecker(name);
        if (name.equals("exit")) {
            return;
        }
        String code = Choices.getInput("Enter code of MeetingCentre ('EBC-MC_' will be automatically added as prefix): ");
        code = inputChecker(code);
        if (code.equals("exit")) {
            return;
        }
        String description = Choices.getInput("Enter description of MeetingCentre: ");
        description = inputChecker(description);
        if (description.equals("exit")) {
            return;
        }

        newCentre.setName("EBC " + StringUtils.capitalize(name));
        newCentre.setCode("EBC-MC_" + code.toUpperCase());
        newCentre.setDescription(StringUtils.capitalize(description));

        System.out.println(newCentre.getName());
        System.out.println(newCentre.getCode());
        System.out.println(newCentre.getDescription());


        //ROOMS
        boolean roomAdding = true;

        String roomChoice = Choices.getInput("Do you wish to add Meeting Rooms? [Y/N]");

        while (roomAdding) {
            if (roomChoice.toLowerCase().equals("y") || roomChoice.toLowerCase().equals("yes")) {
                addMeetingRoom(code, newCentre, meetingRooms);

                roomChoice = Choices.getInput("Do you want to add another room? [Y/N]");
                if ((!roomChoice.toLowerCase().equals("y")) && (!roomChoice.toLowerCase().equals("yes"))) {
                    roomAdding = false;
                }
            } else {
                System.out.println("No meeting room will be added.");
                System.out.println();
                newCentre.setMeetingRooms(meetingRooms);
                roomAdding = false;
            }
        }
        meetingCentres.add(newCentre);
    }

    /**
     * NEW
     * Add new meeting room.
     */
    public void addMeetingRoom(String code, MeetingCentre meetingCentre, List<MeetingRoom> meetingRooms) {
        String roomName = Choices.getInput("Enter room name (Example:'1.1 Alpha') :");
        roomName = inputChecker(roomName);
        if (roomName.equals("exit")) {
            return;
        }
        String roomCode = Choices.getInput("Enter room code (Example: '1_1') :");
        roomCode = inputChecker(roomCode);
        if (roomCode.equals("exit")) {
            return;
        }
        String roomDescription = Choices.getInput("Enter room description:");
        roomDescription = inputChecker(roomDescription);
        if (roomDescription.equals("exit")) {
            return;
        }
        String roomCapacity = Choices.getInput("Capacity (enter only positive whole number!):");
        if (!roomCapacity.matches("[1-9]+[0-9]*")) {
            while (!roomCapacity.matches("[1-9]+[0-9]*")) {
                System.err.println("Try again!");
                roomCapacity = Choices.getInput("");
            }
        }
        boolean hasVideo;
        String tempVideo = Choices.getInput("Has video conference? [Y/N]");
        if (tempVideo.toLowerCase().equals("y") || tempVideo.toLowerCase().equals("yes")) {
            hasVideo = true;
        } else {
            hasVideo = false;
        }

        MeetingRoom room = new MeetingRoom();
        room.setName(roomName);
        room.setCode("EBC-" + code.toUpperCase() + "-MR:" + roomCode.toUpperCase());
        room.setDescription(StringUtils.capitalize(roomDescription));
        room.setCapacity(Integer.parseInt(roomCapacity));
        room.setHasVideoConference(hasVideo);
        room.setMeetingCentre(meetingCentre);

        System.out.println(room.getName());
        System.out.println(room.getCode());
        System.out.println(room.getDescription());
        System.out.println(room.getCapacity());
        System.out.println(room.isHasVideoConference());
        System.out.println(room.getMeetingCentre().getName());

        meetingRooms.add(room);
        meetingCentre.setMeetingRooms(meetingRooms);
    }

    /**
     * Method to show meeting centre details by id.
     */
    public void showMeetingCentreDetails(String code) {
        //TODO: doplnit nacteni prislusneho meeting centra a vypsani jeho zakladnich hodnot
        MeetingCentre chosenCentre = new MeetingCentre();
        List<MeetingRoom> meetingRooms = new ArrayList<>();

        for (MeetingCentre mc : meetingCentres) {
            if (mc.getCode().contains(code)) {
                chosenCentre = mc;
            }
        }

        System.out.println("MEETING CENTRE:");
        System.out.println("[" + chosenCentre.getName() + ", " + chosenCentre.getCode() + ", " + chosenCentre.getDescription() + "]");
        System.out.println();

        List<String> choices = new ArrayList<String>();
        choices.add("Show meeting rooms");
        choices.add("Add meeting room");
        choices.add("Edit/Delete meeting room");
        choices.add("Go Back");

        //TODO: doplnit metody pro obsluhu meeting rooms atd..
        while (true) {
            switch (Choices.getChoice("Choose option:", choices)) {
                case 1:
                    for (MeetingRoom mr : chosenCentre.getMeetingRooms()) {
                        System.out.println("[" + mr.getName() + ", " + mr.getCode() + ", " + mr.getDescription() + ", Kapacita: " + mr.getCapacity() + ", Videokonferencia: " + mr.isHasVideoConference() + "]");
                    }
                    System.out.println();
                    break;
                case 2:
                    addMeetingRoom(code, chosenCentre, chosenCentre.getMeetingRooms());
                    System.out.println();
                    break;
                case 3:
                    if (chosenCentre.getMeetingRooms().size() > 0){
                        editMeetingRoom(code,chosenCentre);
                    }else{
                        System.out.println("Meeting centre doesn't contain any meeting rooms.");
                    }
                    System.out.println();
                    break;
                case 4:
                    return;
            }
        }
    }

    /**
     * NEW
     * Method to edit or delete the meeting room.
     */

    private void editMeetingRoom(String code, MeetingCentre meetingCentre){
        MeetingRoom room = new MeetingRoom();
        for (MeetingRoom mr : meetingCentre.getMeetingRooms()) {
            System.out.println("[" + mr.getName() + ", " + mr.getCode() + ", " + mr.getDescription() + ", Kapacita: " + mr.getCapacity() + ", Videokonferencia: " + mr.isHasVideoConference() + "]");
        }
        System.out.println();
        String roomCode =  Choices.getInput("Choose meeting room to edit (enter the last part of the code - Example: for EBC-PAR-MR:0_1, enter '0_1'): ");


        for (MeetingRoom mr :meetingCentre.getMeetingRooms()){
            if (mr.getCode().equals("EBC-" + code.toUpperCase() + "-MR:" + roomCode.toUpperCase())){
                room = mr;
            }
        }

        if(room.getName() == null){
            System.out.println();
            System.err.println("ROOM DOES NOT EXIST.");
            return;
        }

        String newValue;
        String input = Choices.getInput("What would you like to change? Enter one of the following:[NAME|CODE|DESCRIPTION|CAPACITY|VIDEO] or DELETE for removing the room: ");
            switch (input.toLowerCase()) {
                case "name":
                    newValue = Choices.getInput("Enter new name: ");
                    room.setName(newValue);
                    break;
                case "code":
                    newValue = Choices.getInput("Enter new code: ");
                    room.setCode("EBC-" + code.toUpperCase() + "-MR:" + newValue);
                    break;
                case "description":
                    newValue = Choices.getInput("Enter new description: ");
                    room.setDescription(StringUtils.capitalize(newValue));
                    break;
                case "capacity":
                    newValue = Choices.getInput("Enter new capacity (Only positive whole numbers): ");
                    if (!newValue.matches("[1-9]+[0-9]*")) {
                        while (!newValue.matches("[1-9]+[0-9]*")) {
                            System.err.println("Try again!");
                            newValue = Choices.getInput("");
                        }
                    }
                    room.setCapacity(Integer.parseInt(newValue));
                    break;
                case "video":
                    boolean hasVideo;
                    newValue = Choices.getInput("Has video conference? [Y/N]");
                    if (newValue.toLowerCase().equals("y") || newValue.toLowerCase().equals("yes")) {
                        hasVideo = true;
                    } else {
                        hasVideo = false;
                    }
                    room.setHasVideoConference(hasVideo);
                    break;
                case "delete":
                    newValue = Choices.getInput("Do you really want to delete room " + room.getName() + "? [Y/N]");
                    if (newValue.toLowerCase().equals("y") || newValue.toLowerCase().equals("yes")){
                        meetingCentre.getMeetingRooms().remove(room);
                        System.out.println(room.getName() + " was removed.");
                    }else{
                        System.out.println(room.getName() + " was not removed.");
                    }
            }
    }

    /**
     * Method to edit meeting centre data by id.
     */
    public void editMeetingCentre(String code) {
        //TODO: doplneni editace, bud vsech polozek s moznosti preskoceni nebo vyber jednotlive polozky
        MeetingCentre chosenCentre = new MeetingCentre();
        for (MeetingCentre mc : meetingCentres) {
            if (mc.getCode().contains(code)) {
                chosenCentre = mc;
            }
        }
        System.out.println("MEETING CENTRE:");
        System.out.println("[" + chosenCentre.getName() + ", " + chosenCentre.getCode() + ", " + chosenCentre.getDescription() + "]");
        System.out.println();
        String newValue;
        String input = Choices.getInput("What would you like to change? Enter one of the following: [NAME|CODE|DESCRIPTION]: ");
        switch (input.toLowerCase()) {
            case "name":
                newValue = Choices.getInput("Enter new name: ");
                chosenCentre.setName(newValue);
                break;
            case "code":
                newValue = Choices.getInput("Enter new code: ");
                chosenCentre.setCode(code.toUpperCase());
                break;
            case "description":
                newValue = Choices.getInput("Enter new description: ");
                chosenCentre.setDescription(newValue);
                break;
        }
    }

    /**
     * Method to delete by id
     */
    private void deleteMeetingCentre(String code) {
        //TODO: doplnit vymazani meeting centra a jeho mistnosti a vypsani potvrzeni o smazani
        MeetingCentre chosenCentre = new MeetingCentre();
        for (MeetingCentre mc : meetingCentres) {
            if (mc.getCode().contains(code)) {
                chosenCentre = mc;
            }
        }

       String confirmation = Choices.getInput("Do you really want to remove Meeting centre " + chosenCentre.getName() + "? [Y/N]");
        if (confirmation.toLowerCase().equals("y") || confirmation.toLowerCase().equals("yes")){
            meetingCentres.remove(chosenCentre);
            System.out.println();
            System.out.println("Meeting centre " + chosenCentre.getName() + " was removed with all its rooms.");
            System.out.println();

        }else{
            System.out.println();
            System.out.println(chosenCentre.getName() + " was not removed.");
        }

    }

    /**
     * Method to get all data to save in string format
     *
     * @return
     */
    public String toSaveString() {
        return null;
    }


    public List<MeetingCentre> getMeetingCentres() {
        return meetingCentres;
    }

    public void setMeetingCentres(List<MeetingCentre> meetingCentres) {
        this.meetingCentres = meetingCentres;
    }
}
