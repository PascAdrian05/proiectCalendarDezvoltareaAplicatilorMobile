package com.example.proiectcalendarumfst;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proiectcalendarumfst.ConectareBackEnd.ApiService;
import com.example.proiectcalendarumfst.ConectareBackEnd.DtoEveniment;
import com.example.proiectcalendarumfst.ConectareBackEnd.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdaugareActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    TextView textView4;
    Button buttonOra;
    Button buttonLocatie;
    TextInputEditText notite;
    Switch switch1;
    Spinner spinner;
    Button button2;
    TextInputEditText titlu;
    LocalDate date;
    String selectedItem = "";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final ActivityResultLauncher<Intent> mapLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String strada = result.getData().getStringExtra("strada");
                    if (strada != null) {
                        buttonLocatie.setText("Strada: " + strada);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adaugare);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView4 = findViewById(R.id.textView4);
        date = (LocalDate) getIntent().getSerializableExtra("date");
        if (date != null) {
            textView4.setText("Adaugare eveniment in data de " + date.format(formatter));
        }

        buttonOra = findViewById(R.id.buttonOra);
        switch1 = findViewById(R.id.switch1);
        spinner = findViewById(R.id.spinnerCategorii);
        button2 = findViewById(R.id.button2);
        titlu = findViewById(R.id.titlutext);
        notite = findViewById(R.id.notite);
        buttonLocatie = findViewById(R.id.buttonLocatie);

        DtoEveniment ev = (DtoEveniment) getIntent().getSerializableExtra("evenimentUpdate");
        int index = getIntent().getIntExtra("index", -1);

        if(ev != null) {
            textView4.setText("Modificare eveniment in data de " + LocalDate.parse(ev.getData()).format(formatter));
            titlu.setText(ev.getTitlu());
            buttonOra.setText(ev.getOra());
            buttonLocatie.setText(ev.getLocatie());
            notite.setText(ev.getNote());
            selectedItem = ev.getCategorie();
            creareSpinner();
            
            if (spinner.getAdapter() != null) {
                int pos = ((ArrayAdapter<String>) spinner.getAdapter()).getPosition(selectedItem);
                spinner.setSelection(pos);
            }

            if("Toata ziua".equals(ev.getOra())) {
                switch1.setChecked(true);
                buttonOra.setEnabled(false);
            }
            else {
                switch1.setChecked(false);
                buttonOra.setEnabled(true);
            }
            button2.setText("Modifica eveniment");

            button2.setOnClickListener(v -> {
                if (validareCampuri()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
                    String token = sharedPreferences.getString("token", null);

                    if (token != null) {
                        String tokenptrServer = "Bearer " + token;

                        // Actualizăm obiectul local
                        ev.setTitlu(titlu.getText().toString());
                        ev.setCategorie(selectedItem);
                        ev.setOra(buttonOra.getText().toString());
                        ev.setLocatie(buttonLocatie.getText().toString());
                        ev.setNote(notite.getText().toString());

                        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                        // Apelăm serverul pentru UPDATE
                        apiService.updateEveniment(tokenptrServer, ev.getId(), ev).enqueue(new Callback<DtoEveniment>() {
                            @Override
                            public void onResponse(Call<DtoEveniment> call, Response<DtoEveniment> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(AdaugareActivity.this, "Eveniment modificat!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(AdaugareActivity.this, "Eroare la modificare: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<DtoEveniment> call, Throwable t) {
                                Toast.makeText(AdaugareActivity.this, "Eroare rețea", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
        else {
            // Logica pentru ADAUGARE (rămâne neschimbată)
            button2.setOnClickListener(v -> {
                if (validareCampuri()) {
                    SharedPreferences sharedPreferences=getSharedPreferences("preferences",MODE_PRIVATE);
                    String token=sharedPreferences.getString("token",null);
                    if(token!=null){
                        String tokenptrServer="Bearer "+token;
                        String dataFormatataPentruServer = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        DtoEveniment evenimentReq = new DtoEveniment(titlu.getText().toString(), dataFormatataPentruServer, buttonOra.getText().toString(), buttonLocatie.getText().toString(), selectedItem, notite.getText().toString());
                        
                        ApiService apiService= RetrofitClient.getClient().create(ApiService.class);
                        apiService.adaugareEveniment(evenimentReq, tokenptrServer).enqueue(new Callback<DtoEveniment>() {
                            @Override
                            public void onResponse(Call<DtoEveniment> call, Response<DtoEveniment> response) {
                                if(response.isSuccessful()){
                                    finish();
                                }
                            }
                            @Override
                            public void onFailure(Call<DtoEveniment> call, Throwable t) {}
                        });
                    }
                }
            });
        }
        
        apasareOra();
        apasareLocatie();
        verficareToataZiua();
        selectieSpinner();
    }

    private boolean validareCampuri() {
        if (titlu.getText().toString().isEmpty()) {
            titlu.setError("Titlu invalid");
            return false;
        }
        return true;
    }

    public void apasareOra() {
        buttonOra.setOnClickListener(v -> {
            TimePickerFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
        buttonOra.setText(formattedTime);
    }

    public void apasareLocatie() {
        buttonLocatie.setOnClickListener(v -> {
            Intent intent = new Intent(AdaugareActivity.this, MapsActivity.class);
            mapLauncher.launch(intent);
        });
    }

    public void verficareToataZiua() {
        switch1.setOnClickListener(v -> {
            if (switch1.isChecked()) {
                buttonOra.setText("Toata ziua");
                buttonOra.setEnabled(false);
            } else {
                buttonOra.setText("Selectare ora");
                buttonOra.setEnabled(true);
            }
        });
    }

    public void creareSpinner() {
        Vector<String> vector = new Vector<>();
        vector.add("Categorie");
        vector.add("Magazin");
        vector.add("Sport");
        vector.add("Zi nastere");
        vector.add("Tuns");
        vector.add("Zbor");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vector);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void selectieSpinner() {
        creareSpinner();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Categorie")) {
                    selectedItem = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
