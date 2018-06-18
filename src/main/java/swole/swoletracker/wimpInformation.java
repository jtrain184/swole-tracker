package swole.swoletracker;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;


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

public class wimpInformation extends AppCompatActivity{
    private String id;
    private String name;
    private String sex;
    private String weight;
    private String age;

    private OkHttpClient mOkHttpClient;
    private ListView simpleListView;

    List<Map<String,String>> posts = new ArrayList<Map<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        createWimp refresh = new createWimp();

        Bundle bundle = getIntent().getExtras();

        id = bundle.getString("id");
        name = bundle.getString("name");
        sex = bundle.getString("sex");
        weight = bundle.getString("weight");
        age = bundle.getString("age");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wimp_information);


        TextView nameTxtView = (TextView)findViewById(R.id.wimpNameTxtViewBox);
        nameTxtView.setText(name);

        TextView sexTxtView = (TextView)findViewById(R.id.wimpSexTxtView);
        sexTxtView.setText(sex);

        TextView weightTxtView = (TextView)findViewById(R.id.wimpWeightTxtView);
        weightTxtView.setText(weight);

        TextView ageTxtView = (TextView)findViewById(R.id.wimpAgeTxtView);
        ageTxtView.setText(age);

        simpleListView = (ListView)findViewById(R.id.workoutListWimpInfo);

        updateList();

        Button deleteButton = (Button)findViewById(R.id.deleteButton);
        Button modifyButton = (Button)findViewById(R.id.modifyWimpButton);
        Button addWorkoutButton = (Button)findViewById(R.id.addWorkoutButton);


        deleteButton.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {
                deleteWimp();
                finish();
            }

        }); // end of the closeButton callback

        modifyButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                modifyWimp();
                finish();
            }
        });


        addWorkoutButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                addWorkout();
                finish();
            }
        });
    } // end of the onCreate

    @Override
    protected void onRestart()
    {
        super.onRestart();
        updateList();
    }


    private void updateList()
    {
        mOkHttpClient = new OkHttpClient();

        System.out.println("ID inside updatelist is: ");
        System.out.println(id);

        // This pulls all workouts associated to user id
        HttpUrl reqURL = HttpUrl.parse("https://swole-tracker.appspot.com/userworkouts/" + id);
        System.out.println("request url inside updatelist is: ");
        System.out.println(reqURL);

        Request request = new Request.Builder()
                .url(reqURL)
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
                // set the response to the r variable
                String r = response.body().string();

                try
                {
                    JSONArray workouts = new JSONArray(r);
                    System.out.println("First Response");
                    System.out.println(workouts);

                    int workoutsCount = workouts.length();
                    System.out.println("Workout count");
                    System.out.println(workoutsCount);

                    // final List<Map<String,String>> posts = new ArrayList<Map<String,String>>();

                    // checking to make sure the list is not empty
                    if(workoutsCount > 0)
                    {

                        for(int i = 0; i < workoutsCount; i++)
                        {

                            // make a GET request to get the info on the single dog first
                            HttpUrl reqURL2 = HttpUrl.parse("https://swole-tracker.appspot.com/workouts/" + workouts.getJSONObject(i).getString("id"));

                            Request request2 = new Request.Builder()
                                    .url(reqURL2)
                                    .build();

                            mOkHttpClient.newCall(request2).enqueue(new Callback(){

                                @Override
                                public void onFailure(Call call, IOException e)
                                {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException
                                {
                                    String r2 = response.body().string();

                                    try
                                    {
                                        JSONObject j2 = new JSONObject(r2);
                                        System.out.println("Second Response");
                                        System.out.println(j2);


                                        HashMap<String, String> m = new HashMap<String, String>();

                                        m.put("Name", j2.getString("name"));
                                        m.put("Reps", j2.getString("reps"));
                                        m.put("Duration", j2.getString("duration"));
                                        m.put("Weight", j2.getString("weight"));
                                        m.put("ID", j2.getString("id"));


                                        posts.add(m);

                                        final SimpleAdapter postAdapter = new SimpleAdapter(
                                                wimpInformation.this,
                                                posts,
                                                R.layout.workout_entity_layout,
                                                new String[]{"Name", "Reps", "Duration", "Weight"},
                                                new int[]{R.id.workout_name, R.id.workout_reps, R.id.workout_duration, R.id.workout_weight});

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // ((ListView)findViewById(R.id.dogsListView)).setAdapter(postAdapter);
                                                simpleListView.setAdapter(postAdapter);


                                                simpleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                    @Override
                                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                                        Intent intent = new Intent(wimpInformation.this, workoutInformation.class);

                                                        String workoutID = posts.get(position).get("ID");

                                                        intent.putExtra("workout_id", workoutID);
                                                        intent.putExtra("reps", posts.get(position).get("Reps"));
                                                        intent.putExtra("duration", posts.get(position).get("Duration"));
                                                        intent.putExtra("weight", posts.get(position).get("Weight"));
                                                        intent.putExtra("name", posts.get(position).get("Name"));

                                                        startActivity(intent);


                                                    } // end of the onItemClick overridden function


                                                }); // end of the setOnItemClickListener() callback function

                                            }
                                        });
                                    } // end of inner try statement

                                    catch (JSONException e1)
                                    {
                                        e1.printStackTrace();
                                    }

                                } // end of onResponse

                            }); // end of the newCall.enqueue callback

                        } // end of inner for loop

                    }// end of if

                } // end of inner try

                catch (JSONException e1)
                {
                    e1.printStackTrace();
                }

            } // end of the onResponse

        }); // end of the newCall.enqueue

    } // end of updateList()

    /*
    Function called when the user edits a wimp
     */

    private void modifyWimp()
    {

        Intent intent = new Intent(wimpInformation.this, modifyWimpInfo.class);

        //Debug
        System.out.println("ID is: ");
        System.out.println(id);

        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("sex", sex);
        intent.putExtra("weight", weight);
        intent.putExtra("age", age);

        startActivity(intent);


    }

    /*
    Function called when the user presses delete user button
     */


    private void deleteWimp()
    {
        //Debug
        System.out.println("ID is: ");
        System.out.println(id);

        mOkHttpClient = new OkHttpClient();

        HttpUrl reqURL = HttpUrl.parse("http://swole-tracker.appspot.com/wimps/" + id);

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

    private void addWorkout()
    {
        Intent intent = new Intent(wimpInformation.this, createWorkout.class);
        //Debug
        System.out.println("ID in addWorkout() is: ");
        System.out.println(id);

        intent.putExtra("id", id);

        startActivity(intent);
    }


} // end of class