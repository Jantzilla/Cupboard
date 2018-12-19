package com.creativesourceapps.android.cupboard;

import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class DetailCupboardFragment extends Fragment {
    ConstraintLayout constraintLayout;
    ImageView imageView;

    public DetailCupboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_cupboard, container, false);

        // Inflate the layout for this fragment
        constraintLayout = view.findViewById(R.id.cl_cupboard_detail);

        Bundle bundle = getArguments();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            constraintLayout.setTransitionName(bundle.getString("Shared Element"));
        }

        imageView = view.findViewById(R.id.iv_collapse);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

}
