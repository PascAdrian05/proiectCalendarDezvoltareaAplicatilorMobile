package com.example.proiectcalendarumfst;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proiectcalendarumfst.ConectareBackEnd.ApiService;
import com.example.proiectcalendarumfst.ConectareBackEnd.DTOutilizatori;
import com.example.proiectcalendarumfst.ConectareBackEnd.DtoResponseUtilizator;
import com.example.proiectcalendarumfst.ConectareBackEnd.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

                    DTOutilizatori request=new DTOutilizatori(name,email,password);
                    ApiService service= RetrofitClient.getClient().create(ApiService.class);
                    Call<DtoResponseUtilizator> apel=service.register(request);
                    apel.enqueue(new Callback<DtoResponseUtilizator>() {
                        @Override
                        public void onResponse(Call<DtoResponseUtilizator> call, Response<DtoResponseUtilizator> response) {
                            if(response.isSuccessful()){
                                SharedPreferences preferences=getSharedPreferences("preferences",MODE_PRIVATE);
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putString("token",response.body().getToken()).apply();
                                editor.putString("nume",response.body().getNume()).apply();
                                editor.putString("email",response.body().getEmail()).apply();

                                textUserName.setError(null);
                                textEmail.setError(null);
                                textParola.setError(null);
                                Toast.makeText(RegisterActivity.this, "Cont creat cu succes ("+name+")", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else{
                                Toast.makeText(RegisterActivity.this,"Date Incorecte",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<DtoResponseUtilizator> call, Throwable throwable) {
                            Toast.makeText(RegisterActivity.this,"Failed response server",Toast.LENGTH_SHORT).show();
                        }
                    });


                }
                else{
                    Toast.makeText(RegisterActivity.this, "Date invalide ", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }



}