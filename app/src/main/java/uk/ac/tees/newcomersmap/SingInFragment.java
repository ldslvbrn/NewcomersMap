package uk.ac.tees.newcomersmap;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingInFragment extends Fragment {

    public static final String TAG = "SingInFragment";
    public static final int GOOGLE_SIGN_IN_REQUEST = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions googleSignInOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        
        // Inflate the layout for this fragment and instantiate the View
        View view = inflater.inflate(R.layout.fragment_sing_in, container, false);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN
        googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // Value default_web_client_id is generated at compile time
                // from google-services.json file
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);

        // Set the dimensions of the sign-in button.
        SignInButton googleSignInButton = view.findViewById(R.id.google_sign_in_button);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        mGoogleSignInClient.getSignInIntent(), GOOGLE_SIGN_IN_REQUEST);
            }
        });

        // Return view
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if(requestCode == GOOGLE_SIGN_IN_REQUEST && resultCode == getActivity().RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
                    .navigate(R.id.errorFragment);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Bundle bundle = new Bundle();
            bundle.putParcelable(MapListFragment.EXTRA_GOOGLE_SIGN_IN_ACCOUNT,account);
            Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
                    .navigate(R.id.action_singInFragment_to_mapListFragment, bundle);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getActivity(), "Failed to authenticate.",Toast.LENGTH_LONG);
        }
    }
}
