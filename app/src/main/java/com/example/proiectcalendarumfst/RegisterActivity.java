package com.example.proiectcalendarumfst;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {


    private Button btnRegisterSubmit;
    private TextInputEditText textUserName;
    private TextInputEditText textEmail;
    private TextInputEditText textParola;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        textUserName = findViewById(R.id.textUserName);
        textEmail = findViewById(R.id.textEmail);
        textParola = findViewById(R.id.textParola);
        submit();
    }

    public void submit() {
        btnRegisterSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = textUserName.getText().toString();
                String email = textEmail.getText().toString().trim();
                String password = textParola.getText().toString().trim();
                String patternEmail = "^[A-Za-z0-9._%+-]+@(gmail\\.com|yahoo\\.com)$";
                String patternPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[^\\s]{5,}$";
                boolean isValid=true;

                if(name.length()<5||name.contains(" ")){
                    textUserName.setError("Username invalid");
                    isValid=false;
                }
                if(!email.matches(patternEmail)||email.contains(" ")){
                    textEmail.setError("Email invalid");
                    isValid=false;
                }
                if(!password.matches(patternPassword)||password.contains(" ")){
                    textParola.setError("Password invalid");
                    isValid=false;
                }
                if(isValid==true){
                    textUserName.setError(null);
                    textEmail.setError(null);
                    textParola.setError(null);

                    Toast.makeText(RegisterActivity.this, "Cont creat cu succes ("+name+")", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegisterActivity.this, CalendarActivity.class);
                    intent.putExtra("name",name);
                    startActivity(intent);
                    finish();

                }
                else{
                    Toast.makeText(RegisterActivity.this, "Date invalide ", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }



}