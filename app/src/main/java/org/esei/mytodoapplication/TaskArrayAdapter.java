package org.esei.mytodoapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.esei.mytodoapplication.model.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskArrayAdapter  extends ArrayAdapter<Task> {


    public TaskArrayAdapter(@NonNull Context context, List<Task> objects) {
        super(context, 0, objects);
    }

    class ViewHolder {
        TextView textViewName;
        TextView textViewDate;
        CheckBox checkBoxDone;
        LinearLayout linearLayoutTask;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder= null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.task_list_item, null);

            viewHolder.textViewName =convertView.findViewById(R.id.textViewName);
            viewHolder.textViewDate = convertView.findViewById(R.id.textViewDate);
            viewHolder.checkBoxDone = convertView.findViewById(R.id.checkBoxDone);
            viewHolder.linearLayoutTask = convertView.findViewById(R.id.linearLayoutTask);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.textViewName.setText(getItem(position).getName());

        if (getItem(position).getDone()){
            viewHolder.textViewName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            viewHolder.textViewName.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        viewHolder.textViewDate.setText(sdf.format(getItem(position).getDate()));


        viewHolder.checkBoxDone.setChecked(getItem(position).getDone());
        viewHolder.checkBoxDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getItem(position).setDone(isChecked);
                notifyDataSetChanged();
            }
        });

        Calendar calendar = Calendar.getInstance();
        Date today = new Date(System.currentTimeMillis());
        calendar.setTime(today);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        today = calendar.getTime();


        if (today.after(getItem(position).getDate()))
            viewHolder.linearLayoutTask.setBackgroundColor(Color.RED);
        else
            viewHolder.linearLayoutTask.setBackgroundColor(Color.WHITE);


        return convertView;
    }
}
