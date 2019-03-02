package com.wangdiam.studybuddycapstoneproject.models;

public class Card {
    private Long id;
    private String front,back;
    private Long subjectId;
    private boolean isSelected;
    private String subjectName;
    public Card(String front, String back, Long subjectId, String subjectName) {
        this.front = front;
        this.back = back;
        this.subjectId = subjectId;
        this.id = System.currentTimeMillis();
        this.subjectName = subjectName;
    }

    public Card() {

    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {this.id = id;}

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public String getSubjectName() {
        return subjectName;
    }
    public void setSubjectName(String name) {
        this.subjectName = name;
    }
}
