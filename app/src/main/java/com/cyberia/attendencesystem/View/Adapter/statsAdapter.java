package com.cyberia.attendencesystem.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyberia.attendencesystem.R;
import com.cyberia.attendencesystem.View.Modals.Student;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

 public class statsAdapter extends RecyclerView.Adapter<statsAdapter.viewHolder> {
    private Context context;
    public static ArrayList<Student> studentList;


    public statsAdapter(Context context, ArrayList<Student> studentList) {
        this.context = context;
        this.studentList = studentList;

    }

    public static ArrayList<Student> getStudentList(){
        return studentList;
    }

    @NonNull
    @Override
    public statsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_student_report,parent,false);
        return new statsAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull statsAdapter.viewHolder holder, final int position) {

        final Student student=studentList.get(position);

        holder.studentName.setText(student.getName());
        holder.rollNo.setText("Roll No : "+student.getRollNo());
        holder.present.setText(student.getTotal_present()+ " / " + student.getTotal_lecture());

    }


    public static class viewHolder extends RecyclerView.ViewHolder{

        public TextView studentName, rollNo , present;



        public viewHolder(@NonNull View itemView) {
            super(itemView);

            studentName=itemView.findViewById(R.id.textViewStudentName);
            rollNo=itemView.findViewById(R.id.textViewRollNo);
            present=itemView.findViewById(R.id.textViewPresent);


        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

}
