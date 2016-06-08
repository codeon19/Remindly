package samuelbreck.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

    }

    public void onSubmit(View v) {

        // gets the string from the editText
        EditText editText = (EditText) findViewById(R.id.editText);
        Intent data = new Intent();

        int pos = getIntent().getIntExtra("pos", 0);

        // Pass relevant data back as a result
        data.putExtra("edits", editText.getText().toString());
        data.putExtra("pos", pos);
        
        // Activity finished ok, return the data

        setResult(RESULT_OK, data);

        // closes the activity and returns to the first screen
        finish();
    }
}
