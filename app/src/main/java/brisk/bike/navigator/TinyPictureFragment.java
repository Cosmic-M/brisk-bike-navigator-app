package brisk.bike.navigator;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import java.io.File;
import brisk.bike.navigator.modul.MemoryPlace;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public class TinyPictureFragment extends Fragment implements View.OnClickListener {
    private RouteComposeListener listener;
    private File file;
    private int tag;
    private static final String EXTRA_TAG_ARG = "tag";
    private ImageView mAssignTransitionPoint;

    public static TinyPictureFragment newInstance(int tag) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TAG_ARG, tag);
        TinyPictureFragment fragment = new TinyPictureFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        tag = getArguments().getInt(EXTRA_TAG_ARG);
        MemoryPlace mp = PlaceLab.get(getActivity()).getMemoryPlace().get(tag);
        file = PlaceLab.get(getActivity()).getPhotoFile(mp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mini_image, container, false);
        listener = (RouteComposeListener) getActivity();
        ImageView mDetailedPointShow = view.findViewById(R.id.mini_image_id);
        ImageView mAssignDestinationPoint = view.findViewById(R.id.btn_target_point_id);
        mAssignTransitionPoint = view.findViewById(R.id.btn_transit_point_id);
        ImageView mExcludePoint = view.findViewById(R.id.btn_remove_point_id);
        Bitmap bitmap = PictureUtils.getScaledBitmap(file.getPath(), getActivity());
        mDetailedPointShow.setImageBitmap(bitmap);
        int resource = listener.getResourceForTransitionImage(tag);
        mAssignTransitionPoint.setImageResource(resource);
        mDetailedPointShow.setOnClickListener(this);
        mExcludePoint.setOnClickListener(this);
        mAssignDestinationPoint.setOnClickListener(this);
        mAssignTransitionPoint.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mini_image_id:
                listener.onDetailedPointShow(tag);
                break;
            case R.id.btn_remove_point_id:
                listener.onRemoveFragment(tag);
                break;
            case R.id.btn_target_point_id:
                int res = listener.onAssignDestinationPoint(tag);
                if (res != 0) {
                    mAssignTransitionPoint.setImageResource(res);
                } else {
                    mAssignTransitionPoint.setImageResource(R.mipmap.transition_flag);
                }
                break;
            case R.id.btn_transit_point_id:
                int resource = listener.onAssignTransitionPoint(tag);
                mAssignTransitionPoint.setImageResource(resource);
                break;
            default:
                break;
        }
    }
}
