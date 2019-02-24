package uk.ac.tees.newcomersmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.ThreadPoolExecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Navigate to the welcome screen if not no previous session is found
        if (savedInstanceState != null) {

            Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.titleScreenFragment);
        } else {
            // Location and Internet access permission check
            checkPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (ASK_MULTIPLE_PERMISSION_REQUEST_CODE) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    // If request is cancelled, the result arrays are empty.
                    for (int result: grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Bundle bundle = new Bundle();
                            bundle.putInt(ErrorFragment.EXTRA_ERROR_CODE,
                                    ErrorFragment.PERMISSION_ERROR_CODE);
                            Navigation.findNavController(this, R.id.nav_host_fragment)
                                    .navigate(R.id.errorFragment, bundle);
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted; Request permissions
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }
}
