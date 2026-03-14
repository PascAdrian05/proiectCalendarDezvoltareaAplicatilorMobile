package com.example.proiectcalendarumfst;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proiectcalendarumfst.ConectareBackEnd.ApiService;
import com.example.proiectcalendarumfst.ConectareBackEnd.DtoEveniment;
import com.example.proiectcalendarumfst.ConectareBackEnd.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvenimentAdapter extends RecyclerView.Adapter<EvenimentAdapter.EvenimentViewHolder> {
    private List<DtoEveniment> evenimente;

    public EvenimentAdapter(List<DtoEveniment> evenimente) {
        this.evenimente = evenimente;
    }

    @NonNull
    @Override
    public EvenimentAdapter.EvenimentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eveniment, parent, false);
        return new EvenimentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EvenimentAdapter.EvenimentViewHolder holder, int position) {
        DtoEveniment eveniment = evenimente.get(position);
        
        String info = "";
        info += "Titlu: " + eveniment.getTitlu() + "\nora: " + eveniment.getOra();
        if (eveniment.getLocatie() != null && !eveniment.getLocatie().equals("Locatie prin Google Maps") && !eveniment.getLocatie().isEmpty()) {
            info += "\nLocatie: " + eveniment.getLocatie();
        }
        if (eveniment.getCategorie() != null && !eveniment.getCategorie().equals("Categorie") && !eveniment.getCategorie().isEmpty()) {
            info += "\nCategorie: " + eveniment.getCategorie();
        }

        holder.textViewDateEveniment.setText(info);

        holder.buttonDelete.setOnClickListener(v -> {
            int poz = holder.getAbsoluteAdapterPosition();
            if (poz == RecyclerView.NO_POSITION) return;

            DtoEveniment evenimentDeSters = evenimente.get(poz);

            SharedPreferences preferences = v.getContext().getSharedPreferences("preferences", MODE_PRIVATE);
            String token = preferences.getString("token", null);
            if (token != null) {
                String tokenptrServer = "Bearer " + token;
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                
                // Log pentru debug (opțional)
                // System.out.println("Stergere eveniment ID: " + evenimentDeSters.getId());

                Call<Void> da = apiService.stergereEveniment(tokenptrServer, evenimentDeSters.getId());
                da.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Eliminăm din ambele liste pentru a păstra consistența
                            CalendarActivity.evenimente.remove(evenimentDeSters);
                            evenimente.remove(poz);
                            
                            notifyItemRemoved(poz);
                            notifyItemRangeChanged(poz, evenimente.size());
                            Toast.makeText(v.getContext(), "Eveniment șters cu succes", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "Eroare server: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable throwable) {
                        Toast.makeText(v.getContext(), "Eroare rețea: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(v.getContext(), "Nu există token", Toast.LENGTH_SHORT).show();
            }
        });

        holder.buttonUpdate.setOnClickListener(v -> {
            int poz = holder.getAbsoluteAdapterPosition();
            if (poz == RecyclerView.NO_POSITION) return;
            
            DtoEveniment ev = evenimente.get(poz);
            int globalIndex = CalendarActivity.evenimente.indexOf(ev);

            Intent intent = new Intent(v.getContext(), AdaugareActivity.class);
            intent.putExtra("evenimentUpdate", ev);
            intent.putExtra("index", globalIndex);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return evenimente != null ? evenimente.size() : 0;
    }

    public static class EvenimentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDateEveniment;
        Button buttonDelete, buttonUpdate;

        public EvenimentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDateEveniment = itemView.findViewById(R.id.textViewDateEveniment);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
        }
    }
}
