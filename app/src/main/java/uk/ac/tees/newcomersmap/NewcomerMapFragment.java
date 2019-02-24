package uk.ac.tees.newcomersmap;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewcomerMapFragment extends Fragment {

    public static final String EXTRA_MAP_LIST_INDEX =
            "uk.ac.tees.newcomersmap.EXTRA_MAP_LIST_INDEX";

    private NewcomerMapViewModel newcomerMapViewModel;
    private NewcomerMap newcomerMap;

    public NewcomerMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_newcomer_map, container, false);

        newcomerMapViewModel = ViewModelProviders.of(this)
                .get(NewcomerMapViewModel.class);

        if (this.getArguments().containsKey(EXTRA_MAP_LIST_INDEX)) {
            newcomerMap = newcomerMapViewModel.getAllMaps().getValue()
                    .get(this.getArguments().getInt(EXTRA_MAP_LIST_INDEX));
            this.getActivity().setTitle(newcomerMap.getTitle());
            // TODO fillout out marker RecyclerView
        } else {
            this.getActivity().setTitle("New Custom Map");
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
