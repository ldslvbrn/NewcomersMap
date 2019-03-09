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

public class MapTitleDialogFragment extends DialogFragment {

    private EditText editTextMapTitle;
    private TitleDialogListener titleDialogListener;
    private NewcomerMap mNewcomerMap;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_map_title, null);

        editTextMapTitle = view.findViewById(R.id.edit_map_title);
        editTextMapTitle.setText(mNewcomerMap.getTitle());
        editTextMapTitle.selectAll();
        builder.setView(view)
                .setTitle("Map:")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (titleDialogListener != null) {
                            titleDialogListener
                                    .onDialogResult(DialogResult.CANCEL_PRESSED);
                        }
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (titleDialogListener != null) {
                            String title = editTextMapTitle.getText().toString().trim();
                            if (title.isEmpty() || title.length() < 3 || title.length() > 16) {
                                titleDialogListener.onDialogResult(DialogResult.INPUT_INVALID);
                            } else {
                                mNewcomerMap.setTitle(title);
                                titleDialogListener.onDialogResult(DialogResult.INPUT_OK);
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

    public void setNewcomerMap(NewcomerMap mNewcomerMap) {
        this.mNewcomerMap = mNewcomerMap;
    }

    public void setTitleDialogListener(TitleDialogListener titleDialogListener) {
        this.titleDialogListener = titleDialogListener;
    }

}
