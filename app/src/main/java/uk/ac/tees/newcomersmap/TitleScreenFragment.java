package uk.ac.tees.newcomersmap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


public class TitleScreenFragment extends Fragment {


    public static final String TAG = "TitleScreenFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_title_screen, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        GoogleSignInAccount currentUser = GoogleSignIn.getLastSignedInAccount(getActivity());
        // Check if the user was signed in using Google Account ( !=null)
        if(currentUser != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(UserMapListFragment.EXTRA_GOOGLE_SIGN_IN_ACCOUNT, currentUser);
            // Navigate to the user's maps list
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_titleScreenFragment_to_mapListFragment, bundle);
        } else {
            // Navigate to the login screen
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_titleScreenFragment_to_singInFragment);
        }
    }
}
