package com.example.trainerguide;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

public class FragmentProfile extends Fragment {
    private MaterialCardView trainerCardView, traineeCardView;
    private FragmentProfileListener listener;

    public interface FragmentProfileListener{
        void OnCardClicked(boolean IsTrainer);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_select_fragment,container,false);

        trainerCardView = v.findViewById(R.id.trainerCardView);
        traineeCardView = v.findViewById(R.id.traineeCardView);

        trainerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCardClicked(true);
            }
        });

        traineeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCardClicked(true);

            }
        });
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentProfileListener)
        {
            listener = (FragmentProfileListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
            +" must implement Fragment Profile Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
