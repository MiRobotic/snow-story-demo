package com.mirobotic.story.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirobotic.story.R;
import com.mirobotic.story.ui.activities.OnActivityInteraction;
import com.mirobotic.story.utils.Request;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import static androidx.navigation.ViewKt.findNavController;
import static com.mirobotic.story.app.Config.DIR_SONG;
import static com.mirobotic.story.app.Config.DIR_STORY;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements View.OnClickListener {

    private OnActivityInteraction activityInteraction;
    private boolean isDancing = false;
    private TextView tvDance;
    private boolean isVoiceRecognizing = false;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityInteraction = (OnActivityInteraction) context;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView cvSing = view.findViewById(R.id.cvSing);
        CardView cvDance = view.findViewById(R.id.cvDance);
        CardView cvStory = view.findViewById(R.id.cvStory);

        tvDance = view.findViewById(R.id.tvDance);

        cvSing.setOnClickListener(this);
        cvDance.setOnClickListener(this);
        cvStory.setOnClickListener(this);


        final ImageView imgSpeech = view.findViewById(R.id.imgSpeech);
        imgSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isVoiceRecognizing) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("msg_id", Request.SPEECH_STOP_MULTI_RECOG_REQ);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    activityInteraction.sendData(object.toString());

                    imgSpeech.setImageResource(R.drawable.ic_mic_black);
                    isVoiceRecognizing = false;

                    return;
                }

                JSONObject object = new JSONObject();
                try {
                    object.put("msg_id", Request.SPEECH_START_MULTI_RECOG_REQ);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                activityInteraction.sendData(object.toString());
                imgSpeech.setImageResource(R.drawable.ic_mic_off);
                isVoiceRecognizing = true;

            }
        });


    }


    @Override
    public void onClick(View view) {

        Log.e("Menu", "click " + view.getId());

        Bundle bundle = new Bundle();
        if (view.getId() == R.id.cvSing) {
            bundle.putString("type", DIR_SONG);
           showFragment(bundle);
        } else if (view.getId() == R.id.cvStory) {
            bundle.putString("type", DIR_STORY);
            showFragment(bundle);
        } else if (view.getId() == R.id.cvDance) {
            JSONObject object = new JSONObject();

            if (isDancing) {
                try {
                    object.put("msg_id", Request.DANCE_STOP_REQ);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tvDance.setText(getResources().getString(R.string.dance_start));
                isDancing = false;
            } else {
                isDancing = true;
                tvDance.setText(getResources().getString(R.string.dance_stop));
                try {
                    object.put("msg_id", Request.DANCE_START_REQ);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            activityInteraction.sendData(object.toString());
        }

    }


    private void showFragment(Bundle bundle){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.nav_host_fragment,new MenuFragment(),"");
        ft.addToBackStack("MenuFragment");
        ft.commit();
    }

}
