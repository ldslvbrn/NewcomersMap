package uk.ac.tees.newcomersmap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MapTitleDialog extends DialogFragment {

    private EditText editTextMapTitle;
    private MapTitleDialogListener mapTitleDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_map_title,null);

        editTextMapTitle = view.findViewById(R.id.edit_map_title);


        builder.setView(view)
                .setTitle("Map:")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mapTitleDialogListener != null) {

                            mapTitleDialogListener.OnDialogReturn(editTextMapTitle.getText().toString());
                        }
                    }
                });
        return builder.create();
    }

    public void setMapTitleDialogListener(MapTitleDialogListener mapTitleDialogListener) {
        this.mapTitleDialogListener = mapTitleDialogListener;
    }

    protected interface MapTitleDialogListener {
        void OnDialogReturn(String title);
    }
}
