package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class DetailStepFragment extends Fragment {

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
        TextView textView = (TextView) rootView.findViewById(R.id.text_description);
        final Recipe recipe = getActivity().getIntent().getParcelableExtra("parcel_data");
        final int position = getActivity().getIntent().getIntExtra("position", 0);
        textView.setText(recipe.instructions.get(position));

        // Return the root view
        return rootView;
    }
}
