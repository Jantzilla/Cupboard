package com.creativesourceapps.android.cupboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class CupboardAdapter extends RecyclerView.Adapter<CupboardAdapter.CupboardViewHolder> {

    public void CupboardAdapter() {

    }

    @NonNull
    @Override
    public CupboardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CupboardViewHolder cupboardViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CupboardViewHolder extends RecyclerView.ViewHolder {

        public CupboardViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
}
