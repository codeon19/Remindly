package samuelbreck.remindly;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    String taskName = "";
    String taskDate = "";
    String taskPriority = "";
    String taskNote = "";

    String primary_key;

    ArrayList<Task> arrayOfTasks;
    TaskAdapter adapter;
    ListView listView;

    Realm myRealm;

    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Construct the data source
        arrayOfTasks = new ArrayList<Task>();
        // Create the adapter to convert the array to views
        // Custom adapter in TaskAdapter.java
        adapter = new TaskAdapter(this, arrayOfTasks);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.lvItems);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);

        myRealm = Realm.getDefaultInstance();

        updateList();

        listView.setAdapter(adapter);

        setupListViewListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {

            Intent intention = new Intent(MainActivity.this, EditActivity.class);
            intention.putExtra("pos", -1);

            startActivityForResult(intention,REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupListViewListener() {

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {

                // creates the intent
                Intent intention = new Intent(MainActivity.this, EditActivity.class);

                intention.putExtra("pos", pos);
                intention.putExtra("taskName", arrayOfTasks.get(pos).getTaskName());
                intention.putExtra("taskDate", arrayOfTasks.get(pos).getDueDate());
                intention.putExtra("taskPriority", arrayOfTasks.get(pos).getPriority());
                intention.putExtra("taskNote", arrayOfTasks.get(pos).getTaskNote());

                // launches the edit item screen
                startActivityForResult(intention,REQUEST_CODE);

                return true;
            }
        });
    }

    // ActivityOne.java, time to handle the result of the sub-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            int pos = data.getExtras().getInt("pos", 0);

            taskName = data.getExtras().getString("taskName");
            taskDate = data.getExtras().getString("taskDate");
            taskPriority = data.getExtras().getString("taskPriority");
            taskNote = data.getExtras().getString("taskNote");

            if(pos != -1) {

                primary_key = data.getExtras().getString("primary_key");

                // editing a current object
                myRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Task editTask= realm.where(Task.class).equalTo("taskName", primary_key).findFirst();

                        editTask.setTaskName(taskName);
                        editTask.setDueDate(taskDate);
                        editTask.setPriority(taskPriority);
                        editTask.setTaskNote(taskNote);
                    }
                });
            }
            else {

                // adding a new object
                myRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Task newTask = realm.createObject(Task.class);

                        newTask.setTaskName(taskName);
                        newTask.setDueDate(taskDate);
                        newTask.setPriority(taskPriority);
                        newTask.setTaskNote(taskNote);
                    }
                });

            }

            updateList();

            // resets the edit
            taskName = "";
            taskDate = "";
            taskPriority = "";
            taskNote = "";
        }
        else {
            int pos = data.getExtras().getInt("pos", 0);

            // make sure a blank data set is not accidentally deleted
            if(pos != -1) {
                final String remove_taskName = arrayOfTasks.get(pos).getTaskName();
                arrayOfTasks.remove(pos);
                adapter.notifyDataSetChanged();

                final RealmResults<Task> results = myRealm.where(Task.class).equalTo("taskName", remove_taskName).findAll();

                myRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        results.deleteAllFromRealm();
                    }
                });

                updateList();
            }
        }
    }

    private void updateList() {

        RealmResults<Task> result = myRealm.where(Task.class).findAll();

        arrayOfTasks.clear();
        adapter.notifyDataSetChanged();

        for(Task t : result)
        {
            arrayOfTasks.add(t);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

}
