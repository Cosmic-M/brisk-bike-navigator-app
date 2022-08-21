package brisk.bike.navigator.modul;

import java.util.List;

/**
 * Created by Cosmic_M on 11.09.2017.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(List<Route> list);
}
