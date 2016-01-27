/*
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lu.uni.psod.corsanum.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.models.fit.Action;
import lu.uni.psod.corsanum.models.fit.ActionType;
import lu.uni.psod.corsanum.models.fit.Exercise;
import lu.uni.psod.corsanum.models.fit.Position;

public class LoginActivity extends Activity {

    private Button created_shared;
    private Button exercises_button;



    // [START auth_oncreate_setup_beginning]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        exercises_button = (Button) findViewById(R.id.my_exercises_btn);
        exercises_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, MyExercisesActivity.class);
                startActivity(it);
            }
        });

        created_shared = (Button) findViewById(R.id.shared_btn);
        created_shared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();

                Exercise ex1 = new Exercise("Long run in Park");
                ex1.getActions().add(new Action(new Position(49.628025, 6.159010), new Position(49.632136, 6.150895), 0.0, ActionType.RUN));
                ex1.getActions().add(new Action(new Position(49.632136, 6.150895), new Position(49.627945, 6.142899), 0.0, ActionType.WALK_FAST));
                ex1.getActions().add(new Action(new Position(49.627945, 6.142999), new Position(49.624607, 6.145090), 2.0, ActionType.RUN_FAST));

                Exercise ex2 = new Exercise("Stretching session");
                ex2.getActions().add(new Action(new Position(0,0), new Position(0,0), 5.0, ActionType.STRETCH));
                ex2.getActions().add(new Action(new Position(0,0), new Position(0,0), 5.0, ActionType.STRETCH));
                ex2.getActions().add(new Action(new Position(0,0), new Position(0,0), 5.0, ActionType.STRETCH));

                Exercise ex3 = new Exercise("Sprinting");
                ex3.getActions().add(new Action(new Position(0,0), new Position(2,2), 0.0, ActionType.RUN));
                ex3.getActions().add(new Action(new Position(2,2), new Position(4,4), 0.0, ActionType.RUN_FAST));
                ex3.getActions().add(new Action(new Position(4,4), new Position(6,6.5), 0.0, ActionType.RUN));
                ex3.getActions().add(new Action(new Position(6,6.5), new Position(9,9), 0.0, ActionType.RUN_FAST));
                ex3.getActions().add(new Action(new Position(9, 9), new Position(9, 9), 10.0, ActionType.STRETCH));

                exerciseList.add(ex1);
                exerciseList.add(ex2);
                exerciseList.add(ex3);

                Gson gson = new Gson();
                String exercise_list_json = gson.toJson(exerciseList);

                SharedPreferences settings = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(getString(R.string.saved_exercise_list), exercise_list_json);

                editor.commit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}