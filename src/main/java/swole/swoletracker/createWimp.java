package swole.swoletracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

public class createWimp extends AppCompatActivity{
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String name;
    private String sex;
    private String weight;
    private String age;
    private String id;

    private OkHttpClient mOkHttpClient;

    EditText wimpName;
    EditText wimpSex;
    EditText wimpWeight;
    EditText wimpAge;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wimp);

        Button sendWimpInfo = (Button)findViewById(R.id.submitWimpInfo);

        wimpName = (EditText)findViewById(R.id.wimpNameInput);

        wimpSex = (EditText)findViewById(R.id.wimpSexInput);

        wimpWeight = (EditText)findViewById(R.id.wimpWeightInput);

        wimpAge = (EditText)findViewById(R.id.wimpAgeInput);

        sendWimpInfo.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {
                createWimpInfo();
                finish();
            }

        }); // end of the closeButton callback


    } // end of the onCreate() function

    private void createWimpInfo()
    {
        name = wimpName.getText().toString();
        sex = wimpSex.getText().toString();
        weight = wimpWeight.getText().toString();
        age = wimpAge.getText().toString();


        String jsonBody = "{ ";

        if(name != "")
        {
            jsonBody = jsonBody + " \"name\": \"" + name + "\",";
        }
        if(age != "")
        {
            jsonBody = jsonBody + " \"sex\": \"" + sex + "\",";
        }
        if(weight != "")
        {
            jsonBody = jsonBody + " \"weight\":" + Integer.parseInt(weight) +"," ;
        }

        if(age != "")
        {
            jsonBody = jsonBody + " \"age\":" + Integer.parseInt(age);
        }

        jsonBody = jsonBody + " }";

        // Debug
        System.out.println(jsonBody);

        mOkHttpClient = new OkHttpClient();

        HttpUrl reqURL = HttpUrl.parse("https://swole-tracker.appspot.com/wimps");

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

        }); // end of the newCall.enqueue callback


    } // end of createWimp() function
} // end of class
