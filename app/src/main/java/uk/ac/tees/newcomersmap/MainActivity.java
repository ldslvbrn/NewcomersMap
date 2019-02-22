package uk.ac.tees.newcomersmap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;


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
