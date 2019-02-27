package uk.ac.tees.newcomersmap;


import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/**
 * A simple {@link Fragment} subclass.
 */
public class MapListFragment extends Fragment {

    public static final String TAG = "MapListFragment";
    public static final String EXTRA_GOOGLE_SING_IN_ACCOUNT =
            "uk.ac.tees.newcomersmap.EXTRA_GOOGLE_SING_IN_ACCOUNT";

    private ConstraintLayout loadingSpinnerView;
    private RecyclerView mapListRecyclerView;
    private MapRecyclerAdapter adapter;
    private FloatingActionButton addButton;
    private NewcomerMapViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        // Inflate the layout for this fragment and instantiate the View
        View view = inflater.inflate(R.layout.fragment_map_list, container, false);

        loadingSpinnerView = view.findViewById(R.id.constraintLayout_loading_screen_holder);
        addButton = view.findViewById(R.id.floatingActionButton_add_map);

        FloatingActionButton buttonAddMap = view.findViewById(R.id.floatingActionButton_add_map);
        buttonAddMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
                        .navigate(R.id.action_mapListFragment_to_newcomerMapFragment);
            }
        });

        mapListRecyclerView = view.findViewById(R.id.recycleListView_map_list);
        mapListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mapListRecyclerView.setHasFixedSize(true);

        adapter = new MapRecyclerAdapter(new Geocoder(getActivity()));
        adapter.setOnItemClickListener(new MapRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NewcomerMap map) {
                Bundle bundle = new Bundle();
                bundle.putInt(NewcomerMapFragment.EXTRA_MAP_LIST_INDEX,
                        viewModel.getAllMaps().getValue().indexOf(map));

                Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
                        .navigate(R.id.action_mapListFragment_to_newcomerMapFragment);
            }
        });
        mapListRecyclerView.setAdapter(adapter);

        // TODO: Menu options

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set-up ViewModel
        viewModel = ViewModelProviders.of(this)
                .get(NewcomerMapViewModel.class);
        // Observe ViewModel
        viewModel.getAllMaps().observe(getActivity(), new Observer<List<NewcomerMap>>() {
            @Override
            public void onChanged(List<NewcomerMap> maps) {
                adapter.setMaps(maps);
            }
        });

        // If navigating from the authentication fragment
        if (this.getArguments() != null) {
            // Get GoogleSignInAccount from previous fragments
            GoogleSignInAccount mGoogleSignInAccount = getArguments()
                    .getParcelable(EXTRA_GOOGLE_SING_IN_ACCOUNT);
            // Authenticate to remote services
            viewModel.authenticateToFirebase(
                    mGoogleSignInAccount,
                    onAuthenticateResultListener );
        // If navigating from a map fragment
        } else {
            loadingSpinnerView.setVisibility(View.GONE);
            mapListRecyclerView.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
        }
    }

    private final NewcomerMapViewModel.OnServiceResultListener onAuthenticateResultListener
            = new NewcomerMapViewModel.OnServiceResultListener() {
        @Override
        public void OnResultCallback(Boolean isSuccessful) {
            if (isSuccessful) {
                viewModel.retrieveData(onRetrieveDataResultListener);
            } else {
                Log.d(TAG, "OnResultCallback: Unable to receive user data");
                Toast.makeText(getActivity(),"Oops, something went wrong!",Toast.LENGTH_SHORT)
                        .show();
                Bundle bundle = new Bundle();
                bundle.putInt(ErrorFragment.EXTRA_ERROR_CODE,ErrorFragment.FIRESTORE_ERROR_CODE);
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
                        .navigate(R.id.action_mapListFragment_to_errorFragment, bundle);
            }
        }
    };
    private final NewcomerMapViewModel.OnServiceResultListener onRetrieveDataResultListener
            = new NewcomerMapViewModel.OnServiceResultListener() {
        @Override
        public void OnResultCallback(Boolean isSuccessful) {
            if (isSuccessful) {
                loadingSpinnerView.setVisibility(View.GONE);
                mapListRecyclerView.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "OnResultCallback: Unable to receive user data");
                Toast.makeText(getActivity(),"Oops, something went wrong!",Toast.LENGTH_SHORT)
                        .show();
                Bundle bundle = new Bundle();
                bundle.putInt(ErrorFragment.EXTRA_ERROR_CODE,ErrorFragment.FIRESTORE_ERROR_CODE);
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
                        .navigate(R.id.action_mapListFragment_to_errorFragment, bundle);
            }
        }
    };

}
