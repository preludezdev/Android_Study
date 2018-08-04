package com.example.pen.android_study_01;

import java.io.Serializable;

public class Memo implements Serializable {
    private int id;
    private String name;
    private String content;
    private String detailContent;

    public Memo(int id , String title, String content, String detail) {
        this.id = id;
        this.name = title;
        this.content = content;
        this.detailContent = detail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetailContent() {
        return detailContent;
    }

    public void setDetailContent(String detailContent) {
        this.detailContent = detailContent;
    }

    @Override
    public String toString() {
        return this.getName()+"\n"+
                this.getContent()+"\n"+
                this.getDetailContent();
    }
}

