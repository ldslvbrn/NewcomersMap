package uk.ac.tees.newcomersmap.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import uk.ac.tees.newcomersmap.NewcomerMap;
import uk.ac.tees.newcomersmap.R;
import uk.ac.tees.newcomersmap.NewcomerMap;
import uk.ac.tees.newcomersmap.NewcomerMapViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapListFragment extends Fragment {

    public static final String TAG = "MapListFragment";
    public static final String EXTRA_GOOGLE_SING_IN_ACCOUNT =
            "uk.ac.tees.newcomersmap.EXTRA_GOOGLE_SING_IN_ACCOUNT";

    private NewcomerMapViewModel newcomerMapViewModel;

    public MapListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        // Inflate the layout for this fragment and instantiate the View
        View view = inflater.inflate(R.layout.fragment_sing_in, container, false);

        // Set-up ViewModel
        newcomerMapViewModel = ViewModelProviders.of(this)
                .get(NewcomerMapViewModel.class);

        // Get GoogleSignInAccount from previous fragments
        GoogleSignInAccount mGoogleSignInAccount = getArguments()
                .getParcelable(EXTRA_GOOGLE_SING_IN_ACCOUNT);
        // Authenticate to remote services
        newcomerMapViewModel.authenticateToFirebase(mGoogleSignInAccount);
        // newcomerMapViewModel.loadData();
        // Observe ViewModel
        newcomerMapViewModel.getAllMaps().observe(this, new Observer<List<NewcomerMap>>() {
            @Override
            public void onChanged(List<NewcomerMap> maps) {
                // TODO: Shove something in here
                // adapter.submitList(maps);
            }
        });

        return view;
    }



}
