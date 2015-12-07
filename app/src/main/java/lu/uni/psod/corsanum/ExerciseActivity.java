package lu.uni.psod.corsanum;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ExerciseActivity extends ActionBarActivity {

    TextView tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        tv = (TextView) findViewById(R.id.exc_name_id);

        String title = getIntent().getStringExtra("title");
        tv.setText(title);

    }
}
