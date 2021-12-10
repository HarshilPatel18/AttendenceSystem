package com.cyberia.attendencesystem.View.Modals;

import java.util.HashMap;

public  class Attendence {
    public static HashMap<Integer,Boolean> data;

    public Attendence(HashMap<Integer,Boolean> att) {
        data=att;
    }

    public static HashMap<Integer, Boolean> getAttendence() {
        return data;
    }

    public static void setAttendence(HashMap<Integer, Boolean> data) {
        Attendence.data = data;
    }
}
