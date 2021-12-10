package com.cyberia.attendencesystem.View.Modals;

public class Lecture {
    private int teacherId;
    private int subjectId;
    private int scheduleId;
    private int id;
    private int lectureId;
    private String day;
    private String timeFrom;
    private String timeTo;
    private int type;
    private String group;
    private Boolean is_marked;
    private Subject subject;

    public Lecture(int teacherId, int subjectId, int scheduleId, int id, int lectureId, String day, String timeFrom, String timeTo, int type, String group, Boolean is_marked ,Subject subject) {
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.scheduleId = scheduleId;
        this.id = id;
        this.lectureId = lectureId;
        this.day = day;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.type = type;
        this.group = group;
        this.is_marked=is_marked;
        this.subject = subject;
    }


    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLectureId() {
        return lectureId;
    }

    public void setLectureId(int lectureId) {
        this.lectureId = lectureId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Boolean getIs_marked() {
        return is_marked;
    }

    public void setIs_marked(Boolean is_marked) {
        this.is_marked = is_marked;
    }
}
