package com.cyberia.attendencesystem.View.Modals;

import java.util.List;

public class Schedule {
    private int scheduleId;
    private int lectureId;
    private String subjectCode;
    private String subjectName;
    private String _class;
    private String teacherName;
    private List<Student> students = null;
    private String classType;
    private String group;

    public Schedule(int scheduleId, int lectureId, String subjectCode, String subjectName, String _class, String teacherName, List<Student> students, String classType, String group) {
        this.scheduleId = scheduleId;
        this.lectureId = lectureId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this._class = _class;
        this.teacherName = teacherName;
        this.students = students;
        this.classType = classType;
        this.group = group;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getLectureId() {
        return lectureId;
    }

    public void setLectureId(int lectureId) {
        this.lectureId = lectureId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
