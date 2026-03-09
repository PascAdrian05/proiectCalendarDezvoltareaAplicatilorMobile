package com.example.proiectcalendarumfst;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    TextView textView;
    TextView textView2;
    CalendarView calendarView;
    Button button;
    String date = "";
    
    static List<Eveniment> evenimente = new ArrayList<>();
    List<Eveniment> listaFiltrata = new ArrayList<>();
    RecyclerView recyclerViewTasks;
    EvenimentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView = findViewById(R.id.textView3);
        textView2 = findViewById(R.id.textView2);
        button = findViewById(R.id.button);
        calendarView = findViewById(R.id.calendarView);
        

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EvenimentAdapter(listaFiltrata);
        recyclerViewTasks.setAdapter(adapter);



        Eveniment nouEveniment = (Eveniment) getIntent().getSerializableExtra("eveniment");
        if (nouEveniment != null) {
            evenimente.add(nouEveniment);
        }

        String name = getIntent().getStringExtra("name");
        if (name != null) {
            textView.setText("Bine ai venit \n" + name);
        }


        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        date = day + "/" + (month + 1) + "/" + year;
        textView2.setText("Data selectată: " + date);

        filtrareEvenimente(date);
        actiune();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (date != null && !date.isEmpty()) {
            filtrareEvenimente(date);
        }
    }

    private void filtrareEvenimente(String dataSelectata) {
        listaFiltrata.clear();
        for(Eveniment e : evenimente){
            if(e.getData().equals(dataSelectata)){
                listaFiltrata.add(e);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void actiune() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                date = dayOfMonth + "/" + (month + 1) + "/" + year;
                textView2.setText("Data selectată: " + date);
                
                filtrareEvenimente(date);

                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);

                if (selectedDate.before(today)) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }
        });

        button.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, AdaugareActivity.class);
            intent.putExtra("date", date);
            startActivity(intent);
        });
    }
}
