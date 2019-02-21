package uk.ac.tees.newcomersmap;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import uk.ac.tees.newcomersmap.NewcomerMap;

public class NewcomerMapViewModel extends AndroidViewModel {

    public static final String TAG = "NewcomerMapViewModel";

    private static final String USERS_COLLECTION = "users";
    private static final String USER_NCMAPS_COLLECTION = "user_NCMaps";
    private static final String NCMAP_KEY = "NCMap_Key";
    private static boolean AUTHENTICATED;
    private Application application;

    private GoogleSignInAccount mGoogleSignInAccount;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private CollectionReference mUserDataReference;

    private MutableLiveData<List<NewcomerMap>> allMaps;

    // TODO: REWORK THE WHOLE CLASS TO A ViewModel
    public NewcomerMapViewModel(Application application) {
        super(application);
        // Assign variables
        this.application = application;
        mAuth = FirebaseAuth.getInstance();
        mUserDataReference = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(mFirebaseUser.getUid()).collection(USER_NCMAPS_COLLECTION);
    }

    public void authenticateToFirebase(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account
                .getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mGoogleSignInAccount = account;
                            // Sign in success, update the IS_AUTHENTICATED status variable
                            mFirebaseUser = mAuth.getCurrentUser();
                            AUTHENTICATED = true;
                        } else {
                            // Sign in attempt failed, update the IS_AUTHENTICATED status variable
                            AUTHENTICATED = false;
                        }
                    }
                });
    }

    /*
     * Get all user data from the Firestore service and store them inside the ViewModel
     */
    private void readData() {
        if (AUTHENTICATED) {


        }



        if (AUTHENTICATED) {
            mUserDataReference = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                    .document(mFirebaseUser.getUid()).collection(USER_NCMAPS_COLLECTION);

            mUserDataReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "readData: onComplete: Services reached successfully");
                        List<NewcomerMap> mapList = new ArrayList<>();
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NewcomerMap map = document.toObject(NewcomerMap.class);
                                map.setDocumentId(document.getId());
                                mapList.add(map);
                            }
                            allMaps = new MutableLiveData<>();
                            allMaps.setValue(mapList);
                        }
                        else {
                            Log.d(TAG, "readData: onFailure: Failed to reach online services");
                            Toast.makeText(application.getApplicationContext(),
                                    "Failed to reach online services", Toast.LENGTH_SHORT);
                        }
                    }
                }
            });
        }
    }

    public void addMap(final NewcomerMap map) {
        mUserDataReference.document().set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "addMap: onComplete: Service reached successfully");
                            allMaps.getValue().add(map);
                        }
                        else {
                            Log.d(TAG, "addMap: onFailure: Failed to reach online services");
                            Toast.makeText(application.getApplicationContext(),
                                    "Failed to reach online services", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    public void updateMap(final NewcomerMap map) {
        mUserDataReference.document().set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "updateMap: onComplete: Service reached successfully");
                            int index = allMaps.getValue().indexOf(map);
                            allMaps.getValue().set(index, map);
                        }
                        else {
                            Log.d(TAG, "updateMap: onFailure: Failed to reach online services");
                            Toast.makeText(application.getApplicationContext(),
                                    "Failed to reach online services", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    public void deleteMap(final NewcomerMap map) {
        mUserDataReference.document(map.getDocumentId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "deleteMap: onComplete: Service reached successfully");
                            int index = allMaps.getValue().indexOf(map);
                            allMaps.getValue().remove(map);
                        }
                        else {
                            Log.d(TAG, "deleteMap: onFailure: Failed to reach online services");
                            Toast.makeText(application.getApplicationContext(),
                                    "Failed to reach online services", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    /*
     * Retrieves all available user maps and returns them as LiveData object
     * Return null if unable to reach remote services.
     */
    public LiveData<List<NewcomerMap>> getAllMaps() {
        if (allMaps == null) {
            readData();
        }
        return allMaps;
    }

    public static boolean isAuthenticated() {
        return AUTHENTICATED;
    }

}
