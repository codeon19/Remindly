package samuelbreck.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    EditText taskNameEdit;
    EditText taskNoteEdit;

    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        pos = getIntent().getIntExtra("pos", 0);

        taskNameEdit = (EditText) findViewById(R.id.taskNameEdit);
        taskNoteEdit = (EditText) findViewById(R.id.taskNoteEdit);

        // if the form already exists
        if(pos >= 0){
            editForm();
        }
    }

    public void editForm() {

        // setting the form that already contains text
        taskNameEdit.setText(getIntent().getStringExtra("taskName"));
        taskNoteEdit.setText(getIntent().getStringExtra("taskNote"));

    }

    public void onSubmit(View v) {

        Intent data = new Intent();

        // Pass relevant data back as a result
        data.putExtra("taskName", taskNameEdit.getText().toString());
        data.putExtra("taskNote", taskNoteEdit.getText().toString());

        data.putExtra("pos", pos);
        
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);

        // closes the activity and returns to the first screen
        finish();
    }
}
