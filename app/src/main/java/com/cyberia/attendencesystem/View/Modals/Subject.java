package com.cyberia.attendencesystem.View.Modals;

public class Subject {
    private int id;
    private String name;
    private String code;
    private String className;


    public Subject(int id, String name, String code, String className) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.className = className;
    }
    @Override
    public String toString() {
        return name;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getclassName() {
        return className;
    }

    public void setclassName(String className) {
        this.className = className;
    }
}
