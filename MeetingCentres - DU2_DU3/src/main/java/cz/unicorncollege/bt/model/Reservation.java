package cz.unicorncollege.bt.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Reservation {
    private MeetingRoom meetingRoom;
    private Date date;
    private String timeFrom;
    private String timeTo;
    private int expectedPersonCount;
    private String customer;
    private boolean needVideoConference;
    private String note;

    private static int count = 0;
    private int id = 0;

    public Reservation(){
        id=count++;
    }

    public MeetingRoom getMeetingRoom() {
        return meetingRoom;
    }

    public void setMeetingRoom(MeetingRoom meetingRoom) {
        this.meetingRoom = meetingRoom;
    }

    public Date getDate() {
        return date;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getiD(){
        return id;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public int getExpectedPersonCount() {
        return expectedPersonCount;
    }

    public void setExpectedPersonCount(int expectedPersonCount) {
        this.expectedPersonCount = expectedPersonCount;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public boolean isNeedVideoConference() {
        return needVideoConference;
    }

    public void setNeedVideoConference(boolean needVideoConference) {
        this.needVideoConference = needVideoConference;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", date=" +  getFormattedDate() +
                ", timeFrom='" + timeFrom + '\'' +
                ", timeTo='" + timeTo + '\'' +
                ", expectedPersonCount=" + expectedPersonCount +
                ", customer='" + customer + '\'' +
                ", needVideoConference=" + needVideoConference +
                ", note='" + note + '\'' +
                '}';
    }
}
