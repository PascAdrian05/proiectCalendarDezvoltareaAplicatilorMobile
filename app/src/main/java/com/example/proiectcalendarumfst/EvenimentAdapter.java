package com.example.proiectcalendarumfst;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EvenimentAdapter extends RecyclerView.Adapter<EvenimentAdapter.EvenimentViewHolder> {
    private List<Eveniment> evenimente;

    public EvenimentAdapter(List<Eveniment> evenimente) {
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
        Eveniment eveniment = evenimente.get(position);
        
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
            Eveniment ev = evenimente.get(poz);
            CalendarActivity.evenimente.remove(ev); 
            evenimente.remove(poz);
            notifyItemRemoved(poz);
            notifyItemRangeChanged(poz, evenimente.size());
        });

        holder.buttonUpdate.setOnClickListener(v -> {
            int poz = holder.getAbsoluteAdapterPosition();
            Eveniment ev = evenimente.get(poz);
            
            // Găsim indexul în lista globală statică
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
