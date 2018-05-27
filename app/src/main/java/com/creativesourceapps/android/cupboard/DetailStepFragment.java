package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DetailStepFragment extends Fragment {
    int position;

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    // Mandatory empty constructor
    public DetailStepFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_detail_step, container, false);
        final TextView textView = (TextView) rootView.findViewById(R.id.text_description);
        Button previousBtn = (Button) rootView.findViewById(R.id.buttonA);
        Button nextBtn = (Button) rootView.findViewById(R.id.buttonB);
        final Recipe recipe = getActivity().getIntent().getParcelableExtra("parcel_data");
        position = getActivity().getIntent().getIntExtra("position", 0);
        textView.setText(recipe.instructions.get(position));

         previousBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(position != 0) {
                     position -= 1;
                     textView.setText(recipe.instructions.get(position));
                 } else {
                     position = recipe.instructions.size() - 1;
                     textView.setText(recipe.instructions.get(recipe.instructions.size() - 1));
                 }
             }
         });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position != recipe.instructions.size() - 1) {
                    position += 1;
                    textView.setText(recipe.instructions.get(position));
                } else {
                    position = 0;
                    textView.setText(recipe.instructions.get(0));
                }
            }
        });

        // Return the root view
        return rootView;
    }
}
