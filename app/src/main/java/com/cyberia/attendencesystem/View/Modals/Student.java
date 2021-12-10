package com.cyberia.attendencesystem.View.Modals;

import java.util.Comparator;

public class Student {
    private String rollNo;
    private String name;
    private String prn;
    private Boolean present;
    private int total_present;
    private int total_lecture;

    public Student(String rollNo, String name, String prn) {
        this.rollNo = rollNo;
        this.name = name;
        this.prn = prn;
    }
    public Student(String rollNo, String name, Boolean present) {
        this.rollNo = rollNo;
        this.name = name;
        this.present=present;
    }

    public Student(String rollNo, String name, int total_present, int total_lecture) {
        this.rollNo = rollNo;
        this.name = name;
        this.total_present=total_present;
        this.total_lecture=total_lecture;
    }

    public static Comparator<Student> StuRollno = new Comparator<Student>() {

        public int compare(Student s1, Student s2) {

            String rollno1 = s1.getRollNo();
            String rollno2 = s2.getRollNo();

            return rollno1.compareTo(rollno2);

        }};




    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrn() {
        return prn;
    }

    public void setPrn(String prn) {
        this.prn = prn;
    }

    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }

    public int getTotal_present() {
        return total_present;
    }

    public void setTotal_present(int total_present) {
        this.total_present = total_present;
    }

    public int getTotal_lecture() {
        return total_lecture;
    }

    public void setTotal_lecture(int total_lecture) {
        this.total_lecture = total_lecture;
    }
}
