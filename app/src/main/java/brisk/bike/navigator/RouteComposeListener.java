package brisk.bike.navigator;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public interface RouteComposeListener {
    int onAssignDestinationPoint(int tag);

    int onAssignTransitionPoint(int tag);

    void onRemoveFragment(int tag);

    void onDetailedPointShow(int tag);

    int getResourceForTransitionImage(int tag);
}
