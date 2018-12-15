package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class StepListAdapter extends RecyclerView.Adapter<StepListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> steps;
    private Recipe recipe;
    private OnStepClickListener mCallback;

    // OnStepClickListener interface, calls a method in the host activity named OnStepSelected
    public interface OnStepClickListener {
        void onStepSelected(int position, Recipe recipe);
    }

    /**
     * Constructor method
     * @param steps The list of recipes to display
     * @param recipe
     */
    public StepListAdapter(Context context, ArrayList<String> steps, Recipe recipe) {
        this.context = context;
        this.steps = steps;
        this.recipe = recipe;
        mCallback = (OnStepClickListener) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_step_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // Set the text resource and return the newly created TextView
        String step = steps.get(position);
        holder.stepTextView.setText(step);
        if(position == 0)
            holder.numberTextView.setText("");
        else
            holder.numberTextView.setText(String.valueOf(position));
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onStepSelected(holder.getAdapterPosition(), recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.steps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView stepTextView;
        private TextView numberTextView;
        private View parentView;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.parentView = view;
            this.stepTextView = (TextView)view.findViewById(R.id.list_item_step);
            this.numberTextView = (TextView)view.findViewById(R.id.tv_step_number);
        }
    }
}
