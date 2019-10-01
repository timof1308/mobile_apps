package de.vms.vmsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button activity_main_buttonCreateCompany;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_company);
        setContentView(R.layout.activity_main);



        activity_main_buttonCreateCompany = findViewById(R.id.activity_main_buttonCreateCompany);
        activity_main_buttonCreateCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateCompany();
            }
        });

    }

    public void openCreateCompany(){
        Intent intent = new Intent(this, CreateCompanyActivity.class);
        startActivity(intent);
    }
}
