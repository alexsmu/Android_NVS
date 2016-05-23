package byuie499.auto_nvs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class TabFragment1 extends Fragment {
    private View mMain;
    private Tab1Data staticData = null;
    private ToggleButton noise, vibration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.tab_fragment_1, container, false);
        staticData = new Tab1Data(mMain.getContext());
        noise = (ToggleButton) mMain.findViewById(R.id.toggleNoise);
        vibration = (ToggleButton) mMain.findViewById(R.id.toggleVibration);
        noise.setChecked(Tab1Data.getBool("Noise", false));
        vibration.setChecked(Tab1Data.getBool("Vibration", true));
        noise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tab1Data.getBool("Noise", false))
                {
                    noise.setChecked(false);
                    Tab1Data.setCheckBox("Noise", false);
                } else {
                    noise.setChecked(true);
                    Tab1Data.setCheckBox("Noise", true);
                }

            }
        });
        vibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tab1Data.getBool("Vibration", true))
                {
                    vibration.setChecked(false);
                    Tab1Data.setCheckBox("Vibration", false);
                } else {
                    vibration.setChecked(true);
                    Tab1Data.setCheckBox("Vibration", true);
                }
            }
        });

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

}