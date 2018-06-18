package swole.swoletracker;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import android.os.Bundle;


public class createWorkout extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String name;
    private String reps;
    private String duration;
    private String weight;
    private String user;

    private OkHttpClient mOkHttpClient;

    EditText workoutName;
    EditText workoutReps;
    EditText workoutDuration;
    EditText workoutWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Bundle bundle = getIntent().getExtras();

        user = bundle.getString("id");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        Button sendDogInfo = (Button)findViewById(R.id.submitWorkoutInfo);

        workoutName = (EditText)findViewById(R.id.workoutNameInput);
        workoutReps = (EditText)findViewById(R.id.workoutRepsInput);
        workoutDuration = (EditText)findViewById(R.id.workoutDurationInput);
        workoutWeight = (EditText)findViewById(R.id.workoutWeightInput);

        sendDogInfo.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {
                submitNewDogInfo();
                finish();
            }

        });


    }

    private void submitNewDogInfo()
    {
        name = workoutName.getText().toString() ;
        reps = workoutReps.getText().toString();
        duration = workoutDuration.getText().toString();
        weight = workoutWeight.getText().toString();

        String jsonBody = "{ ";

        if(name != "")
        {
            jsonBody = jsonBody + " \"name\": \"" + name + "\",";
        }
        if(user != "")
        {
            jsonBody = jsonBody + " \"user\": \"" + user + "\",";
        }
        if(reps != "")
        {
            jsonBody = jsonBody + " \"reps\": " + Integer.parseInt(reps) + ", ";
        }
        if(duration != "")
        {
            jsonBody = jsonBody + " \"duration\": " + Integer.parseInt(duration) + ", ";
        }
        if(weight != "")
        {
            jsonBody = jsonBody + " \"weight\": " + Integer.parseInt(weight);
        }

        jsonBody = jsonBody + " }";

        System.out.println(jsonBody);

        mOkHttpClient = new OkHttpClient();

        HttpUrl reqURL = HttpUrl.parse("https://swole-tracker.appspot.com/workouts");

        RequestBody body = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(reqURL)
                .post(body)
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

        });

    }
}