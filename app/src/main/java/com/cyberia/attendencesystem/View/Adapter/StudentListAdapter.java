package com.cyberia.attendencesystem.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyberia.attendencesystem.R;
import com.cyberia.attendencesystem.View.Modals.Attendence;
import com.cyberia.attendencesystem.View.Modals.Lecture;
import com.cyberia.attendencesystem.View.Modals.Schedule;
import com.cyberia.attendencesystem.View.Modals.Student;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.viewHolder> {


    private Context context;
    public static ArrayList<Student> studentList;


    public StudentListAdapter(Context context, ArrayList<Student> studentList) {
        this.context = context;
        this.studentList = studentList;

    }

    public static ArrayList<Student> getStudentList(){
        return studentList;
    }

    @NonNull
    @Override
    public StudentListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_student,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {

        final Student student=studentList.get(position);

        holder.studentName.setText(student.getName());
        holder.rollNo.setText("Roll No : "+student.getRollNo());

        if(student.getPresent()!=null)
        {
            if(student.getPresent())
                holder.chipPresent.setChecked(true);
            else
                holder.chipAbsent.setChecked(true);

        }
        holder.chipPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                student.setPresent(true);
            }
        });

        holder.chipAbsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                student.setPresent(false);
            }
        });




    }


    public static class viewHolder extends RecyclerView.ViewHolder{

        public TextView studentName, rollNo;
        public ChipGroup chipGroup;
        public Chip chipAbsent,chipPresent;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            studentName=itemView.findViewById(R.id.textViewStudentName);
            rollNo=itemView.findViewById(R.id.textViewRollNo);
            chipGroup= itemView.findViewById(R.id.chipGroupAttendence);
            chipAbsent=itemView.findViewById(R.id.chipAbsent);
            chipPresent=itemView.findViewById(R.id.chipPresent);


        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
