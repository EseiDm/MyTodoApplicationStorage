package org.esei.mytodoapplication.model;

import java.util.Date;

public class Task {

    private String name ="";
    private Date date = new Date(System.currentTimeMillis());
    private Boolean done = false;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", done=" + done +
                '}';
    }
}
