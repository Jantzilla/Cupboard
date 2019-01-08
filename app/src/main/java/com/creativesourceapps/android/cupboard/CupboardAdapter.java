package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
        void onItemClicked(int clickedItem, CardView itemLayout);
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
                Glide.with(context).load(R.drawable.ingredients).into(cupboardViewHolder.imageView);
                cupboardViewHolder.textView.setVisibility(View.VISIBLE);
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

            listItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(getAdapterPosition(),listItemLayout);
                }
            });
        }

    }
}
