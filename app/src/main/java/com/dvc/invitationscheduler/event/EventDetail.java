package com.dvc.invitationscheduler.event;

public class EventDetail {
    public EventDetail() {
    }

    public String event_name,event_time, event_date, place;
    public int guestNumber = 0, invitation_sent = 0;

    public int getInvitation_sent() {
        return invitation_sent;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setInvitation_sent(int invitation_sent) {
        this.invitation_sent = invitation_sent;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public int getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(int guestNumber) {
        this.guestNumber = guestNumber;
    }

    public EventDetail(String event_name, String event_time, String dateAdded) {
        this.event_name = event_name;
        this.event_time = event_time;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }
}
