package com.example.trainerguide;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.card.MaterialCardView;

public class ProfileSelectDialog extends AppCompatDialogFragment {
private ProfileCardSelectListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.profile_select_dialog,null);
        builder.setView(view)
                .setTitle("")
                .setNegativeButton("@+id/trainerCardView", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    boolean IsTrainer = true;
                    listener.profile(IsTrainer);
                    }
                })
                .setPositiveButton("@+id/traineeCardView", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean IsTrainer = false;
                        listener.profile(IsTrainer);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ProfileCardSelectListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+
                    "must implement Listener for Dialog");
        }
    }

    public  interface ProfileCardSelectListener
    {
        void profile(boolean IsTrainer);
    }
}
