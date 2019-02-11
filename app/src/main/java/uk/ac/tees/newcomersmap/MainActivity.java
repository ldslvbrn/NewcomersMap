package uk.ac.tees.newcomersmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) { Log.d(TAG, "onCreate: Woohoo, I'm being called!!!"); }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In mGoogleSingInAccount, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount mGoogleSingInAccount = GoogleSignIn.getLastSignedInAccount(this);

        // Get the Navigation Controller instance
        NavController navController = Navigation
                .findNavController(this, R.id.nav_host_fragment);

        if(mGoogleSingInAccount != null) {
            // Navigate to the user's maps list
            navController.navigate(R.id.mapListFragment);
        }
        else
        {
            // Navigate to the login screen
            navController.navigate(R.id.singInFragment);
        }
    }
}
