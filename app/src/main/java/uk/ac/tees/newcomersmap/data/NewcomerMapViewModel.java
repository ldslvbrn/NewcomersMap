package uk.ac.tees.newcomersmap.data;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class NewcomersMapViewModel extends AndroidViewModel {

    private static final String USER_DATA_COLLECTION = "users_data";
    private static final String USER_MAPS_COLLECTION = "user_maps";
    private static boolean AUTHENTICATED;

    private GoogleSignInAccount mGoogleSignInAccount;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private CollectionReference mUserDataReference;

    private LiveData<List<NewcomerMap>> allMaps;

    /* TODO: REWORK THE WHOLE CLASS TO A ViewModel
     * Suitable use of this repository is to instantiate repository variable,
     * call authenticateToFirebase() and after check if the user has been authenticated
     * using isAuthenticated() boolean value.
     */
    public NewcomersMapViewModel(Application application,GoogleSignInAccount account) {
        super(application);

        // Assign variables
        AUTHENTICATED = false;
        mGoogleSignInAccount = account;
        mAuth = FirebaseAuth.getInstance();
        mUserDataReference = FirebaseFirestore.getInstance().collection(USER_DATA_COLLECTION)
                .document(mFirebaseUser.getUid()).collection(USER_MAPS_COLLECTION);
    }

    private void fetchData() {
        mUserDataReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        allMaps.add(document.toObject(NewcomerMap.class));
                    }
                }
            }
        });
    }

    /*
     *
     */
    public void add(NewcomerMap map) {
        mUserDataReference.document().set(map);
    }

    public void update(NewcomerMap oldM, final NewcomerMap newM) {
        final NewcomerMap finalNewM = newM;
        Query subjectQuery = mUserDataReference
                .whereEqualTo("name",oldM.getName());

        subjectQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                mUserDataReference.document("/" + list.get(0).getId())
                        .set(finalNewM);
            }
        });
    }

    public void delete(NewcomerMap map) {
        Query subjectQuery = mUserDataReference
                .whereEqualTo("name",map.getName());

        subjectQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                mUserDataReference
                        .document("/" + list.get(0).getId())
                        .delete();
            }
        });
    }


    public LiveData<List<NewcomerMap>> getAllMaps() {
        if (allMaps == null || allMaps.size() == 0) {
            fetchData();
        }
        return allMaps;
    }

    public static boolean isAuthenticated() {
        return AUTHENTICATED;
    }

    public void authenticateToFirebase() {
        AuthCredential credential = GoogleAuthProvider.getCredential(mGoogleSignInAccount
                .getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update the IS_AUTHENTICATED status variable
                            mFirebaseUser = mAuth.getCurrentUser();
                            AUTHENTICATED = true;
                        } else {
                            // Sign fail, update the IS_AUTHENTICATED status variable
                            AUTHENTICATED = false;
                        }
                    }
                });
    }

}
