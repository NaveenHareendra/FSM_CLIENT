/*package com.example.agrisiteclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class DivisionRecyclerViewAdapter extends RecyclerView.Adapter<DivisionRecyclerViewAdapter.MyViewHolder> {

    private final List<Divisions> items; //Items Array List
    private final Context context; //Context

    //Constructor
    public DivisionRecyclerViewAdapter(List<Divisions> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public DivisionRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DivisionRecyclerViewAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_division_recycler_view_adapter,null));
    }

    @Override
    public void onBindViewHolder(@NonNull DivisionRecyclerViewAdapter.MyViewHolder holder, int position) {
        //Getting single task related details from list
        Divisions divisons = items.get(position);

        //Setting user details to TextViews
        holder.TextViewDSC.setText(divisons.getDistrict());
        holder.TextViewAGSCCount.setText(String.valueOf(divisons.getCount()));
        //holder.TextViewAGSCCount.setText(divisons.getCount());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //MyViewHolder Class to hold View Reference for every item in the RecyclerView
    static class MyViewHolder extends RecyclerView.ViewHolder {

        //Declaring TextViews
        private final TextView TextViewDSC, TextViewAGSCCount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //Getting TextViews from task_recycler_view_adapter_layout XML file

            TextViewDSC = itemView.findViewById(R.id.TextViewDSC);
            TextViewAGSCCount = itemView.findViewById(R.id.TextViewAGSCCount);

        }
    }
}*/