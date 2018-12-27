package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private ListItemClickListener clickLister;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private Context context;

    public RecipeAdapter(ArrayList<Recipe> recipes, ListItemClickListener clickListener) {
        this.recipes.addAll(recipes);
        this.clickLister = clickListener;
    }

    public void remove(int itemClicked) {
        recipes.remove(itemClicked);
        notifyItemRemoved(itemClicked);
    }

    public interface ListItemClickListener {
        void onItemClickListener(int itemClicked, View view);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.grid_recipe_layout,viewGroup,false);
        RecipeViewHolder viewHolder = new RecipeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int i) {
        recipeViewHolder.titleTextView.setText(recipes.get(i).title);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        ImageView buttonImageView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.grid_item_recipe);
            buttonImageView = itemView.findViewById(R.id.iv_button);

            buttonImageView.setOnClickListener(this);
            titleTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickLister.onItemClickListener(getAdapterPosition(), v);
        }
    }
}
