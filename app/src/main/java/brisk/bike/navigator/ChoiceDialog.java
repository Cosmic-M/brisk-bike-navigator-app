package brisk.bike.navigator;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public class ChoiceDialog extends DialogFragment implements View.OnClickListener{
    private static final String REQUEST_TEXT_DIALOG = "package com.development.cosmic_m.navigator.request_text_dialog";
    private static final String EXTRA_LATLNG_ARG = "package com.development.cosmic_m.navigator.extra_latlng_arg";
    private static final int REQUEST_NEW_POINT_REMOVE_OLD_DESTINATION = 350;
    private View view = null;
    private String text;
    private LatLng mLatLng;

    public static ChoiceDialog newInstance(String textRequest, LatLng location){
        Bundle bundle = new Bundle();
        bundle.putString(REQUEST_TEXT_DIALOG, textRequest);
        bundle.putParcelable(EXTRA_LATLNG_ARG, location);
        ChoiceDialog dialog = new ChoiceDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        this.setRetainInstance(true);
        text = getArguments().getString(REQUEST_TEXT_DIALOG);
        mLatLng = getArguments().getParcelable(EXTRA_LATLNG_ARG);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        if (view == null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            view = inflater.inflate(R.layout.dialog_container, container, false);
            TextView request = view.findViewById(R.id.text_dialog_id);
            Button positiveBtn = view.findViewById(R.id.positive_btn_id);
            Button negativeBtn = view.findViewById(R.id.negative_btn_id);
            positiveBtn.setOnClickListener(this);
            negativeBtn.setOnClickListener(this);
            request.setText(text);
        }
        else{
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    @Override
    public void onDestroyView(){
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.positive_btn_id:
                Intent intent = AddPointActivity.newIntent(getActivity());
                intent.putExtra("latlng", mLatLng);
                getActivity().startActivityForResult(intent, REQUEST_NEW_POINT_REMOVE_OLD_DESTINATION);
                dismiss();
                break;
            case R.id.negative_btn_id:
                dismiss();
                break;
            default:
                break;
        }
    }
}
