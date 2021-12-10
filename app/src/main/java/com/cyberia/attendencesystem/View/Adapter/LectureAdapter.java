package com.cyberia.attendencesystem.View.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cyberia.attendencesystem.R;
import com.cyberia.attendencesystem.View.Modals.Lecture;
import com.cyberia.attendencesystem.View.StudentsActivity;
import com.lid.lib.LabelTextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.MyviewHolder> {

    private Context context;
    private ArrayList<Lecture> lectureList;

    public LectureAdapter(Context context, ArrayList<Lecture> lectureList) {
        this.context = context;
        this.lectureList = lectureList;
    }

    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_lecture,parent,false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyviewHolder holder, int position) {
        final Lecture lecture=lectureList.get(position);
        holder.SubjectName.setText(lecture.getSubject().getName());

        final String cName;
        if(lecture.getGroup()==null || lecture.getGroup().trim()=="null")
            cName=lecture.getSubject().getclassName();

        else
            cName=lecture.getSubject().getclassName() + " - Group " + lecture.getGroup();

        if(lecture.getIs_marked())
        {
            holder.labelTextView.setVisibility(View.VISIBLE);
        }
        else {
            holder.labelTextView.setVisibility(View.GONE);
        }
        holder.className.setText(cName);
        holder.Day.setText(lecture.getDay());
        holder.Time.setText(lecture.getTimeFrom()+" - "+lecture.getTimeTo());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),StudentsActivity.class);
                intent.putExtra("is_marked",lecture.getIs_marked());
                intent.putExtra("subject_id",lecture.getSubjectId());
                intent.putExtra("day",lecture.getDay().toUpperCase());
                intent.putExtra("time_from",lecture.getTimeFrom());
                intent.putExtra("time_to",lecture.getTimeTo());
                intent.putExtra("subject",lecture.getSubject().getName());
                intent.putExtra("schedule_id",lecture.getScheduleId());
                intent.putExtra("lecture_id",lecture.getLectureId());
                intent.putExtra("code",lecture.getSubject().getCode());
                intent.putExtra("class",lecture.getSubject().getclassName());
                intent.putExtra("classname",cName);
                intent.putExtra("is_prev",false);
                view.getContext().startActivity(intent);
                Animatoo.animateSlideLeft(context);
            }
        });

    }

    public static class MyviewHolder extends RecyclerView.ViewHolder{

        public TextView SubjectName, className,Day,Time;
        public CardView cardView;
        public LabelTextView labelTextView;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            SubjectName=itemView.findViewById(R.id.textViewSubjectName);
            className=itemView.findViewById(R.id.textViewClassName);
            Day=itemView.findViewById(R.id.textViewDay);
            Time=itemView.findViewById(R.id.textViewTime);
            cardView=itemView.findViewById(R.id.cardviewLecture);
            labelTextView=itemView.findViewById(R.id.labelMarked);


        }
    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }

}
