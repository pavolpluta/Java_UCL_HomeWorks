package cz.unicorncollege.controller;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.unicorncollege.bt.utils.Choices;
import cz.unicorncollege.bt.utils.FileParser;

/**
 * Main controller class.
 * Contains methods to communicate with user and methods to work with files.
 *
 * @author UCL
 */
public class MainController {
	private MeetingController controll;
	private ReservationController controllReservation;
	
	/**
	 * Constructor of main class.
	 */
	public MainController() {
		controll = new MeetingController();
		controll.init();

		controllReservation = new ReservationController(controll);
	}

	
    /**
	 * Main method, which runs the whole application.
	 *
	 * @param argv String[]
	 */
	public static void main(String[] argv) throws Exception {
		MainController instance = new MainController();
		instance.run();
	}

	/**
	 * Method which shows the main menu and end after user chooses Exit.
	 */
	private void run() {
		List<String> choices = new ArrayList<String>();
		choices.add("List all Meeting Centres");
		choices.add("Add new Meeting Centre");
		choices.add("Reservations");
		choices.add("Import Data");
		choices.add("Export Data to JSON");
		choices.add("Exit and Save (XML)");
		choices.add("Exit");

		while (true) {
			switch (Choices.getChoice("Select an option: ", choices)) {
			case 1:
				controll.listAllMeetingCentres();
				break;
			case 2:
				controll.addMeetingCentre();
				break;
			case 3:
				controllReservation.showReservationMenu();
				break;
			case 4:
				controll.setMeetingCentres(FileParser.importData());
				//controll.listAllMeetingCentres();  not necessary
				break;
			case 5:

				FileParser.exportDataToJSON(controll.getMeetingCentres());
				break;
			case 6:
				if(controll.getMeetingCentres() != null){
					FileParser.saveDataToXml(controll.getMeetingCentres());
				}else{
					System.out.println("No changes were made. Exiting...");
				}
				return;
			case 7:
				return;
			}
		}
	}
}
