package brisk.bike.navigator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.List;
import brisk.bike.navigator.modul.MemoryPlace;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public class
PlacePagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private List<MemoryPlace> mPlaceList = null;

    public static Intent newIntent(Context context){
        return new Intent(context, PlacePagerActivity.class);
    }

    public void notifyPagerAdapter(){
        mPlaceList = PlaceLab.get(getApplicationContext()).getMemoryPlace();
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setResult(RESULT_OK);
        setContentView(R.layout.activity_place_pager);
        mViewPager = findViewById(R.id.activity_place_pager_view_pager);
        mPlaceList = PlaceLab.get(this).getMemoryPlace();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return PlaceFragment.newInstance(mPlaceList.get(position));
            }

            @Override
            public int getItemPosition(Object object){
                return POSITION_NONE;
            }

            @Override
            public int getCount() {
                return mPlaceList.size();
            }
        });
    }
}
