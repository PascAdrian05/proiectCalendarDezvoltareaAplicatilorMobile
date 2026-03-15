package com.example.proiectcalendarumfst;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proiectcalendarumfst.ConectareBackEnd.ApiService;
import com.example.proiectcalendarumfst.ConectareBackEnd.DtoEveniment;
import com.example.proiectcalendarumfst.ConectareBackEnd.RetrofitClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends AppCompatActivity {

    TextView textView;
    TextView textView2;
    CalendarView calendarView;
    Button button;
    LocalDate selectedDate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static List<DtoEveniment> evenimente = new ArrayList<>();
    List<DtoEveniment> listaFiltrata = new ArrayList<>();
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

        String name = getIntent().getStringExtra("name");
        if (name != null) {
            textView.setText("Bine ai venit \n" + name);
        }

        selectedDate = LocalDate.now();
        textView2.setText("Data selectată: " + selectedDate.format(formatter));

        // Am eliminat filtrareEvenimente de aici pentru că se apelează automat în onResume()
        actiune();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedDate != null) {
            filtrareEvenimente(selectedDate);
        }
    }

    private void filtrareEvenimente(LocalDate dataSelectata) {
        SharedPreferences preferences=getSharedPreferences("preferences",MODE_PRIVATE);
        String token=preferences.getString("token",null);
        if(token!=null){
            String tokenptrServer="Bearer "+token;
            ApiService apiService= RetrofitClient.getClient().create(ApiService.class);
           Call<List<DtoEveniment>> da=apiService.evenimenteleUtilizatorului(tokenptrServer);
            final ProgressDialog loading = new ProgressDialog(this);
            loading.setMessage("Se încarcă...");
            loading.show();
           da.enqueue(new Callback<List<DtoEveniment>>() {
               @Override
               public void onResponse(Call<List<DtoEveniment>> call, Response<List<DtoEveniment>> response) {
                   if(response.isSuccessful()){
                       evenimente = response.body();
                       listaFiltrata.clear(); // Curățăm lista doar după ce primim răspunsul de la server
                       if (evenimente != null) {
                           for (DtoEveniment eveniment : evenimente) {
                               if (eveniment.getData() != null && eveniment.getData().equals(dataSelectata.toString())) {
                                   listaFiltrata.add(eveniment);
                               }
                           }
                       }
                       loading.dismiss();
                       adapter.notifyDataSetChanged();
                   }
                   else{
                       Toast.makeText(CalendarActivity.this,"Eroare la incarcare evenimente",Toast.LENGTH_SHORT).show();
                       loading.dismiss();
                   }
               }

               @Override
               public void onFailure(Call<List<DtoEveniment>> call, Throwable throwable) {
                   Log.e("CalendarActivity", "Failed response server");
                   loading.dismiss();
               }
           });
        }
        else{
            Toast.makeText(CalendarActivity.this,"Nu exista token",Toast.LENGTH_SHORT).show();
        }
    }

    public void actiune() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                textView2.setText("Data selectată: " + selectedDate.format(formatter));
                
                filtrareEvenimente(selectedDate);

                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);

                if (cal.before(today)) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }
        });

        button.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, AdaugareActivity.class);
            intent.putExtra("date", selectedDate);
            startActivity(intent);
        });
    }
}
