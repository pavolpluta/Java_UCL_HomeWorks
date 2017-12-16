package cz.unicorncollege.bt.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MeetingRoom extends MeetingObject {
	private int capacity;	
	private boolean hasVideoConference;	
	private MeetingCentre meetingCentre;
	private List<Reservation> reservations;

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public boolean isHasVideoConference() {
		return hasVideoConference;
	}

	public void setHasVideoConference(boolean hasVideoConference) {
		this.hasVideoConference = hasVideoConference;
	}

	public MeetingCentre getMeetingCentre() {
		return meetingCentre;
	}

	public void setMeetingCentre(MeetingCentre meetingCentre) {
		this.meetingCentre = meetingCentre;
	}

	public List<Reservation> getReservations() {
		return reservations;
	}

	public List<Reservation> getSortedReservationsByDate(Date date) {
		//TODO get reservations by date and return sorted reservations by hours
		List<Reservation> sortedReservations = new ArrayList<>();
		for (Reservation res : reservations){
			if (res.getDate().equals(date)){
				sortedReservations.add(res);
			}
		}
		sortedReservations.sort(Comparator.comparing(Reservation::getTimeFrom));
		return sortedReservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
}
