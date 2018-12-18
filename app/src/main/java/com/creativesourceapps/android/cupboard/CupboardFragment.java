package com.creativesourceapps.android.cupboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class CupboardFragment extends Fragment {

    private GridView gridView;

    public CupboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cupboard, container, false);
        gridView = view.findViewById(R.id.cupboard_grid_view);
        getCategories();

        return view;
    }

    private void getCategories() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Handle UI here
                //Initialize adapter here

                //Set adapter here

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

//                        Recipe item_clicked = recipes.get(position);                    TODO: GET CLICKED POSITION

//                        Intent intent = new Intent(getContext(), RecipeActivity.class); TODO: CHANGE STARTED INTENT/TRANSACTION
//                        intent.putExtra("parcel_data", item_clicked);                   TODO: SEND CLICKED POSITION
//                        startActivity(intent);

                    }
                });

            }
        });

    }

}
