package com.creativesourceapps.android.cupboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class DetailCupboardFragment extends Fragment {

    public DetailCupboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Transition transition = TransitionInflater.from(getContext())
                    .inflateTransition(R.transition.cupboard_shared_element_transition);

            setSharedElementEnterTransition(transition);
        }

        return inflater.inflate(R.layout.fragment_detail_cupboard, container, false);
    }

}
