package uk.ac.tees.java.newcomersmap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MarkerTitleDialogFragment extends DialogFragment {

    private EditText editTextMarkerTitle;
    private DialogListener dialogListener;
    private UserMarker mUserMarker;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_marker_title, null);

        editTextMarkerTitle = view.findViewById(R.id.edit_marker_description);
        editTextMarkerTitle.setText(mUserMarker.getTitle());
        editTextMarkerTitle.selectAll();
        builder.setView(view)
                .setTitle("Marker:")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogListener != null) {
                            dialogListener
                                    .onDialogResult(DialogResult.CANCEL_PRESSED);
                        }
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogListener != null) {
                            String title = editTextMarkerTitle.getText().toString().trim();
                            if (title.isEmpty() || title.length() < 3 || title.length() > 16) {
                                dialogListener.onDialogResult(DialogResult.INPUT_INVALID);
                            } else {
                                mUserMarker.setTitle(title);
                                dialogListener.onDialogResult(DialogResult.INPUT_OK);
                            }
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Show keyboard when fragments get created
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void setUserMarker(UserMarker mUserMarker) {
        this.mUserMarker = mUserMarker;
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}
