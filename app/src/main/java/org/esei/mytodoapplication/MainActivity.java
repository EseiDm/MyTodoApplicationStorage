package org.esei.mytodoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import org.esei.mytodoapplication.model.Task;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> tasks = new ArrayList<>();
    private TaskArrayAdapter taskArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int i = 1; i<100; i++){
            Task obj = new Task();
            obj.setName("Task " + i);
            tasks.add(obj);
        }
        ListView listView = findViewById(R.id.listViewTask);
        taskArrayAdapter = new TaskArrayAdapter(this, tasks);
        listView.setAdapter(taskArrayAdapter);

        registerForContextMenu(listView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.task_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case(R.id.menuItemAdd):
                doAdd();
                break;
            case (R.id.menuItemClear):
                doClear();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;

    }

    private void doClear() {
        tasks.clear();
        taskArrayAdapter.notifyDataSetChanged();
    }

    private void doAdd() {
        tasks.add(new Task());
        doEdit(tasks.size()-1);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listViewTask){
            getMenuInflater().inflate(R.menu.task_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case(R.id.itemMenuEdit):
                int position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                doEdit(position);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    private void doEdit(int position) {
        DatePickerDialog dialog = new DatePickerDialog(this);
        dialog.setTitle("Pick Date");
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                tasks.get(position).setDate(calendar.getTime());
                taskArrayAdapter.notifyDataSetChanged();
                showTaskNameDialog(position);
            }
        });
        dialog.show();

    }

    private void showTaskNameDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Task Name");
        EditText editText = new EditText(this);
        editText.setText(tasks.get(position).getName());
        builder.setView(editText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taskName = editText.getText().toString();
                tasks.get(position).setName(taskName);
                taskArrayAdapter.notifyDataSetChanged();
            }
        });
        builder.create().show();
    }
}