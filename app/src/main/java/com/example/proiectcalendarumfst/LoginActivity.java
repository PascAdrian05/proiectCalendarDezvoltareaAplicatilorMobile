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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private Button btnLoginSubmit;
    private TextInputEditText txtname;
    private TextInputEditText txtPassword;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit);
        txtname = findViewById(R.id.txtname);
        txtPassword = findViewById(R.id.txtPassword);

        submit();

    }


    public void submit() {
        btnLoginSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              String  name = txtname.getText().toString();
                String password = txtPassword.getText().toString();
                String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[^\\s]{5,}$";
                boolean isValid=true;
                if((name.contains(" "))||name.length()<5){
                    txtname.setError("Username invalid");
                    isValid=false;
                }
                if((password.contains(" "))||!password.matches(passwordPattern)){
                    txtPassword.setError("Password invalid");
                    isValid=false;
                }
                if(isValid){
                    txtname.setError(null);
                    txtPassword.setError(null);
                    Toast.makeText(LoginActivity.this, "Logat cu succes ("+name+")", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoginActivity.this,CalendarActivity.class);
                    intent.putExtra("name",name);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Date invalide ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}