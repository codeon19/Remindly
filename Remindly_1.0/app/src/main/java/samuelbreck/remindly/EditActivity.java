package samuelbreck.remindly;

import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;


public class EditActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {


    String primary_key;

    EditText taskNameEdit;
    TextView taskDateEdit;

    RadioButton low;
    RadioButton medium;
    RadioButton high;

    EditText taskNoteEdit;

    int pos;

    String priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        pos = getIntent().getIntExtra("pos", 0);

        taskNameEdit = (EditText) findViewById(R.id.taskNameEdit);

        taskDateEdit = (TextView) findViewById(R.id.taskDateEdit);

        low = (RadioButton) findViewById(R.id.low);
        medium = (RadioButton) findViewById(R.id.medium);
        high = (RadioButton) findViewById(R.id.high);

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

        priority = getIntent().getStringExtra("taskPriority");

        switch(priority) {
            case "low":
                low.setChecked(true);
                break;
            case "medium":
                medium.setChecked(true);
                break;
            case "high":
                high.setChecked(true);
                break;

        }

        taskNoteEdit.setText(getIntent().getStringExtra("taskNote"));

    }

    public void onSubmit() {

        Intent data = new Intent();

        // Pass relevant data back as a result

        data.putExtra("taskName", taskNameEdit.getText().toString());
        data.putExtra("taskDate", taskDateEdit.getText().toString());
        data.putExtra("taskPriority",priority);
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

    public void onClickDate (View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                EditActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = (monthOfYear+1) + "/" + dayOfMonth + "/" + year;
        taskDateEdit.setText(date);
    }

    public void onRadioButtonClicked(View v) {
        // Is the button now checked?
        boolean checked = ((RadioButton) v).isChecked();

        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.low:
                if (checked)
                    priority = "low";
                    break;
            case R.id.medium:
                if (checked)
                    priority = "medium";
                    break;
            case R.id.high:
                if (checked)
                    priority = "high";
                    break;
        }
    }

}
