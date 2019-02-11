package uk.ac.tees.newcomersmap.ui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.ac.tees.newcomersmap.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TitleScreenFragment extends Fragment {


    public static final String TAG = "TitleScreenFragment";

    public TitleScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Woohoo, I'b being called!!!");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_title_screen, container, false);
    }

}
