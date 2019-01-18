package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private ListItemClickListener clickLister;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private String type;
    private Context context;

    public RecipeAdapter(ArrayList<Recipe> recipes, ListItemClickListener clickListener, String type) {
        this.recipes.addAll(recipes);
        this.clickLister = clickListener;
        this.type = type;
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
    public void onBindViewHolder(@NonNull final RecipeViewHolder recipeViewHolder, int i) {
        recipeViewHolder.titleTextView.setText(recipes.get(i).title);
        if(type.equals("Recipes"))
            recipeViewHolder.buttonImageView.setImageResource(R.drawable.bookmark);
        else
            recipeViewHolder.buttonImageView.setImageResource(R.drawable.delete);

        Glide.with(context).load(recipes.get(i).media).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    recipeViewHolder.imageView.setBackground(resource);
                }
            }
        });

        for(int o = 0; o < recipes.get(i).ingredients.size(); o++) {
            if(!IngredientAddFragment.searchIngredients(context,CupboardContract.Ingredients.
                    TABLE_NAME,recipes.get(i).ingredients.get(o)).name.equals("")) {
                recipeViewHolder.availableCount++;
            }
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        ImageView buttonImageView;
        ImageView imageView;
        int availableCount;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.grid_item_recipe);
            buttonImageView = itemView.findViewById(R.id.iv_button);
            imageView = itemView.findViewById(R.id.iv_recipe);

            buttonImageView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickLister.onItemClickListener(getAdapterPosition(), v);
        }
    }
}
