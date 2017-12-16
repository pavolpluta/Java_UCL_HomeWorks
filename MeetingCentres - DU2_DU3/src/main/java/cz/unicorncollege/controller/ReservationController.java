package cz.unicorncollege.controller;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.xml.internal.ws.util.StringUtils;
import cz.unicorncollege.bt.model.MeetingCentre;
import cz.unicorncollege.bt.model.MeetingRoom;
import cz.unicorncollege.bt.model.Reservation;
import cz.unicorncollege.bt.utils.Choices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class ReservationController {
    private MeetingController meetingController;
    private MeetingCentre actualMeetingCentre;
    private MeetingRoom actualMeetingRoom;
    private Date actualDate;

    /**
     * Constructor for ReservationController class
     *
     * @param meetingController loaded data of centers and its rooms
     */
    public ReservationController(MeetingController meetingController) {
        this.meetingController = meetingController;
        //Custom date set for 'actualDate' field
        this.actualDate = new GregorianCalendar(2017, Calendar.DECEMBER, 14).getTime();

    }

    /**
     * Method to show options for reservations
     */
    public void showReservationMenu() {

        actualMeetingRoom = null;
        actualMeetingCentre = null;
        // let them choose one of the loaded meeting centres
        for (MeetingCentre mc : meetingController.getMeetingCentres()) {
            System.out.println("[" + mc.getName() + ", " + mc.getCode() + "]");
        }
        System.out.println();

        String chosenOption = Choices.getInput("Choose the Meeting Centre (Enter the code after the 'EBC-MC_'. Example: for EBC-MC_PAR, enter 'PAR'). For return to menu, enter 'EXIT' :");
        if (chosenOption.toLowerCase().equals("exit")) {
            System.out.println();
            return;
        }
        // get the chosen meeting center
        for (MeetingCentre mc : meetingController.getMeetingCentres()) {
            if (mc.getCode().equals("EBC-MC_" + chosenOption.toUpperCase())) {
                actualMeetingCentre = mc;
            }
        }
        String code = chosenOption.toUpperCase();

        if (actualMeetingCentre == null) {
            System.err.println("Meeting centre doesn't exist. Try again.");
            System.out.println();
            showReservationMenu();
            return;
        }


        if (actualMeetingCentre.getMeetingRooms().isEmpty()) {
            System.out.println();
            System.out.println("--- Rooms not found. Returning to menu. ---");
            System.out.println();
            return;
        }

        // display rooms from actual meeting center
        for (MeetingRoom room : actualMeetingCentre.getMeetingRooms()) {
            System.out.println("[" + room.getName() + ", " + room.getCode() + "]");
        }


        System.out.println();
        chosenOption = Choices.getInput("Choose the room to create reservation (enter the last part of the code - Example: for EBC-PAR-MR:0_1, enter '0_1') or EXIT: ");

        for (MeetingRoom room : actualMeetingCentre.getMeetingRooms()) {
            if (room.getCode().equals("EBC-" + code.toUpperCase() + "-MR:" + chosenOption)) {
                actualMeetingRoom = room;
            }
        }

        if (chosenOption.toLowerCase().equals("exit")) {
            System.out.println();
            return;
        }
        if (actualMeetingRoom == null) {
            System.err.println("No room with code " + chosenOption + " found.");
            showReservationMenu();
            System.out.println();
            return;
        }

        getItemsToShow();
    }

    private void editReservation() {
        // TODO list reservation as choices, after chosen reservation edit all
        // relevant attributes
        List<Reservation> reservations = actualMeetingRoom.getReservations().stream().filter(reservation -> reservation.getDate().equals(actualDate)).collect(Collectors.toList());
        for (Reservation res : reservations) {
            if (res.getDate().equals(actualDate)) {
                System.out.println(res.toString());
            }
        }
        System.out.println();

        Reservation currentReservation = new Reservation();
        boolean swap = false;
        String input = Choices.getInput("Select a reservation to edit (by ID):");
        if (input.matches("[0-9]+[0-9]*")) {
            for (Reservation res : reservations) {
                if (res.getiD() == Integer.parseInt(input)) {
                    swap = true;
                    currentReservation = res;
                    actualMeetingRoom.getReservations().remove(currentReservation);
                    break;
                }
            }
        }else{
            System.err.println("Invalid input.");
            return;
        }
        if (!swap){
            System.err.println("Invalid ID");
            return;
        }

        boolean correct;
        input = Choices.getInput("What would you like to edit? Enter one of the following: [TIME/PERSONS/CUSTOMER/VIDEO/NOTE]");
            switch (input.toLowerCase()){
                case "time":
                    setTimes(currentReservation);
                    break;
                case "persons":
                    input = Choices.getInput("Enter expected person count (1 - " + actualMeetingRoom.getCapacity() + "):");
                    correct = false;

                    while (!correct) {
                        if (input.matches("[1-9]+[0-9]*")) {
                            if (Integer.parseInt(input) > 0 && Integer.parseInt(input) <= actualMeetingRoom.getCapacity()) {
                                currentReservation.setExpectedPersonCount(Integer.parseInt(input));
                                correct = true;
                            } else {
                                System.err.println("Invalid number. Try again:");
                                input = Choices.getInput("");
                            }
                        } else {
                            System.err.println("Invalid number. Try again:");
                            input = Choices.getInput("");
                        }
                    }
                    break;
                case "customer":
                    input = Choices.getInput("Enter the name of the customer: ");

                    correct = false;

                    while (!correct) {
                        if (input.length() < 2 || input.length() > 100) {
                            System.err.println("String length is not valid. Try again:");
                            input = Choices.getInput("");
                        } else {
                            currentReservation.setCustomer(StringUtils.capitalize(input));
                            correct = true;
                        }
                    }
                    break;
                case "video":
                    if (actualMeetingRoom.isHasVideoConference()) {
                        input = Choices.getInput("Do you need video conference? [Y/N]?");
                        if (input.toLowerCase().equals("y") || input.toLowerCase().equals("yes")) {
                            currentReservation.setNeedVideoConference(true);
                        } else {
                            currentReservation.setNeedVideoConference(false);
                        }
                    } else {
                        currentReservation.setNeedVideoConference(false);
                    }
                    break;
                case "note":
                    input = Choices.getInput("Enter the note: ");
                    correct = false;
                    while (!correct) {
                        if (input.length() > 300) {
                            System.err.println("String length is not valid. Try again");
                            input = Choices.getInput("");
                        } else {
                            currentReservation.setNote(input);
                            correct = true;
                        }
                    }
                    break;
            }

            actualMeetingRoom.getReservations().add(currentReservation);

    }

    private void addNewReservation() {
        // TODO enter data one by one, add new reservation object to the actual
        // room, then inform about successful adding
        try {
            Reservation reservation = new Reservation();
            reservation.setMeetingRoom(actualMeetingRoom);

            String input = Choices.getInput("Choose a date (format dd.MM.yyyy):");

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            sdf.setLenient(false);
            Date date = sdf.parse(input);
            reservation.setDate(date);

//          SETTING HOURS TO DATE
//            Calendar cl = Calendar.getInstance();
//            cl.setTime(date);
//            String time = "15:30";
//            cl.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0,2)));
//            cl.set(Calendar.MINUTE, Integer.parseInt(time.substring(3)));
//            Date d = cl.getTime();
//            System.out.println(d);

            setTimes(reservation);

            input = Choices.getInput("Enter expected person count (1 - " + actualMeetingRoom.getCapacity() + "):");
            boolean correct = false;

            while (!correct) {
                if (input.matches("[1-9]+[0-9]*")) {
                    if (Integer.parseInt(input) > 0 && Integer.parseInt(input) <= actualMeetingRoom.getCapacity()) {
                        reservation.setExpectedPersonCount(Integer.parseInt(input));
                        correct = true;
                    } else {
                        System.err.println("Invalid number. Try again:");
                        input = Choices.getInput("");
                    }
                } else {
                    System.err.println("Invalid number. Try again:");
                    input = Choices.getInput("");
                }
            }

            input = Choices.getInput("Enter the name of the customer: ");

            correct = false;

            while (!correct) {
                if (input.length() < 2 || input.length() > 100) {
                    System.err.println("String length is not valid. Try again:");
                    input = Choices.getInput("");
                } else {
                    reservation.setCustomer(StringUtils.capitalize(input));
                    correct = true;
                }
            }

            if (actualMeetingRoom.isHasVideoConference()) {
                input = Choices.getInput("Do you need video conference? [Y/N]?");
                if (input.toLowerCase().equals("y") || input.toLowerCase().equals("yes")) {
                    reservation.setNeedVideoConference(true);
                } else {
                    reservation.setNeedVideoConference(false);
                }
            } else {
                reservation.setNeedVideoConference(false);
            }

            input = Choices.getInput("Enter the note: ");
            correct = false;
            while (!correct) {
                if (input.length() > 300) {
                    System.err.println("String length is not valid. Try again");
                    input = Choices.getInput("");
                } else {
                    reservation.setNote(input);
                    correct = true;
                }
            }

            List<Reservation> reservations = new ArrayList<>();
            if (actualMeetingRoom.getReservations() != null) {
                reservations = actualMeetingRoom.getReservations();
            }

            reservations.add(reservation);
            actualMeetingRoom.setReservations(reservations);
            System.out.println("Reservation was successfully created.");

        } catch (ParseException | DateTimeParseException e) {
            //  System.err.println(e);
            //  System.err.println("Try again.");
            e.printStackTrace();
        }
    }

    private static boolean isOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * NEW function
     * Set times for reservations
     * @param reservation
     */

    private void setTimes( Reservation reservation){
        String input = Choices.getInput("Select time from (format hh:mm):");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        LocalTime timeFrom = LocalTime.parse(input, formatter);

        String input2 = Choices.getInput("Select time to (format hh:mm):");
        LocalTime timeTo = LocalTime.parse(input2, formatter);
        boolean correct = false;

        while (!correct) {
            if (timeFrom.isBefore(timeTo)) {
                if (actualMeetingRoom.getReservations().size() > 0) {
                    boolean overlap = false;
                    for (Reservation res : actualMeetingRoom.getReservations()) {
                        if (res.getDate().equals(reservation.getDate()) &&
                                isOverlapping(LocalTime.parse(res.getTimeFrom()), LocalTime.parse(res.getTimeTo()), timeFrom, timeTo)) {
                            System.out.println("**************");
                            System.out.println(timeFrom);
                            System.out.println(timeTo);
                            System.out.println(LocalTime.parse(res.getTimeFrom()));
                            System.out.println(LocalTime.parse(res.getTimeTo()));
                            System.out.println("**************");
                            overlap = true;
                            System.out.println();
                            System.err.println("Reservation time is overlapping other reservation. Please choose different times.");
                            System.out.println();
                            input = Choices.getInput("Time from:");
                            input2 = Choices.getInput("Time to:");

                            timeFrom = LocalTime.parse(input, formatter);
                            timeTo = LocalTime.parse(input2, formatter);
                            break;
                        }
                    }
                    if (!overlap) {
                        //na tuto situaciu som zbytocne nevymyslal metodu, aj ked sa to duplikuje...
                        if (input.length() == 4) {
                            input = "0" + input;
                        }
                        if (input2.length() == 4) {
                            input2 = "0" + input2;
                        }
                        reservation.setTimeFrom(input);
                        reservation.setTimeTo(input2);
                        correct = true;
                    }
                } else {
                    if (input.length() == 4) {
                        input = "0" + input;
                    }
                    if (input2.length() == 4) {
                        input2 = "0" + input2;
                    }
                    reservation.setTimeFrom(input);
                    reservation.setTimeTo(input2);
                    correct = true;

                }
            } else {
                System.out.println();
                System.err.println("Times are not valid. Change them.");
                System.out.println();
                input = Choices.getInput("Time from:");
                input2 = Choices.getInput("Time to:");

                timeFrom = LocalTime.parse(input, formatter);
                timeTo = LocalTime.parse(input2, formatter);
            }
        }
    }

    private void deleteReservation() {
        // TODO list all reservations as choices and let enter item for
        // deletion, delete it and inform about successful deletion
        List<Reservation> reservations = actualMeetingRoom.getReservations().stream().filter(reservation -> reservation.getDate().equals(actualDate)).collect(Collectors.toList());

        for (Reservation res : reservations) {
            if (res.getDate().equals(actualDate)) {
                System.out.println(res.toString());
            }
        }
        System.out.println();
        Reservation currentReservation;
        boolean swap = false;
        String input = Choices.getInput("Choose a reservation to delete (by ID):");
        if (input.matches("[0-9]+[0-9]*")) {
            for (Reservation res : reservations) {
                if (res.getiD() == Integer.parseInt(input)) {
                    swap = true;
                    currentReservation = res;
                    input = Choices.getInput("Are you sure?[Y/N]");
                    if (input.toLowerCase().equals("y") || (input.toLowerCase().equals("yes"))) {
                        actualMeetingRoom.getReservations().remove(currentReservation);
                        System.out.println();
                        System.out.println("*** RESERVATION DELETED. ***");
                        System.out.println();
                        break;
                    } else {
                        System.out.println("Reservation will not be deleted.");
                        break;
                    }
                }
            }
        } else {
            System.out.println();
            System.err.println("Invalid input.");
            System.out.println();
        }
        if (!swap) {
            System.err.println("Invalid ID");
        }

    }

    private void changeDate() {
        // TODO let them enter new date in format YYYY-MM-dd, change the actual
        // date, list actual reservations on this date and menu by
        try {
            String input = Choices.getInput("Enter new date (format dd.MM.yyyy): ");
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            sdf.setLenient(false);
            actualDate = sdf.parse(input);
            //  getItemsToShow();

        } catch (ParseException e) {
            System.err.println(e);
        }

    }

    private void getItemsToShow() {
        //CUSTOM DATE
        //       Date myDate = new GregorianCalendar(2017, Calendar.DECEMBER,14).getTime();
//        System.out.println(myDate);

        List<String> choices = new ArrayList<String>();
        choices.add("Edit Reservations");
        choices.add("Add New Reservation");
        choices.add("Delete Reservation");
        choices.add("Change Date");
        choices.add("Exit");

        while (true) {
            listReservationsByDate(actualDate); //nastavil som staticky datum
            //Sort by date and time from
//            actualMeetingRoom.getReservations().sort(Comparator.comparing(Reservation::getDate).thenComparing(Reservation::getTimeFrom));
//            System.out.println("***");
//            if (actualMeetingRoom.getReservations() != null){
//                for (Reservation res : actualMeetingRoom.getReservations()) {
//                    System.out.println(res.toString());
//                }
//            }
//            System.out.println("***");

            switch (Choices.getChoice("Select an option: ", choices)) {
                case 1:
                    editReservation();
                    break;
                case 2:
                    addNewReservation();
                    break;
                case 3:
                    deleteReservation();
                    break;
                case 4:
                    changeDate();
                    break;
                case 5:
                    return;
            }
        }
    }

    private void listReservationsByDate(Date date) {
        // list reservations
        List<Reservation> list = actualMeetingRoom.getSortedReservationsByDate(date);
        list.sort(Comparator.comparing(Reservation::getDate).thenComparing(Reservation::getTimeFrom));
        if (list != null && list.size() > 0) {
            System.out.println("");
            System.out.println("Reservations for " + getFormattedDate());
            for (Reservation reserv : list) {
                System.out.println(reserv.toString());
            }
            System.out.println("");
        } else {
            System.out.println("");
            System.out.println("There are no reservation for " + getFormattedDate());
            System.out.println("");
        }
    }

    /**
     * Method to get formatted actual date
     *
     * @return
     */
    private String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        return sdf.format(actualDate);
    }

    /**
     * Method to get actual name of place - meeting center and room
     *
     * @return
     */
    private String getCentreAndRoomNames() {
        return "MC: " + actualMeetingCentre.getName() + " , MR: " + actualMeetingRoom.getName();
    }

    /**
     * Method to get actual state - MC, MR, Date
     *
     * @return
     */
    private String getActualData() {
        return getCentreAndRoomNames() + ", Date: " + getFormattedDate();
    }
}
