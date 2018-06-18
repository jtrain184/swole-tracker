package swole.swoletracker;

import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.support.annotation.RequiresApi;
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
import android.widget.LinearLayout;
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

public class modifyWimpInfo extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String id;
    private String sex;
    private String weight;
    private String name;
    private String age;

    private OkHttpClient mOkHttpClient;
    private ListView simpleListView;

    EditText wimpName;
    EditText wimpSex;
    EditText wimpWeight;
    EditText wimpAge;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Bundle bundle = getIntent().getExtras();

        id = bundle.getString("id");
        name = bundle.getString("name");
        sex = bundle.getString("sex");
        weight = bundle.getString("weight");
        age = bundle.getString("age");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_human_info);

        Button sendWimpInfo = (Button)findViewById(R.id.submitWimpInfo);

        wimpName = (EditText)findViewById(R.id.wimpNameInput);
        wimpName.setText(name);

        wimpSex = (EditText)findViewById(R.id.wimpSexInput);
        wimpSex.setText(sex);

        wimpWeight = (EditText)findViewById(R.id.wimpWeightInput);
        wimpWeight.setText(weight);

        wimpAge = (EditText)findViewById(R.id.wimpAgeInput);
        wimpAge.setText(age);

        sendWimpInfo.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {
                sendHumanInfo();
                finish();
            }

        }); // end of the closeButton callback


    } // end of onCreate function


    private void sendHumanInfo()
    {

        String newName = wimpName.getText().toString() ;
        String newSex = wimpSex.getText().toString() ;
        String newWeight = wimpWeight.getText().toString() ;
        String newAge = wimpAge.getText().toString() ;

        String jsonBody = "{ ";

        if(newName != "")
        {
            jsonBody = jsonBody + " \"name\": \"" + newName + "\",";
        }

        if(newSex != "")
        {
            jsonBody = jsonBody + " \"sex\": \"" + newSex + "\",";
        }

        if(newWeight != "")
        {
            jsonBody = jsonBody + " \"weight\": " + Integer.parseInt(newWeight) + ", ";
        }

        if(newAge != "")
        {
            jsonBody = jsonBody + " \"age\": " + Integer.parseInt(newAge);
        }

        jsonBody = jsonBody + " }";

        System.out.println(jsonBody);

        mOkHttpClient = new OkHttpClient();

        HttpUrl reqURL = HttpUrl.parse("https://swole-tracker.appspot.com/wimps/" + id);

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