package com.axa.jira.worklog.model;

/**
 * Created by A.Solianyk on 17.02.2017.
 */
public class Ticket {
    private String number;
    private Float sp;
    private String description;

    private String status;

    public Ticket(String number, Float sp, String status, String description) {
        this.number = number;
        this.sp = sp;
        this.status = status;
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Float getSp() {
        return sp;
    }

    public void setSp(Float sp) {
        this.sp = sp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
