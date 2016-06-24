package samuelbreck.remindly;

import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class EditActivity extends AppCompatActivity {


    String primary_key;

    EditText taskNameEdit;
    // missing the priority field
    EditText taskDateEdit;
    EditText taskNoteEdit;

    int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        pos = getIntent().getIntExtra("pos", 0);

        taskNameEdit = (EditText) findViewById(R.id.taskNameEdit);
        // missing the priority field
        taskDateEdit = (EditText) findViewById(R.id.taskDateEdit);
        taskNoteEdit = (EditText) findViewById(R.id.taskNoteEdit);

        // if the form already exists
        if(pos != -1){
            editForm();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_save:
                onSubmit();
                return true;

            case R.id.action_delete:
                onDelete();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it
                return super.onOptionsItemSelected(item);
        }
    }

    public void editForm() {

        // setting the form that already contains text
        primary_key = getIntent().getStringExtra("taskName");

        taskNameEdit.setText(primary_key);
        taskDateEdit.setText(getIntent().getStringExtra("taskDate"));
        // missing the priority field
        taskNoteEdit.setText(getIntent().getStringExtra("taskNote"));

    }

    public void onSubmit() {

        Intent data = new Intent();

        // Pass relevant data back as a result

        data.putExtra("taskName", taskNameEdit.getText().toString());
        data.putExtra("taskDate", taskDateEdit.getText().toString());
        data.putExtra("taskPriority","N/A" );
        data.putExtra("taskNote", taskNoteEdit.getText().toString());

        data.putExtra("pos", pos);

        if (pos != -1) {
            data.putExtra("primary_key",primary_key);
        }

        // Activity finished ok, return the data
        setResult(RESULT_OK, data);

        // closes the activity and returns to the first screen
        finish();
    }

    public void onDelete() {
        Intent data = new Intent();
        data.putExtra("pos", pos);

        setResult(RESULT_CANCELED,data);
        finish();
    }

}
