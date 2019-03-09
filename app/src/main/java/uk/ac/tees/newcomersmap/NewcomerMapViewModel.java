package uk.ac.tees.newcomersmap;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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

public class NewcomerMapViewModel extends AndroidViewModel {

    public static final String TAG = "NewcomerMapViewModel";

    private static final String USERS_COLLECTION = "users";
    private static final String USER_NCMAPS_COLLECTION = "user_NCMaps";
    private static final String NCMAP_KEY = "NCMap_Key";

    private boolean AUTHENTICATED;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private CollectionReference userDataReference;

    private MutableLiveData<List<NewcomerMap>> allMaps;


    public NewcomerMapViewModel(Application application) {
        super(application);
        // Assign variables
//        FirebaseApp.initializeApp(application.getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();
        this.allMaps = new MutableLiveData<>();
    }

    public void authenticateToFirebase(final GoogleSignInAccount account, final OnServiceResultListener listener) {


        AuthCredential credential = GoogleAuthProvider.getCredential(account
                .getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Authentication SUCCESSFUL");
                            firebaseUser = firebaseAuth.getCurrentUser();
                            AUTHENTICATED = true;
                            listener.OnResultCallback(true);
                        } else {
                            Log.d(TAG, "onComplete: Authentication UNSUCCESSFUL");
                            // Sign in attempt failed
                            AUTHENTICATED = false;
                            listener.OnResultCallback(false);
                        }
                    }
                });
    }

    /*
     * Get all user data from the Firestore service and store them inside the ViewModel
     * - Return null reference if connection attempt was unsuccessful
     * - Return empty list if connection attempt was successful but no data was found
     * - Return populated list if connection attempt was successful
     */
    public void retrieveData(final OnServiceResultListener listener) {
        if (AUTHENTICATED) {
            userDataReference = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                    .document(firebaseUser.getUid()).collection(USER_NCMAPS_COLLECTION);

            userDataReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        }
                        allMaps.setValue(mapList);
                        listener.OnResultCallback(true);
                    } else {
                        Log.d(TAG, "readData: onFailure: Failed to reach online services");
                        listener.OnResultCallback(false);
                    }
                }
            });
        }
    }

    public void addMap(final NewcomerMap map, final OnServiceResultListener listener) {
        userDataReference.document().set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "addMap: onComplete: Service reached successfully");
                            allMaps.getValue().add(map);
                            listener.OnResultCallback(true);
                        } else {
                            Log.d(TAG, "addMap: onFailure: Failed to reach online services");
                            listener.OnResultCallback(false);
                        }
                    }
                });
    }

    public void updateMap(final NewcomerMap map, final OnServiceResultListener listener) {
        userDataReference.document(map.getDocumentId()).set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "updateMap: onComplete: Service reached successfully");
                            int index = allMaps.getValue().indexOf(map);
                            allMaps.getValue().set(index, map);
                            listener.OnResultCallback(true);
                        } else {
                            Log.d(TAG, "updateMap: onFailure: Failed to reach online services");
                            listener.OnResultCallback(false);
                        }
                    }
                });
    }

    public void deleteMap(final NewcomerMap map, final OnServiceResultListener listener) {
        userDataReference.document(map.getDocumentId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "deleteMap: onComplete: Service reached successfully");
                            allMaps.getValue().remove(map);
                            listener.OnResultCallback(true);
                        } else {
                            Log.d(TAG, "deleteMap: onFailure: Failed to reach online services");
                            listener.OnResultCallback(false);
                        }
                    }
                });
    }

    /*
     * Retrieves all available user maps and returns them as LiveData object
     * Return null if unable to reach remote services.
     */
    public LiveData<List<NewcomerMap>> getAllMaps() {
        return allMaps;
    }

    public boolean isAuthenticated() {
        return AUTHENTICATED;
    }

    protected interface OnServiceResultListener {
        void OnResultCallback(Boolean isSuccessful);
    }

}
