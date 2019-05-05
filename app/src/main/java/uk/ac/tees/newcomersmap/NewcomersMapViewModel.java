package uk.ac.tees.newcomersmap;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

public class NewcomersMapViewModel extends AndroidViewModel {

    public static final String TAG = "NewcomersMapViewModel";

    private static final String USERS_COLLECTION = "users";
    private static final String USER_NCMAPS_COLLECTION = "userMaps";
    private static final String NCMAP_KEY = "NCMap_Key";

    private boolean AUTHENTICATED;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private CollectionReference userDataReference;

    private MutableLiveData<List<UserMap>> allMaps;


    public NewcomersMapViewModel(Application application) {
        super(application);
        // Assign variables
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
                        List<UserMap> mapList = new ArrayList<>();
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserMap map = document.toObject(UserMap.class);
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

//    public void addUserMap(final UserMap map, final OnServiceResultListener listener) {
//        userDataReference.document().set(map)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "addUserMap: onComplete: Service reached successfully");
//                            allMaps.getValue().add(map);
//                            listener.OnResultCallback(true);
//                        } else {
//                            Log.d(TAG, "addUserMap: onFailure: Failed to reach online services");
//                            listener.OnResultCallback(false);
//                        }
//                    }
//                });
//    }

    public void addUserMap(final UserMap map, final OnServiceResultListener listener) {
        userDataReference.add(map)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "addUserMap: onComplete: Service reached successfully");
                            map.setDocumentId(task.getResult().getId());
                            allMaps.getValue().add(map);
                            listener.OnResultCallback(true);
                        } else {
                            Log.d(TAG, "addUserMap: onFailure: Failed to reach online services");
                            listener.OnResultCallback(false);
                        }
                    }
                });
    }

    public void updateUserMap(final UserMap map, final OnServiceResultListener listener) {
        userDataReference.document(map.getDocumentId()).set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "updateUserMap: onComplete: Service reached successfully");
                            int index = allMaps.getValue().indexOf(map);
                            allMaps.getValue().set(index, map);
                            listener.OnResultCallback(true);
                        } else {
                            Log.d(TAG, "updateUserMap: onFailure: Failed to reach online services");
                            listener.OnResultCallback(false);
                        }
                    }
                });
    }

    public void deleteUserMap(final UserMap map, final OnServiceResultListener listener) {
        userDataReference.document(map.getDocumentId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "deleteUserMap: onComplete: Service reached successfully");
                            allMaps.getValue().remove(map);
                            listener.OnResultCallback(true);
                        } else {
                            Log.d(TAG, "deleteUserMap: onFailure: Failed to reach online services");
                            listener.OnResultCallback(false);
                        }
                    }
                });
    }

    /*
     * Retrieves all available user maps and returns them as LiveData object
     * Return null if unable to reach remote services.
     */
    public LiveData<List<UserMap>> getAllMaps() {
        return allMaps;
    }

    public boolean isAuthenticated() {
        return AUTHENTICATED;
    }

    protected interface OnServiceResultListener {
        void OnResultCallback(Boolean isSuccessful);
    }

}
