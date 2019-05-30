package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CupboardAdapter extends RecyclerView.Adapter<CupboardAdapter.CupboardViewHolder> {
    private ArrayList<String> categoryList = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    public CupboardAdapter(ArrayList<String> categoryList, ItemClickListener clickListener) {
        this.categoryList.addAll(categoryList);
        this.clickListener = clickListener;
    }

    public interface ItemClickListener{
        void onItemClicked(int clickedItem, CardView itemLayout, Object tag);
    }

    @NonNull
    @Override
    public CupboardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item_cupboard, viewGroup, false);

        CupboardViewHolder viewHolder = new CupboardViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CupboardViewHolder cupboardViewHolder, int i) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            cupboardViewHolder.listItemLayout.setTransitionName(categoryList.get(i));

        switch (i) {
            case 0:
                cupboardViewHolder.imageView.setVisibility(View.GONE);
                cupboardViewHolder.imageView.setTag("ingredients");
                cupboardViewHolder.textView.setVisibility(View.VISIBLE);
                cupboardViewHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32);
                break;
            case 8:
                cupboardViewHolder.imageView.setImageResource(R.drawable.wine);
                cupboardViewHolder.textView.setText(context.getString(R.string.spirits_beverages));
                cupboardViewHolder.imageView.setTag("wine");
                break;
            case 7:
                cupboardViewHolder.imageView.setImageResource(R.drawable.spices);
                cupboardViewHolder.textView.setText(context.getString(R.string.spices_baking));
                cupboardViewHolder.imageView.setTag("spices");
                break;
            case 6:
                cupboardViewHolder.imageView.setImageResource(R.drawable.bread);
                cupboardViewHolder.textView.setText(context.getString(R.string.bread_grains));
                cupboardViewHolder.imageView.setTag("bread");
                break;
            case 9:
                cupboardViewHolder.imageView.setImageResource(R.drawable.olive_oil);
                cupboardViewHolder.textView.setText(context.getString(R.string.sauces_vinegar));
                cupboardViewHolder.imageView.setTag("oil");
                break;
            case 10:
                cupboardViewHolder.imageView.setImageResource(R.drawable.cans);
                cupboardViewHolder.textView.setText(context.getString(R.string.soups_canned));
                cupboardViewHolder.imageView.setTag("cans");
                break;
            case 5:
                cupboardViewHolder.imageView.setImageResource(R.drawable.snacks);
                cupboardViewHolder.textView.setText(context.getString(R.string.snacks_candy));
                cupboardViewHolder.imageView.setTag("snacks");
                break;
            case 4:
                cupboardViewHolder.imageView.setImageResource(R.drawable.cheese);
                cupboardViewHolder.textView.setText(context.getString(R.string.dairy_eggs));
                cupboardViewHolder.imageView.setTag("dairy");
                break;
            case 2:
                cupboardViewHolder.imageView.setImageResource(R.drawable.pasta);
                cupboardViewHolder.textView.setText(context.getString(R.string.pasta_rice));
                cupboardViewHolder.imageView.setTag("pasta");
                break;
            case 3:
                cupboardViewHolder.imageView.setImageResource(R.drawable.produce);
                cupboardViewHolder.textView.setText(context.getString(R.string.fruits_vegetables));
                cupboardViewHolder.imageView.setTag("produce");
                break;
            case 1:
                cupboardViewHolder.imageView.setImageResource(R.drawable.meat);
                cupboardViewHolder.textView.setText(context.getString(R.string.meat_seafood));
                cupboardViewHolder.imageView.setTag("meat");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CupboardViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CardView listItemLayout;
        ImageView imageView;

        public CupboardViewHolder(@NonNull View itemView) {
            super(itemView);

            listItemLayout = itemView.findViewById(R.id.ll_list_item);
            imageView = itemView.findViewById(R.id.iv_category);
            textView = itemView.findViewById(R.id.tv_category_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(getAdapterPosition(),listItemLayout, imageView.getTag());
                }
            });
        }

    }
}
