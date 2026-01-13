package com.project.back_end.models;

public class TimeSlot {

    private String times;

    public TimeSlot() {
    }
    public TimeSlot(String times) {
        this.times = times;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getStartTime() {
        return times.split("-")[0];
    }
}
