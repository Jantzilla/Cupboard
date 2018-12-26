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

    private ArrayList<Recipe> recipes = new ArrayList<>();
    private Context context;

    public RecipeAdapter(ArrayList<Recipe> recipes) {
        this.recipes.addAll(recipes);
    }

    public interface ListItemClickListener {
        void onItemClickListener(int itemClicked);
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

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView buttonImageView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.grid_item_recipe);
            buttonImageView = itemView.findViewById(R.id.iv_button);
        }
    }
}
