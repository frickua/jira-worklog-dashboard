package com.axa.jira.worklog.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.*;

/**
 * Created by A.Solianyk on 21.02.2017.
 */
public class Day {
    private List<Ticket> workedTickets;
    private String dayOfWeek;
    private boolean weekend;
    private Date date;
    private String dateStr;
    private static final String[] DAYS = new String[]{"","SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private List<WorkLog> workLogs;
    private int totalMinutes;

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(int totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public List<WorkLog> getWorkLogs() {
        return workLogs;
    }

    public void setWorkLogs(List<WorkLog> workLogs) {
        this.workLogs = workLogs;
    }

    public Day(Date date) {
        this.date = date;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DAY_OF_WEEK);
        weekend = (day == SUNDAY || day == SATURDAY);
        dayOfWeek = DAYS[day];
        dateStr = sdf.format(date);
    }

    public String getTotalTimeStr() {
        int h = totalMinutes / 60;
        int m = totalMinutes % 60;
        return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m);
    }

    public String getDayType() {
        Calendar c = Calendar.getInstance();
        if (isWeekend() || date.compareTo(new Date()) > 0) {
            return "panel-default";
        } else if( totalMinutes >= 300 ) { // 5 houers = full day
            return "panel-success";
        } else if (totalMinutes < 300 && totalMinutes > 0) {
            return "panel-warning";
        } else {
            return "panel-danger";
        }
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public Date getDate() {
        return date;
    }

    public String getDateStr() {
        return dateStr;
    }

    public List<Ticket> getWorkedTickets() {
        return workedTickets;
    }

    public void setWorkedTickets(List<Ticket> workedTickets) {
        this.workedTickets = workedTickets;
    }
}
