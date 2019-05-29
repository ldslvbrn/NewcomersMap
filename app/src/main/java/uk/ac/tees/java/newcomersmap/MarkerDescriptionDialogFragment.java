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

public class MarkerDescriptionDialogFragment extends DialogFragment {

    private EditText editTextMarkerDesc;
    private DialogListener dialogListener;
    private UserPoint mUserPoint;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_marker_description, null);

        editTextMarkerDesc = view.findViewById(R.id.edit_marker_description);
        if (mUserPoint.getDescription() != null) {
            editTextMarkerDesc.setText(mUserPoint.getDescription());
        }
        editTextMarkerDesc.selectAll();
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
                            if (editTextMarkerDesc.getText() != null
                                    || !editTextMarkerDesc.getText().toString().isEmpty()) {
                                String desc = editTextMarkerDesc.getText().toString().trim();
                                if (desc == null) {
                                    mUserPoint.setDescription(null);
                                }
                                if (desc.length() > 40) {
                                    dialogListener.onDialogResult(DialogResult.INPUT_INVALID);
                                } else {
                                    mUserPoint.setDescription(desc);
                                    dialogListener.onDialogResult(DialogResult.INPUT_OK);
                                }
                            } else  dialogListener.onDialogResult(DialogResult.INPUT_OK);
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

    public void setUserMarker(UserPoint mUserPoint) {
        this.mUserPoint = mUserPoint;
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}
