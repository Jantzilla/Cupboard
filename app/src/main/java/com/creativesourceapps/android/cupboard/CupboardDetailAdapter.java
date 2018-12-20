package com.creativesourceapps.android.cupboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class CupboardDetailAdapter extends RecyclerView.Adapter<CupboardDetailAdapter.DetailViewHolder> {

    public CupboardDetailAdapter() {

    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder detailViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
