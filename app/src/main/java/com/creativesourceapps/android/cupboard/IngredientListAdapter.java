package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.ViewHolder> {

    private Context context;
    private String title;
    private String ingredientBaseUrl = "https://www.themealdb.com/images/ingredients/";
    private ArrayList<String> ingredient;
    private ArrayList<String> quantity;
    private ArrayList<String> unit;

    public IngredientListAdapter(Context context, String title, ArrayList<String> quantity,
                                 ArrayList<String> unit, ArrayList<String> ingredient) {
        this.context = context;
        this.title = title;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }

    @NonNull
    @Override
    public IngredientListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_item_ingredient, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientListAdapter.ViewHolder viewHolder, int i) {
        String uri = ingredientBaseUrl + ingredient.get(i) + ".png";
        Glide.with(context).load(uri).into(viewHolder.ingredientImageView);
        viewHolder.nameTextView.setText(ingredient.get(i));
        viewHolder.quantityTextView.setText(quantity.get(i));
    }

    @Override
    public int getItemCount() {
        return ingredient.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ingredientImageView, availableImageView;
        TextView nameTextView, quantityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientImageView = itemView.findViewById(R.id.iv_ingredient);
            availableImageView = itemView.findViewById(R.id.iv_available);
            nameTextView = itemView.findViewById(R.id.tv_ingredient_name);
            quantityTextView = itemView.findViewById(R.id.tv_ingredient_quantity);
        }
    }

}
