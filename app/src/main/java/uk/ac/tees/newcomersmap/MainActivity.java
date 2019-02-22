package uk.ac.tees.newcomersmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import uk.ac.tees.newcomersmap.R;
import uk.ac.tees.newcomersmap.ui.MapListFragment;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import static uk.ac.tees.newcomersmap.ui.MapListFragment.*;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Navigate to the welcome screen if not no previous session is found
        if (savedInstanceState != null) {
            Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.titleScreenFragment);
        }
    }

}
