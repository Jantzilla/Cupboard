package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private boolean isCupboard;
    private String ingredientBaseUrl = "https://www.themealdb.com/images/ingredients/";
    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private ItemClickListener itemClickListener;

    public IngredientListAdapter(Context context, boolean isCupboard, ArrayList<Ingredient> ingredients, ItemClickListener itemClickListener) {
        this.context = context;
        this.isCupboard = isCupboard;
        this.ingredients.addAll(ingredients);
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClickListener(String name, String quantity, String unit);
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
        String uri = ingredientBaseUrl + ingredients.get(i).name + ".png";
        Glide.with(context).load(uri).into(viewHolder.ingredientImageView);
        viewHolder.nameTextView.setText(ingredients.get(i).name);
        if(ingredients.get(i).quantity == null)
            viewHolder.quantityTextView.setVisibility(View.INVISIBLE);
        else
            viewHolder.quantityTextView.setText(ingredients.get(i).quantity);

        if(isCupboard && getAvailability(ingredients.get(i).name))
            viewHolder.availableImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ingredientImageView, availableImageView;
        TextView nameTextView, quantityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientImageView = itemView.findViewById(R.id.iv_ingredient);
            availableImageView = itemView.findViewById(R.id.iv_available);
            nameTextView = itemView.findViewById(R.id.tv_ingredient_name);
            quantityTextView = itemView.findViewById(R.id.tv_ingredient_quantity);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String name = ingredients.get(getAdapterPosition()).name;
            String quantity = ingredients.get(getAdapterPosition()).quantity;
            String unit = ingredients.get(getAdapterPosition()).unit;

            itemClickListener.onItemClickListener(name, quantity, unit);
        }
    }

    private boolean getAvailability(String name){
        boolean available = false;
        String selection = CupboardContract.Ingredients.COLUMN_NAME + " = ?";
        String[] projection, selectionArgs;
        SQLiteDatabase db;
        Cursor cursor;
        CupboardDbHelper dbHelper;
        dbHelper = new CupboardDbHelper(context);
        db = dbHelper.getReadableDatabase();

        projection = new String[] {CupboardContract.Ingredients.COLUMN_NAME};
        selectionArgs = new String[] {name};

        cursor = db.query(
                CupboardContract.Ingredients.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if(cursor.getCount() > 0)
            available = true;

        return available;
    }

}
