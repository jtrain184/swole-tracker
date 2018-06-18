package swole.swoletracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class modifyWorkoutInfo extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String id;
    private String name;
    private String reps;
    private String weight;
    private String duration;

    private OkHttpClient mOkHttpClient;
    private ListView simpleListView;

    EditText workoutName;
    EditText workoutReps;
    EditText workoutDuration;
    EditText workoutWeight;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Bundle bundle = getIntent().getExtras();

        id = bundle.getString("workout_id");
        name = bundle.getString("name");
        reps = bundle.getString("reps");
        weight = bundle.getString("weight");
        duration = bundle.getString("duration");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_workout_info);

        Button sendWorkoutInfo = (Button)findViewById(R.id.submitWorkoutInfo);

        workoutName = (EditText)findViewById(R.id.workoutNameInput);
        workoutName.setText(name);

        workoutReps = (EditText)findViewById(R.id.workoutRepsInput);
        workoutReps.setText(reps);

        workoutDuration = (EditText)findViewById(R.id.workoutDurationInput);
        workoutDuration.setText(duration);

        workoutWeight = (EditText)findViewById(R.id.workoutWeightInput);
        workoutWeight.setText(weight);

        sendWorkoutInfo.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {
                sendWorkoutInfo();
                Intent intent = new Intent(modifyWorkoutInfo.this,getWimps.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        });


    } // end of onCreate function


    private void sendWorkoutInfo()
    {

        String newName = workoutName.getText().toString() ;
        String newReps = workoutReps.getText().toString() ;
        String newWeight = workoutWeight.getText().toString() ;
        String newDuration = workoutDuration.getText().toString() ;

        String jsonBody = "{ ";

        if(newName != "")
        {
            jsonBody = jsonBody + " \"name\": \"" + newName + "\",";
        }

        if(newReps != "")
        {
            jsonBody = jsonBody + " \"reps\": " + Integer.parseInt(newReps) + ", ";
        }

        if(newWeight != "")
        {
            jsonBody = jsonBody + " \"weight\": " + Integer.parseInt(newWeight) + ", ";
        }

        if(newDuration != "")
        {
            jsonBody = jsonBody + " \"duration\": " + Integer.parseInt(newDuration);
        }

        jsonBody = jsonBody + " }";

        System.out.println(jsonBody);

        mOkHttpClient = new OkHttpClient();

        HttpUrl reqURL = HttpUrl.parse("https://swole-tracker.appspot.com/workouts/" + id);

        System.out.println("Reqs body is: " + reqURL);

        RequestBody body = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(reqURL)
                .patch(body)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String r = response.body().string();

                System.out.print(r);

            }

        }); // end of the newCall.enqueue

    }

}

