package swole.swoletracker;

import android.support.v7.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import android.os.Bundle;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;


public class getWimps extends AppCompatActivity {
    private OkHttpClient mOkHttpClient;
    private ListView simpleListView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_wimps);

        simpleListView = (ListView)findViewById(R.id.wimpsListView);

        updateList();


        ((Button)findViewById(R.id.getPostsButton)).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                updateList();
            }

        });

    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        updateList();

    }

    private void updateList()
    {

        mOkHttpClient = new OkHttpClient();

        HttpUrl reqURL = HttpUrl.parse("https://swole-tracker.appspot.com/wimps");

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
                System.out.println(r);

                try
                {
                    JSONArray j = new JSONArray(r);
                    System.out.println(j);

                    final List<Map<String,String>> posts = new ArrayList<Map<String,String>>();

                    for(int i = 0; i < j.length(); i++)
                    {

                        HashMap<String, String> m = new HashMap<String, String>();
                        m.put("Name", j.getJSONObject(i).getString("name"));
                        m.put("Sex", j.getJSONObject(i).getString("sex"));
                        m.put("Weight", j.getJSONObject(i).getString("weight"));
                        m.put("Age", j.getJSONObject(i).getString("age"));
                        m.put("ID", j.getJSONObject(i).getString("id"));
                        posts.add(m);

                    }

                    final SimpleAdapter postAdapter = new SimpleAdapter(
                            getWimps.this,
                            posts,
                            R.layout.wimp_entity_layout,
                            new String[]{"Name", "Sex", "Weight", "Age"},
                            new int[]{R.id.wimp_name, R.id.wimp_sex, R.id.wimp_weight,R.id.wimp_age});

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            simpleListView.setAdapter(postAdapter);

                            simpleListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                                {

                                    Intent intent = new Intent(getWimps.this, wimpInformation.class);

                                    String wimpID = posts.get(position).get("ID");

                                    intent.putExtra("id", wimpID);
                                    intent.putExtra("name", posts.get(position).get("Name"));
                                    intent.putExtra("sex", posts.get(position).get("Sex"));
                                    intent.putExtra("weight", posts.get(position).get("Weight"));
                                    intent.putExtra("age", posts.get(position).get("Age"));

                                    startActivity(intent);

                                } // end of the onItemClick

                            }); // end of the setOnItemClickListener()

                        }

                    }); // end of the Runnable callback

                } // end of inner try

                catch (JSONException e1)
                {
                    e1.printStackTrace();
                }


            } // end of the onResponse
        }); // end of the newCall.enqueue

    } // end of the updateList()

}
