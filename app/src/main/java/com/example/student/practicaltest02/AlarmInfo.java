package com.example.student.practicaltest02;

/**
 * Created by student on 21.05.2018.
 */

public class AlarmInfo {
    int hour;
    int minute;

    public AlarmInfo(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return "AlarmInfo{" +
                "hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
