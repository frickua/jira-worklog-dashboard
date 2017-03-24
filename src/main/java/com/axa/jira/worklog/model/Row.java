package com.axa.jira.worklog.model;

import java.util.*;

/**
 * Created by A.Solianyk on 17.02.2017.
 */
public class Row {
    private User user;
    private Map<String, List<WorkLog>> workLogsMap = new LinkedHashMap<>();
    private int totalMinutes = 0;

    public Map<String, Integer> totalMinutesPerDate = new HashMap<>();

    public Map<String, WorkLog> getTotalLogs() {
        return totalLogs;
    }

    private Map<String, WorkLog> totalLogs = new LinkedHashMap<>();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, List<WorkLog>> getWorkLogs() {
        return workLogsMap;
    }

    public void addWorkLog(WorkLog workLog) {
        addWorkLog(workLog.getStartDate(), workLog);
    }

    public void addWorkLog(String dateStr, WorkLog workLog) {
        List<WorkLog> workLogs = workLogsMap.get(dateStr);
        if (workLogs == null) {
            workLogs = new ArrayList<>();
            workLogsMap.put(dateStr, workLogs);
        }
        workLogs.add(workLog);
    }

    public void addTotalLog(String dateStr, WorkLog totalLog) {
        this.totalLogs.put(dateStr, totalLog);
    }

    public WorkLog getTotalLog(String dateStr) {
        return totalLogs.get(dateStr);
    }

    public String getTotalTime(String dateStr) {
        return workLogToString(totalLogs.get(dateStr));
    }

    private static String workLogToString(WorkLog wLog) {
        if (wLog != null) {
            int h = wLog.getHouers();
            int m = wLog.getMinutes();
            return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m);
        }
        return "00:00";
    }

    public void addMinutes(int mins) {
        totalMinutes += mins;
    }

    public void setTotalMinutesPerDate(String dateStr, int mins) {
        totalMinutesPerDate.put(dateStr, mins);
    }

    public int getTotalMinutesPerDate(String dateStr) {
        Integer i = totalMinutesPerDate.get(dateStr);
        return i == null ? 0 : i;
    }

    public String getTotalTimeStr() {
        int h = totalMinutes / 60;
        int m = totalMinutes % 60;
        return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m);
    }

    public Map<String, List<WorkLog>> getWorklogsMap() {
        return workLogsMap;
    }

}
