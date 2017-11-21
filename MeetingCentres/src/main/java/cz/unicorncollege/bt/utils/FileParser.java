package cz.unicorncollege.bt.utils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.unicorncollege.bt.model.MeetingCentre;
import cz.unicorncollege.bt.model.MeetingRoom;

public class FileParser {

    /**
	 * Method to import data from the chosen file.
	 */
	public static List<MeetingCentre> importData() {
        //TODO: Nacist data z importovaneho souboru
        List<MeetingCentre> allMeetingCentres = new ArrayList<>();

		String locationFilter = Choices.getInput("Enter path of imported file (usually 'src/main/resources/ImportData.csv'). Enter 'exit' to exit. : ");
        boolean fileFound = false;

        while (!fileFound){
            if (locationFilter.contains("ImportData.csv")){
                allMeetingCentres = loadData(locationFilter);
                fileFound = true;
            }else if (locationFilter.toLowerCase().equals("exit")){
                return allMeetingCentres;
            }else{
                System.err.println("File not found. Try again or enter exit.");
                locationFilter = Choices.getInput("");
            }
        }

        System.out.println();
        System.out.println("**************************************************");
        System.out.println("Data was imported. " + allMeetingCentres.size() + " objects of MeetingCentres were loaded");
        System.out.println("**************************************************");
        System.out.println();

        return allMeetingCentres;
	}

    /**
     * NEW
     * Universal method to load data from 'csv' files (Called from both methods ImportData and LoadDataFromFile).
     */
    private static List<MeetingCentre> loadData(String locationFilter){
        List<MeetingCentre> allMeetingCentres = new ArrayList<>();

        String line;
        List<String> meetingObjectList;
        List<MeetingRoom> allMeetingRooms = new ArrayList<>();
        boolean roomTrigger = false;

        try(BufferedReader r = new BufferedReader(new FileReader(locationFilter))){
            while((line = r.readLine()) != null){
                MeetingCentre centre = new MeetingCentre();
                meetingObjectList = new ArrayList<String>(Arrays.asList(line.split(",")));

                if ((!roomTrigger) && (meetingObjectList.size() > 1)){
                    centre.setName(meetingObjectList.get(0));
                    centre.setCode(meetingObjectList.get(1));
                    centre.setDescription(meetingObjectList.get(2));
                    allMeetingCentres.add(centre);
                }

                if ((roomTrigger) && (meetingObjectList.size() > 1)){
                    MeetingRoom room = new MeetingRoom();
                    room.setName(meetingObjectList.get(0));
                    room.setCode(meetingObjectList.get(1));
                    room.setDescription(meetingObjectList.get(2));
                    room.setCapacity(Integer.parseInt(meetingObjectList.get(3)));
                    if (meetingObjectList.get(4).equals("YES")){
                        room.setHasVideoConference(true);
                    }else{
                        room.setHasVideoConference(false);
                    }
                    for (MeetingCentre mc:allMeetingCentres){
                        if (meetingObjectList.get(5).equals(mc.getCode())){
                            room.setMeetingCentre(mc);
                        }
                    }
                    allMeetingRooms.add(room);
                }

                if(meetingObjectList.get(0).contains("ROOMS")){
                    roomTrigger = true;
                }
            }
        }catch (IOException ex){
            System.err.println("*** Error with loading file. *** ");
            System.err.println(ex);
        }

        for (MeetingCentre mc:allMeetingCentres){
            List<MeetingRoom> temp = new ArrayList<>();
            for(MeetingRoom mr:allMeetingRooms){
                if (mc.getCode().equals(mr.getMeetingCentre().getCode())){
                    temp.add(mr);
                }
            }
            mc.setMeetingRooms(temp);
        }

        return allMeetingCentres;
    }

	/**
	 * Method to save the data to file.
     * EDIT: Changed the method input from String to List
	 */
	public static void saveData(List<MeetingCentre> meetingCentres) {
		//TODO: ulozeni dat do souboru
        List<MeetingRoom> rooms = new ArrayList<>();
        try(BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src/main/resources/SavedData.csv"))))
        {
            String lineSep = ",,,";
            String attrSep = ",";
            wr.write("MEETING_CENTRES" + ",,,,,");
            wr.write(System.lineSeparator());
            for (MeetingCentre mc: meetingCentres){
                wr.write(mc.getName() + attrSep + mc.getCode() + attrSep + mc.getDescription() + lineSep);
                wr.write(System.lineSeparator());
                if (mc.getMeetingRooms() != null ){
                    rooms.addAll(mc.getMeetingRooms());
                }
            }
            wr.write("MEETING_ROOMS" + ",,,,,");
            wr.write(System.lineSeparator());
                for (MeetingRoom mr: rooms){
                    wr.write(mr.getName() + attrSep + mr.getCode() + attrSep + mr.getDescription() + attrSep + mr.getCapacity() + attrSep);
                    if (mr.isHasVideoConference()){
                        wr.write("YES" + attrSep);
                    }else{
                        wr.write("NO" + attrSep);
                    }
                    wr.write(mr.getMeetingCentre().getCode());
                    wr.write(System.lineSeparator());
                }
        }catch (IOException e){
          // System.err.println("Problem with saving data! ");
         //  System.err.println(e);
           return;
        }

		System.out.println();
		
		System.out.println("**************************************************");
		System.out.println("Data was saved correctly.");
		System.out.println("**************************************************");
		
		System.out.println();
	}
	
	/**
	 * Method to load the data from file.
	 * @return
	 */
	public static List<MeetingCentre> loadDataFromFile() {
		//TODO: nacist data ze souboru

        try{
            List<MeetingCentre> allMeetingCentres;
            Path path = Paths.get("src/main/resources/SavedData.csv");
            String location = path.toString();
            allMeetingCentres = loadData(location);

            System.out.println();

            System.out.println("**************************************************");
            System.out.println("Data was loaded correctly.");
            System.out.println("**************************************************");

            System.out.println();

            return allMeetingCentres;
        }catch (Exception e){
            System.err.println("Problem with loading data | " + e);
            return null;
        }

	}
}
