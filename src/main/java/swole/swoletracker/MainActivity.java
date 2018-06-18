package swole.swoletracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    } // end of the onCreate() function


    public void getWimpsActivity(View view)
    {
        Intent intent = new Intent(MainActivity.this, getWimps.class);
        startActivity(intent);
    }


    public void createWimpActivity(View view)
    {
        Intent intent = new Intent(MainActivity.this, createWimp.class);
        startActivity(intent);
    }

}
