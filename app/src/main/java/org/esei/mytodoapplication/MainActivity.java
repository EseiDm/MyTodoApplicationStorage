package org.esei.mytodoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.esei.mytodoapplication.model.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TASK_SHARED_PREFERENCES = "task_shared_preferences";
    private static final String TASKS = "tasks";

    private ArrayList<Task> tasks = new ArrayList<>();
    private TaskArrayAdapter taskArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int i = 1; i<=5; i++){
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
                int positionToEdit = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                doEdit(positionToEdit);
                break;
            case(R.id.itemMenuRemove):
                int positionToRemove = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                doRemove(positionToRemove);
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    private void doRemove(int positionToRemove) {
        this.tasks.remove(positionToRemove);
        this.taskArrayAdapter.notifyDataSetChanged();
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


    @Override
    protected void onResume() {
        super.onResume();
        //doLoadTaskFromSharedPreferences();
        //doLoadTaskFromInternalStorage();
        doLoadTaskFromExternalStorage();
    }

    private void doLoadTaskFromExternalStorage() {
        if (isExternalStorageReadable()){
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), TASKS);
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String jsonString = bufferedReader.readLine();
                loadTaskFromJson(jsonString);
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    private void doLoadTaskFromInternalStorage() {
        try {
            FileInputStream fileInputStream = this.openFileInput(TASKS);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String taskJsonString = reader.readLine();
            loadTaskFromJson(taskJsonString);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doLoadTaskFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(TASK_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String taskJsonString = preferences.getString(TASKS, "");
        loadTaskFromJson(taskJsonString);
    }

    private void loadTaskFromJson(String taskJsonString) {
        Gson gson = new Gson();
        Task[] taskFromJson = gson.fromJson(taskJsonString, Task[].class);
        this.tasks.clear();
        this.tasks.addAll(Arrays.asList(taskFromJson));
        this.taskArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause",Toast.LENGTH_LONG).show();
        //doSaveTaskAtSharedPreferences();
        //doSaveTaskAtInternalStorage();
        doSaveTaskAtExternalStorge();
    }

    private void doSaveTaskAtExternalStorge() {
        if (isExternalStorageWriteable()){
            File file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    TASKS);
            String taskJsonString = getTasksJsonString();
            try {
                PrintWriter printWriter = new PrintWriter(new FileOutputStream(file));
                printWriter.println(taskJsonString);
                printWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isExternalStorageWriteable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    private void doSaveTaskAtInternalStorage() {
        try {
            FileOutputStream fileOutputStream = this.openFileOutput(TASKS, Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(fileOutputStream);
            writer.println(getTasksJsonString());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private String getTasksJsonString() {
        Gson gson = new Gson();
        String taskJsonString  = gson.toJson(this.tasks);
        return taskJsonString;
    }

    private void doSaveTaskAtSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(TASK_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String taskJsonString = getTasksJsonString();
        editor.putString(TASKS, taskJsonString);
        editor.apply();
    }
}