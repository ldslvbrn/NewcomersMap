package uk.ac.tees.newcomersmap;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorFragment extends Fragment {

    public static final String EXTRA_ERROR_CODE =
            "uk.ac.tees.newcomersmap.EXTRA_ERROR";
    public static final int PERMISSION_ERROR_CODE = 9001;
    public static final int FIRESTORE_ERROR_CODE = 9002;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_error, container, false);

        TextView errorMessage = view.findViewById(R.id.textView_error_message_bottom);

        switch (this.getArguments().getInt(EXTRA_ERROR_CODE)) {
            case PERMISSION_ERROR_CODE:
                errorMessage.setText(R.string.permission_error_message);
                break;

            case FIRESTORE_ERROR_CODE:
                errorMessage.setText(R.string.firestore_error_message);
                break;

            default:
                errorMessage.setText(R.string.default_error_message);
                break;
        }

        return view;
    }

}
