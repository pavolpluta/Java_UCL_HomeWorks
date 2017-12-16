package cz.unicorncollege.bt.utils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import cz.unicorncollege.bt.model.Reservation;
import cz.unicorncollege.controller.MainController;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cz.unicorncollege.bt.model.MeetingCentre;
import cz.unicorncollege.bt.model.MeetingRoom;
import cz.unicorncollege.controller.MeetingController;
import cz.unicorncollege.controller.ReservationController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.json.JsonArray;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class FileParser {

    /**
     * Method to import data from the chosen file.
     */
    public static List<MeetingCentre> importData() {
        //TODO: Nacist data z importovaneho souboru
        List<MeetingCentre> allMeetingCentres = new ArrayList<>();

        String locationFilter = Choices.getInput("Enter path of imported file (usually 'src/main/resources/ImportData.csv'). Enter 'exit' to exit. : ");
        boolean fileFound = false;

        while (!fileFound) {
            if (locationFilter.contains("ImportData.csv")) {
                allMeetingCentres = loadData(locationFilter);
                fileFound = true;
            } else if (locationFilter.toLowerCase().equals("exit")) {
                return allMeetingCentres;
            } else {
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
    private static List<MeetingCentre> loadData(String locationFilter) {
        List<MeetingCentre> allMeetingCentres = new ArrayList<>();

        String line;
        List<String> meetingObjectList;
        List<MeetingRoom> allMeetingRooms = new ArrayList<>();
        boolean roomTrigger = false;

        try (BufferedReader r = new BufferedReader(new FileReader(locationFilter))) {
            while ((line = r.readLine()) != null) {
                MeetingCentre centre = new MeetingCentre();
                meetingObjectList = new ArrayList<String>(Arrays.asList(line.split(",")));

                if ((!roomTrigger) && (meetingObjectList.size() > 1)) {
                    centre.setName(meetingObjectList.get(0));
                    centre.setCode(meetingObjectList.get(1));
                    centre.setDescription(meetingObjectList.get(2));
                    allMeetingCentres.add(centre);
                }

                if ((roomTrigger) && (meetingObjectList.size() > 1)) {
                    MeetingRoom room = new MeetingRoom();
                    room.setName(meetingObjectList.get(0));
                    room.setCode(meetingObjectList.get(1));
                    room.setDescription(meetingObjectList.get(2));
                    room.setCapacity(Integer.parseInt(meetingObjectList.get(3)));
                    if (meetingObjectList.get(4).equals("YES")) {
                        room.setHasVideoConference(true);
                    } else {
                        room.setHasVideoConference(false);
                    }
                    for (MeetingCentre mc : allMeetingCentres) {
                        if (meetingObjectList.get(5).equals(mc.getCode())) {
                            room.setMeetingCentre(mc);
                        }
                    }
                    allMeetingRooms.add(room);
                }

                if (meetingObjectList.get(0).contains("ROOMS")) {
                    roomTrigger = true;
                }
            }
        } catch (IOException ex) {
            System.err.println("*** Error with loading file. *** ");
            System.err.println(ex);
        }

        for (MeetingCentre mc : allMeetingCentres) {
            List<MeetingRoom> temp = new ArrayList<>();
            for (MeetingRoom mr : allMeetingRooms) {
                if (mc.getCode().equals(mr.getMeetingCentre().getCode())) {
                    temp.add(mr);
                }
            }
            mc.setMeetingRooms(temp);
        }

        return allMeetingCentres;
    }

//	/**
//	 * Method to save the data to file.
//     * EDIT: Changed the method input from String to List
//	 */
//	public static void saveData(List<MeetingCentre> meetingCentres) {
//		//TODO: ulozeni dat do souboru
//        List<MeetingRoom> rooms = new ArrayList<>();
//        try(BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src/main/resources/SavedData.csv"))))
//        {
//            String lineSep = ",,,";
//            String attrSep = ",";
//            wr.write("MEETING_CENTRES" + ",,,,,");
//            wr.write(System.lineSeparator());
//            for (MeetingCentre mc: meetingCentres){
//                wr.write(mc.getName() + attrSep + mc.getCode() + attrSep + mc.getDescription() + lineSep);
//                wr.write(System.lineSeparator());
//                if (mc.getMeetingRooms() != null ){
//                    rooms.addAll(mc.getMeetingRooms());
//                }
//            }
//            wr.write("MEETING_ROOMS" + ",,,,,");
//            wr.write(System.lineSeparator());
//                for (MeetingRoom mr: rooms){
//                    wr.write(mr.getName() + attrSep + mr.getCode() + attrSep + mr.getDescription() + attrSep + mr.getCapacity() + attrSep);
//                    if (mr.isHasVideoConference()){
//                        wr.write("YES" + attrSep);
//                    }else{
//                        wr.write("NO" + attrSep);
//                    }
//                    wr.write(mr.getMeetingCentre().getCode());
//                    wr.write(System.lineSeparator());
//                }
//        }catch (IOException e){
//          // System.err.println("Problem with saving data! ");
//         //  System.err.println(e);
//           return;
//        }
//
//		System.out.println();
//
//		System.out.println("**************************************************");
//		System.out.println("Data was saved correctly.");
//		System.out.println("**************************************************");
//
//		System.out.println();
//	}

    /**
     * Method to save the data to file.
     */
    public static void saveDataToXml(List<MeetingCentre> meetingCentres) {
        // TODO: ulozeni dat do XML souboru, jmeno souboru muze byt natvrdo,
        // adresar stejny jako se nachazi aplikace
        //  File fileToSaveXML = null;
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            //root
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("meetingCenters");
            doc.appendChild(rootElement);

            for (MeetingCentre mc : meetingCentres) {
                //centre
                Element centre = doc.createElement("meetingCenter");
                rootElement.appendChild(centre);

                //centre elements
                Element name = doc.createElement("name");
                name.appendChild(doc.createTextNode(mc.getName()));
                centre.appendChild(name);

                Element code = doc.createElement("code");
                code.appendChild(doc.createTextNode(mc.getCode()));
                centre.appendChild(code);

                Element description = doc.createElement("description");
                description.appendChild(doc.createTextNode(mc.getDescription()));
                centre.appendChild(description);

                //rooms
                Element rooms = doc.createElement("meetingRooms");
                centre.appendChild(rooms);

                for (MeetingRoom mr : mc.getMeetingRooms()) {
                    Element room = doc.createElement("meetingRoom");
                    rooms.appendChild(room);

                    Element roomName = doc.createElement("name");
                    roomName.appendChild(doc.createTextNode(mr.getName()));
                    room.appendChild(roomName);

                    Element roomCode = doc.createElement("code");
                    roomCode.appendChild(doc.createTextNode(mr.getCode()));
                    room.appendChild(roomCode);

                    Element roomDescription = doc.createElement("description");
                    roomDescription.appendChild(doc.createTextNode(mr.getDescription()));
                    room.appendChild(roomDescription);

                    Element roomCapacity = doc.createElement("capacity");
                    roomCapacity.appendChild(doc.createTextNode(Integer.toString(mr.getCapacity())));
                    room.appendChild(roomCapacity);

                    Element hasVideo = doc.createElement("hasVideoConference");
                    hasVideo.appendChild(doc.createTextNode(mr.isHasVideoConference() ? "Yes" : "No"));
                    room.appendChild(hasVideo);

                    Element mcCode = doc.createElement("centreCode");
                    mcCode.appendChild(doc.createTextNode(mr.getMeetingCentre().getCode()));
                    room.appendChild(mcCode);

                    //reservations
                    Element reservations = doc.createElement("reservations");
                    room.appendChild(reservations);
                    if (mr.getReservations() != null) {
                        for (Reservation res : mr.getReservations()) {
                            Element reservation = doc.createElement("reservation");
                            reservations.appendChild(reservation);

                            Element customer = doc.createElement("customer");
                            customer.appendChild(doc.createTextNode(res.getCustomer()));
                            reservation.appendChild(customer);

                            Element rDate = doc.createElement("date");
                            rDate.appendChild(doc.createTextNode(res.getFormattedDate()));
                            reservation.appendChild(rDate);

                            Element from = doc.createElement("from");
                            from.appendChild(doc.createTextNode(res.getTimeFrom()));
                            reservation.appendChild(from);

                            Element to = doc.createElement("to");
                            to.appendChild(doc.createTextNode(res.getTimeTo()));
                            reservation.appendChild(to);

                            Element expectedPersons = doc.createElement("expectedPersonCount");
                            expectedPersons.appendChild(doc.createTextNode(Integer.toString(res.getExpectedPersonCount())));
                            reservation.appendChild(expectedPersons);

                            Element video = doc.createElement("videoConference");
                            video.appendChild(doc.createTextNode(res.isNeedVideoConference() ? "Yes" : "No"));
                            reservation.appendChild(video);

                            Element note = doc.createElement("note");
                            note.appendChild(doc.createTextNode(res.getNote()));
                            reservation.appendChild(note);

                            Element roomReservationCode = doc.createElement("roomReservationCode");
                            roomReservationCode.appendChild(doc.createTextNode(res.getMeetingRoom().getCode()));
                            reservation.appendChild(roomReservationCode);
                        }
                    }


                }
            }


            //transform to xml
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("src/main/resources/XmlData.xml"));

            //console
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println();

            System.out.println("**************************************************");
            System.out.println("XML file saved correctly!");
            System.out.println("**************************************************");

            System.out.println();
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to load the data from XML file.
     *
     * @return
     */
    public static List<MeetingCentre> loadDataFromXml() {
        // TODO: nacist data z XML souboru

        try {
            List<MeetingCentre> meetingCentres = new ArrayList<>();
            List<MeetingRoom> meetingRooms = new ArrayList<>();
            List<Reservation> reservations = new ArrayList<>();

            File xmlFile = new File("src/main/resources/XmlData.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();


            NodeList nList = doc.getElementsByTagName("meetingCenter");


            for (int temp = 0; temp < nList.getLength(); temp++) {
                MeetingCentre mc = new MeetingCentre();
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    mc.setName(eElement.getElementsByTagName("name").item(0).getTextContent());
                    mc.setCode(eElement.getElementsByTagName("code").item(0).getTextContent());
                    mc.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());

                    meetingCentres.add(mc);
                }
            }

            nList = doc.getElementsByTagName("meetingRoom");

            for (int i = 0; i < nList.getLength(); i++) {
                MeetingRoom mr = new MeetingRoom();
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    mr.setName(eElement.getElementsByTagName("name").item(0).getTextContent());
                    mr.setCode(eElement.getElementsByTagName("code").item(0).getTextContent());
                    mr.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());
                    mr.setCapacity(Integer.parseInt(eElement.getElementsByTagName("capacity").item(0).getTextContent()));
                    mr.setHasVideoConference(eElement.getElementsByTagName("hasVideoConference").item(0).getTextContent().equals("Yes"));

                    String centreCode = eElement.getElementsByTagName("centreCode").item(0).getTextContent();

                    for (MeetingCentre mc : meetingCentres) {
                        if (centreCode.equals(mc.getCode())) {
                            mr.setMeetingCentre(mc);
                            break;
                        }
                    }
                }
                meetingRooms.add(mr);
            }

            nList = doc.getElementsByTagName("reservation");

            for (int i = 0; i < nList.getLength(); i++) {
                Reservation res = new Reservation();
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    Date date1 = new SimpleDateFormat("dd.MM.yyyy").parse(eElement.getElementsByTagName("date").item(0).getTextContent());
                    res.setDate(date1);
                    res.setCustomer(eElement.getElementsByTagName("customer").item(0).getTextContent());
                    res.setNote(eElement.getElementsByTagName("note").item(0).getTextContent());
                    res.setTimeFrom(eElement.getElementsByTagName("from").item(0).getTextContent());
                    res.setTimeTo(eElement.getElementsByTagName("to").item(0).getTextContent());
                    res.setExpectedPersonCount(Integer.parseInt(eElement.getElementsByTagName("expectedPersonCount").item(0).getTextContent()));
                    res.setNeedVideoConference(eElement.getElementsByTagName("videoConference").item(0).getTextContent().equals("Yes"));
                    String roomCode = eElement.getElementsByTagName("roomReservationCode").item(0).getTextContent();

                    for (MeetingRoom mr: meetingRooms){
                        if (mr.getCode().equals(roomCode)){
                            res.setMeetingRoom(mr);
                        }
                    }
                }
                reservations.add(res);
            }

            for (MeetingRoom mr :meetingRooms){
                List<Reservation> tempRes = new ArrayList<>();
                for (Reservation res : reservations){
                    if (mr.getCode().equals(res.getMeetingRoom().getCode())){
                        tempRes.add(res);
                    }
                }
                mr.setReservations(tempRes);
            }

            for (MeetingCentre mc : meetingCentres) {
                List<MeetingRoom> rooms = new ArrayList<>();
                for (MeetingRoom mr : meetingRooms) {
                    if (mc.getCode().equals(mr.getMeetingCentre().getCode())) {
                        rooms.add(mr);
                    }
                }
                mc.setMeetingRooms(rooms);
            }
            System.out.println();

            System.out.println("**************************************************");
            System.out.println("Data from XML was loaded correctly.");
            System.out.println("**************************************************");

            System.out.println();

            return meetingCentres;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//
//    /**
//     * Method to load the data from file.
//     *
//     * @return
//     */
//    public static List<MeetingCentre> loadDataFromFile() {
//        //TODO: nacist data ze souboru
//
//        try {
//            List<MeetingCentre> allMeetingCentres;
//
//            Path path = Paths.get("src/main/resources/SavedData.csv");
//            String location = path.toString();
//            allMeetingCentres = loadData(location);
//
//            System.out.println();
//            System.out.println("**************************************************");
//            System.out.println("Data was loaded correctly.");
//            System.out.println("**************************************************");
//            System.out.println();
//
//            return allMeetingCentres;
//        } catch (Exception e) {
//            System.err.println("Problem with loading data | " + e);
//            return null;
//        }
//
//    }

    /**
     * Method to export data to JSON file
     * <p>
     * //     * @param controllReservation Object of reservation controller to get all reservation and
     * other data if needed
     */
    public static void exportDataToJSON(List<MeetingCentre> meetingCentres) {
        // TODO: ulozeni dat do souboru ve formatu JSON

        //   File exportDataFile = null;
//        Reservation res1 = new Reservation();
//        Reservation res2 = new Reservation();
//
//        res1.setTimeFrom("7:30");
//        res1.setTimeTo("8:30");
//        res1.setNote("note1");
//        res1.setCustomer("Unicorn");
//        res1.setExpectedPersonCount(5);
//        Date myDate = new GregorianCalendar(2017, Calendar.DECEMBER,14).getTime();
//        res1.setDate(myDate);
//        res1.setNeedVideoConference(true);
//        res1.setMeetingRoom(testCentres.get(0).getMeetingRooms().get(0));
//
//        res2.setTimeFrom("12:30");
//        res2.setTimeTo("14:30");
//        res2.setNote("ahoj");
//        res2.setCustomer("Unicorn CLG");
//        res2.setExpectedPersonCount(8);
//        myDate = new GregorianCalendar(2017, Calendar.DECEMBER,15).getTime();
//        res2.setDate(myDate);
//        res2.setNeedVideoConference(false);
//        res2.setMeetingRoom(testCentres.get(0).getMeetingRooms().get(1));

        ObjectMapper mapper = new ObjectMapper();
        JSONArray array = new JSONArray();
        JSONObject everything = new JSONObject();

        for (MeetingCentre mc : meetingCentres) {
            List<MeetingRoom> rooms = mc.getMeetingRooms();
            for (MeetingRoom mr : rooms) {
                List<Reservation> reservations = mr.getReservations();
                reservations.sort(Comparator.comparing(Reservation::getDate).thenComparing(Reservation::getTimeFrom));
                if (reservations.size() > 0) {
                    JSONObject obj = new JSONObject();
                    JSONObject date = new JSONObject();

                    obj.put("meetingCentre", mc.getCode());
                    obj.put("meetingRoom", mr.getCode());

                    for (Reservation re : reservations) {
                        JSONArray reservationsByDate = new JSONArray();

                        if(date.containsKey(re.getFormattedDate())){
                             reservationsByDate = (JSONArray) date.get(re.getFormattedDate());
                        }

                        JSONObject reservation = new JSONObject();
                        reservation.put("customer", re.getCustomer());
                        reservation.put("from", re.getTimeFrom());
                        reservation.put("to", re.getTimeTo());
                        reservation.put("expectedPersonsCount", re.getExpectedPersonCount());
                        reservation.put("videoConference", re.isNeedVideoConference());
                        reservation.put("note", re.getNote());

                        reservationsByDate.add(reservation);
                        date.put(re.getFormattedDate(), reservationsByDate);


                    }

//                    Map<String,Object> sortedDate = new TreeMap<String, Object>(new DateComparator());
//                    for (Object key :date.keySet()){
//                        sortedDate.put((String)key,date.get(key));
//                    }

                    obj.put("reservations", date);
                    Map<Object,Object> sortedObj = new TreeMap<>(obj);
                    array.add(sortedObj);
                }
            }
        }

        everything.put("data", array);

        System.out.println();
         String locationFilter = Choices.getInput("Enter path and name of the file for export (for example 'src/main/resources/JsonData'): ");
        try (FileWriter file = new FileWriter(locationFilter + ".json")) {

            file.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(everything));
            file.flush();

            System.out.println("***************************************");
            System.out.println("Data exported successfully.");
            System.out.println("***************************************");
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if (exportDataFile != null) {
//            System.out.println("**************************************************");
//            System.out.println("Data was exported correctly. The file is here: " + exportDataFile.getAbsolutePath());
//            System.out.println("**************************************************");
//        } else {
//            System.out.println("**************************************************");
//            System.out.println("Something terrible happened during exporting!");
//            System.out.println("**************************************************");
//        }

//        System.out.println();
    }


}
