package de.vms.vmsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class activity_company extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        Button buttonCreateCompany = findViewById(R.id.create_company);
        buttonCreateCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateCompanyActivity();
            }

        });

        Button buttonDeleteCompany = findViewById(R.id.delete_company);
        buttonDeleteCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDeleteCompanyActivity();
            }

        });


    }
    public void openCreateCompanyActivity(){
        //Intent intent = new Intent(this, CreateCompanyActivity.class);
        //startActivity(intent);

        DialogFragment newFragment = new CreateCompanyFragment();
        newFragment.show(getSupportFragmentManager(), "irgendein String");
    }

    public void openDeleteCompanyActivity(){
        //Intent intent = new Intent(this, CreateCompanyActivity.class);
        //startActivity(intent);

        DialogFragment newFragment = new DeleteCompanyFragment();
        newFragment.show(getSupportFragmentManager(), "irgendein String");
    }


}
