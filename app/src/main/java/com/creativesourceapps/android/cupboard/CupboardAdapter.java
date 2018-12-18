package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
        void onItemClicked(int clickedItem, LinearLayout itemLayout);
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
        cupboardViewHolder.categoryTextView.setText(categoryList.get(i));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CupboardViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView;
        LinearLayout listItemLayout;

        public CupboardViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTextView = itemView.findViewById(R.id.tv_category);
            listItemLayout = itemView.findViewById(R.id.ll_list_item);

            listItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(getAdapterPosition(),listItemLayout);
                }
            });
        }

    }
}
