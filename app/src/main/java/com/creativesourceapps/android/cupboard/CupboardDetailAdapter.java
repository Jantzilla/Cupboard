package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CupboardDetailAdapter extends RecyclerView.Adapter<CupboardDetailAdapter.DetailViewHolder> {
    private ArrayList<Ingredient> ingredientsList = new ArrayList<>();

    private Context context;
    private LayoutInflater layoutInflator;

    public CupboardDetailAdapter(ArrayList<Ingredient> ingredientsList) {
        this.ingredientsList.addAll(ingredientsList);
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        layoutInflator = LayoutInflater.from(context);
        View view = layoutInflator.inflate(R.layout.list_item_cupboard_detail, viewGroup, false);
        DetailViewHolder detailViewHolder = new DetailViewHolder(view);

        return detailViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder detailViewHolder, int i) {
        detailViewHolder.ingredientTextView.setText(ingredientsList.get(i).name);
        detailViewHolder.quantityTextView.setText(String.valueOf(ingredientsList.get(i).quantity));
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ingredientTextView, quantityTextView;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);

            ingredientTextView = itemView.findViewById(R.id.tv_ingredient);
            quantityTextView = itemView.findViewById(R.id.tv_quantity);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
