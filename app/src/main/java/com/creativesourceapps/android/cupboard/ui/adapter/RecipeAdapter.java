package com.creativesourceapps.android.cupboard.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.creativesourceapps.android.cupboard.R;
import com.creativesourceapps.android.cupboard.util.RecipeUtils;
import com.creativesourceapps.android.cupboard.data.model.Recipe;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private ListItemClickListener clickLister;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private String type;
    private Context context;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes, ListItemClickListener clickListener, String type) {
        this.recipes.addAll(recipes);
        this.clickLister = clickListener;
        this.type = type;
        this.context = context;
    }

    public void remove(int itemClicked) {
        recipes.remove(itemClicked);
        this.notifyItemRemoved(itemClicked);
    }

    public void add(Recipe recipe) {
        this.recipes.add(recipe);
    }

    public void clear() {
        recipes.clear();
        notifyItemRangeRemoved(0, getItemCount());
    }

    public Recipe get(int itemClicked) {
        return recipes.get(itemClicked);
    }

    public interface ListItemClickListener {
        void onItemClickListener(int itemClicked, View view);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.grid_recipe_layout,viewGroup,false);
        RecipeViewHolder viewHolder = new RecipeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeViewHolder recipeViewHolder, int i) {
        Recipe recipe = recipes.get(i);

        recipeViewHolder.titleTextView.setText(recipe.title);
        if(type.equals(context.getString(R.string.recipes)))
            recipeViewHolder.buttonImageView.setImageResource(R.drawable.add_cookbook);
        else
            recipeViewHolder.buttonImageView.setImageResource(R.drawable.minus_cookbook);

        Glide.with(context).load(recipe.media).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    recipeViewHolder.imageView.setBackground(resource);
                }
            }
        });

        recipeViewHolder.availableCount = RecipeUtils.checkAvailable(context,recipe.ingredients,recipe.quantity,recipe.unit).size();

        String ingredientFraction = recipeViewHolder.availableCount + "/" + recipe.ingredients.size();

        recipeViewHolder.availabilityTextView.setText(ingredientFraction);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView, availabilityTextView;
        ImageView buttonImageView;
        ImageView imageView;
        int availableCount;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.grid_item_recipe);
            availabilityTextView = itemView.findViewById(R.id.tv_availability);
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
