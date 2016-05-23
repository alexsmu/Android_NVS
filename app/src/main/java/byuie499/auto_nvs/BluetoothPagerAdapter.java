package byuie499.auto_nvs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by marlonvilorio on 5/5/16.
 */
public class BluetoothPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public BluetoothPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                BluetoothTabPaired tab1 = new BluetoothTabPaired();
                return tab1;
            case 1:
                BluetoothTabScan tab2 = new BluetoothTabScan();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}