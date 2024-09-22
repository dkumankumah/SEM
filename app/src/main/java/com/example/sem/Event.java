package com.example.sem;

import java.io.Serializable;

public class Event implements Serializable {
    private String title;
    private String address;
    private int zipCode;
    private int date;
    private int startTime;
    private int endTime;
    private String category;
    private String attendanceType;
    private String description;

    public Event(String title, String address, int zipCode, int date, int startTime, int endTime, String category, String attendanceType, String description){
        this.title = title;
        this.address = address;
        this.zipCode = zipCode;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.attendanceType = attendanceType;
        this.description = description;
    }

    public String getTitle(){
        return title;
    }
    public String getAddress(){
        return address;
    }
    public int getZipCode(){
        return zipCode;
    }
    public int getDate(){return date;}
    public int getStartTime(){return startTime;}
    public int getEndTime(){return endTime;}
    public String getCategory(){return category;}
    public String getAttendanceType(){return attendanceType;}
    public String getDescription(){return description;}


}
