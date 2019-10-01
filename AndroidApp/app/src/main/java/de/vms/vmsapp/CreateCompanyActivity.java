package de.vms.vmsapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class CreateCompanyActivity extends AppCompatActivity {

    private Button activity_create_company_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_company);


        activity_create_company_button = findViewById(R.id.activity_create_company_button);
       /* activity_create_company_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                TextView activity_create_company_textID = findViewById(R.id.activity_create_company_textID);
                activity_create_company_textID.setText("Geändert");
            }
        });
        */


    }
}