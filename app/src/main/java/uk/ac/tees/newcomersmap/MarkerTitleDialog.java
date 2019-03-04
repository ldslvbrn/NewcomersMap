package uk.ac.tees.newcomersmap;

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

public class MarkerTitleDialog extends DialogFragment {

    private EditText editTextMarkerTitle;
    private MarkerTitleDialogListener markerTitleDialogListener;
    private String defaultText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_marker_title,null);

        editTextMarkerTitle = view.findViewById(R.id.edit_marker_title);
        if(defaultText != null) {
            editTextMarkerTitle.setText(defaultText);
            editTextMarkerTitle.selectAll();
        }
        builder.setView(view)
                .setTitle("Marker:")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (markerTitleDialogListener != null) {
                            markerTitleDialogListener
                                    .OnDialogReturn(getString(R.string.new_marker));

                        }
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (markerTitleDialogListener != null) {
                            markerTitleDialogListener
                                    .OnDialogReturn(editTextMarkerTitle.getText().toString());
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

    protected void setDefaultText(String text) {
        this.defaultText = text;
    }

    public void setMarkerTitleDialogListener(MarkerTitleDialogListener markerTitleDialogListener) {
        this.markerTitleDialogListener = markerTitleDialogListener;
    }

    protected interface MarkerTitleDialogListener {
        void OnDialogReturn(String title);
    }
}