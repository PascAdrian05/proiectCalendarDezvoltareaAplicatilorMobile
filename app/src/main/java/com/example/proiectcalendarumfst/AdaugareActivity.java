package com.example.proiectcalendarumfst;

import android.app.TimePickerDialog;
import android.content.Intent;
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

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Vector;

public class AdaugareActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    TextView textView4;
    Button buttonOra;
    Button buttonLocatie;
    TextInputEditText notite;
    Switch switch1;
    Spinner spinner;
    Button button2;
    TextInputEditText titlu;
    String date;
    String selectedItem = "";

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
        date = getIntent().getStringExtra("date");
        textView4.setText("Adaugare eveniment in data de " + date);

        buttonOra = findViewById(R.id.buttonOra);
        switch1 = findViewById(R.id.switch1);
        spinner = findViewById(R.id.spinnerCategorii);
        button2 = findViewById(R.id.button2);
        titlu = findViewById(R.id.titlutext);
        notite = findViewById(R.id.notite);
        buttonLocatie = findViewById(R.id.buttonLocatie);

        Eveniment ev=(Eveniment) getIntent().getSerializableExtra("evenimentUpdate");
        int index = getIntent().getIntExtra("index", -1);

        if(ev!=null) {
            textView4.setText("Modificare eveniment in data de " + ev.getData());
            titlu.setText(ev.getTitlu());
            buttonOra.setText(ev.getOra());
            buttonLocatie.setText(ev.getLocatie());
            notite.setText(ev.getNote());
            selectedItem=ev.getCategorie();
            creareSpinner();
            
            if (spinner.getAdapter() != null) {
                int pos = ((ArrayAdapter<String>) spinner.getAdapter()).getPosition(selectedItem);
                spinner.setSelection(pos);
            }

            if(ev.getOra().equals("Toata ziua")) {
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
                    ev.setTitlu(titlu.getText().toString());
                    ev.setCategorie(selectedItem);
                    ev.setOra(buttonOra.getText().toString());
                    ev.setLocatie(buttonLocatie.getText().toString());
                    ev.setNote(notite.getText().toString());

                    if (index != -1) {
                        CalendarActivity.evenimente.set(index, ev);
                    }

                    finish();
                }
            });
        }
        else{
            verificare();
        }
        
        apasareOra();
        apasareLocatie();
        verficareToataZiua();
        selectieSpinner();
    }

    private boolean validareCampuri() {
        boolean isValid = true;
        if (titlu.getText().toString().isEmpty()) {
            titlu.setError("Titlu invalid");
            isValid = false;
        }
        if (!switch1.isChecked() && buttonOra.getText().toString().equals("Selectare ora")) {
            buttonOra.setError("Ora invalida");
            isValid = false;
        }
        if (!isValid) {
            Toast.makeText(AdaugareActivity.this, "Date invalide", Toast.LENGTH_SHORT).show();
        }
        return isValid;
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
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void verificare() {
        button2.setOnClickListener(v -> {
            if (validareCampuri()) {
                Eveniment eveniment = new Eveniment(titlu.getText().toString(), date, buttonOra.getText().toString(), buttonLocatie.getText().toString(), selectedItem, notite.getText().toString());
                Intent intent = new Intent(AdaugareActivity.this, CalendarActivity.class);
                intent.putExtra("eveniment", eveniment);
                startActivity(intent);
                finish();
            }
        });
    }
}
