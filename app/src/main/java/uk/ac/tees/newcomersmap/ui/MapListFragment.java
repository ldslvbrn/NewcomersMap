package uk.ac.tees.newcomersmap.ui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.ac.tees.newcomersmap.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapListFragment extends Fragment {
    public static final String EXTRA_GOOGLE_SING_IN_ACCOUNT =
            "uk.ac.tees.newcomersmap.EXTRA_GOOGLE_SING_IN_ACCOUNT";

    public MapListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_list, container, false);
    }

}
