package com.axa.jira.worklog.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by A.Solianyk on 17.02.2017.
 */
public class WorkLog {
    private User user;
    private Ticket ticket;
    private String startDate;
    private int houers;
    private int minutes;
    private String description;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public int getHouers() {
        return houers;
    }

    public void setHouers(int houers) {
        this.houers = houers;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
