package com.dvc.invitationscheduler.event;

public class KeyDetails {
    public static String eventKey,guestKey,userKey;
    public static boolean eventEditable;
    public static int invitation_sent , guestNumber;

    public static int getGuestNumber() {
        return guestNumber;
    }

    public static void setGuestNumber(int guestNumber) {
        KeyDetails.guestNumber = guestNumber;
    }

    public static int getInvitation_sent() {
        return invitation_sent;
    }

    public static void setInvitation_sent(int invitation_sent) {
        KeyDetails.invitation_sent = invitation_sent;
    }

    public static boolean isEventEditable() {
        return eventEditable;
    }

    public static void setEventEditable(boolean eventEditable) {
        KeyDetails.eventEditable = eventEditable;
    }

    public static String getUserKey() {
        return userKey;
    }

    public static void setUserKey(String userKey) {
        KeyDetails.userKey = userKey;
    }

    public static String getEventKey() {
        return eventKey;
    }

    public static void setEventKey(String eventKey) {
        KeyDetails.eventKey = eventKey;
    }

    public static String getGuestKey() {
        return guestKey;
    }

    public static void setGuestKey(String guestKey) {
        KeyDetails.guestKey = guestKey;
    }
}
