package com.example.myapplication.model;

import java.io.Serializable;

public class ThuChi implements Serializable {
        private String id;
        private String name;
        private String note;
        private String createTime;

    public ThuChi(String Name, String Note, String createTime) {
        name = Name;
        note = Note;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHoTen(String Name) {
        name = Name;
    }

    public void setEmail(String Note) {
        note = Note;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public ThuChi() {
    }
}
