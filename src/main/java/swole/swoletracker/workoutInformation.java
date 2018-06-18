package swole.swoletracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;

import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;

import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class workoutInformation extends AppCompatActivity
{

    private String user;
    private String workout_id;
    private String name;
    private String reps;
    private String duration;
    private String weight;

    TextView nameTxtView;
    TextView repsTxtView;
    TextView durationTxtView;
    TextView weightTxtView;

    private OkHttpClient mOkHttpClient;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Bundle bundle = getIntent().getExtras();

        name = bundle.getString("name");
        duration = bundle.getString("duration");
        weight = bundle.getString("weight");
        workout_id = bundle.getString("workout_id");
        reps = bundle.getString("reps");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_information);


        nameTxtView = (TextView)findViewById(R.id.workoutNameInputField);
        nameTxtView.setText(name);

        repsTxtView = (TextView)findViewById(R.id.workoutRepsInputField);
        repsTxtView.setText(reps);

        durationTxtView = (TextView)findViewById(R.id.workoutDurationInputField);
        durationTxtView.setText(duration);

        weightTxtView = (TextView)findViewById(R.id.workoutWeightInputField);
        weightTxtView.setText(weight);

        //getOwnerName();

        Button deleteButton = (Button)findViewById(R.id.deleteButton);
        Button modifyButton = (Button)findViewById(R.id.modifyWorkoutButton);

        deleteButton.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {
                deleteWorkout();
                Intent intent = new Intent(workoutInformation.this,getWimps.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        }); // end of the closeButton callback

        modifyButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                modifyWorkout();
                finish();
            }

        }); // end of the modifyButton callback



    } // end of the onCreate() function


    private void deleteWorkout()
    {
        mOkHttpClient = new OkHttpClient();

        HttpUrl reqURL = HttpUrl.parse("https://swole-tracker.appspot.com/workouts/" + workout_id);

        Request request = new Request.Builder()
                .url(reqURL)
                .delete()
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
                System.out.println(r);
            }

        });

    }


    private void modifyWorkout()
    {

        Intent intent = new Intent(workoutInformation.this, modifyWorkoutInfo.class);

        intent.putExtra("workout_id", workout_id);
        intent.putExtra("name", name);
        intent.putExtra("reps", reps);
        intent.putExtra("duration", duration);
        intent.putExtra("weight", weight);

        startActivity(intent);

    }


}
