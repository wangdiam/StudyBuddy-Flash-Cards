package com.wangdiam.studybuddycapstoneproject.models;

public class Subject {
    private String name;
    private Long id;
    private Long cardCount;

    public Subject(String name) {
        this.name = name;
    }

    public Subject() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {return this.id;}

    public void setId(Long id) {this.id = id;}

    public Long getCardCount() {return this.cardCount;}

    public void setCardCount(Long l) {
        this.cardCount = l;
    }
}
