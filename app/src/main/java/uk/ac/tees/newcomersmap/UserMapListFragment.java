package uk.ac.tees.newcomersmap;


import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
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
public class UserMapListFragment extends Fragment {

    public static final String TAG = "UserMapListFragment";
    public static final String EXTRA_GOOGLE_SIGN_IN_ACCOUNT =
            "uk.ac.tees.newcomersmap.EXTRA_GOOGLE_SIGN_IN_ACCOUNT";

    private GoogleSignInAccount mGoogleSignInAccount;
    private ConstraintLayout loadingSpinnerView;
    private RecyclerView mapListRecyclerView;
    private UserMapRecyclerAdapter adapter;
    private FloatingActionButton addButton;
    private Toolbar mToolbar;
    private NewcomersMapViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        // Inflate the layout for this fragment and instantiate the View
        View view = inflater.inflate(R.layout.fragment_map_list, container, false);

        mToolbar = view.findViewById(R.id.toolbar_map_list);
        mToolbar.inflateMenu(R.menu.menu_context_map_list);
        mToolbar.setOnMenuItemClickListener(onMenuItemClickListener);

        loadingSpinnerView = view.findViewById(R.id.constraintLayout_loading_screen_holder);
        addButton = view.findViewById(R.id.floatingActionButton_add_map);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
                        .navigate(R.id.action_mapListFragment_to_newcomerMapFragment);
            }
        });

        mapListRecyclerView = view.findViewById(R.id.recycleListView_map_list);
        mapListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mapListRecyclerView.setHasFixedSize(true);

        adapter = new UserMapRecyclerAdapter(new Geocoder(getActivity()));
        adapter.setOnItemClickListener(new UserMapRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserMap map) {
                Bundle bundle = new Bundle();
                bundle.putInt(UserMapViewFragment.EXTRA_MAP_LIST_INDEX,
                        viewModel.getAllMaps().getValue().indexOf(map));

                Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
                        .navigate(R.id.action_mapListFragment_to_newcomerMapFragment, bundle);
            }
        });
        mapListRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Acquire ViewModel
        viewModel = ViewModelProviders.of(getActivity())
                .get(NewcomersMapViewModel.class);
        // Observe ViewModel
        viewModel.getAllMaps().observe(getActivity(), new Observer<List<UserMap>>() {
            @Override
            public void onChanged(List<UserMap> maps) {
                adapter.setMaps(maps);
            }
        });
        // If navigating from the authentication fragment
        if (this.getArguments() != null) {
            // Get GoogleSignInAccount from previous fragments
            mGoogleSignInAccount = getArguments()
                    .getParcelable(EXTRA_GOOGLE_SIGN_IN_ACCOUNT);
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

    private void signOut() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // Value default_web_client_id is generated at compile time
                // from google-services.json file
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn
                .getClient(getActivity(), googleSignInOptions);

        googleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
                        .navigate(R.id.action_mapListFragment_to_signInFragment);
            }
        });
    }

    private final Toolbar.OnMenuItemClickListener onMenuItemClickListener
            = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.option_item_public_maps:
                    Toast.makeText(getActivity(), getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.option_item_about:
                    // TODO AboutFragment
                    return true;

                case R.id.option_item_sign_out:
                    signOut();
                    return true;

                default:
                    return false;
            }
        }
    };

    private final NewcomersMapViewModel.OnServiceResultListener onAuthenticateResultListener
            = new NewcomersMapViewModel.OnServiceResultListener() {
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
    private final NewcomersMapViewModel.OnServiceResultListener onRetrieveDataResultListener
            = new NewcomersMapViewModel.OnServiceResultListener() {
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
