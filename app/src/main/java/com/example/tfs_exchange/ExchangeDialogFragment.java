package com.example.tfs_exchange;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

/**
 * Created by pusya on 04.12.17.
 */

public class ExchangeDialogFragment extends DialogFragment {

    private static final String TAG = "ExchangeDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)  {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Актуальный курс");
        builder.setMessage("хай, герлз");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "OK");
            }
        });

        builder.setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "not Ok");
            }
        });

        builder.setCancelable(true);

        return builder.create();
    }
}
