package byuie499.auto_nvs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabFragment1 extends Fragment {
    View mMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.tab_fragment_1, container, false);

        return mMain;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Is fragment currently visible ?
        if (this.isVisible()) {
            // Is it becoming invisible?
            if (!isVisibleToUser) {

            }
        }
        else { // Is fragment currently invisible?
            // Is it becoming visible?
            if (isVisibleToUser) {

            }
        }
    }

}