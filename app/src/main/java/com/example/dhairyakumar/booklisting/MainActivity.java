package com.example.dhairyakumar.booklisting;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button search;
    EditText keyword_editText;
    String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyword_editText = findViewById(R.id.editText1);
        search= findViewById(R.id.button1);
        search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v==search)
        {
            Intent i = new Intent(MainActivity.this,ProcessingActivity.class);
            i.putExtra("keyword",keyword_editText.getText().toString());
            startActivity(i);
        }
    }
}
