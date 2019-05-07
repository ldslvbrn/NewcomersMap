package uk.ac.tees.java.newcomersmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Location and Internet access permission check
        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (ASK_MULTIPLE_PERMISSION_REQUEST_CODE) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    // If request is cancelled, the result arrays are empty.
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Bundle bundle = new Bundle();
                            bundle.putInt(ErrorFragment.EXTRA_ERROR_CODE,
                                    ErrorFragment.PERMISSION_ERROR_CODE);
                            Navigation.findNavController(this, R.id.nav_host_fragment)
                                    .navigate(R.id.errorFragment, bundle);
                        }
                    }
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.titleScreenFragment);
                }
                break;

            default:
                break;
        }
    }

    protected void checkPermissions() {
        ArrayList<String> requestPermissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions.add(Manifest.permission.INTERNET);
        }

        if (!requestPermissions.isEmpty()) {
            requestPermissions(requestPermissions.toArray(
                    new String[requestPermissions.size()]), ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        } else {
            Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.titleScreenFragment);
        }
    }

}
