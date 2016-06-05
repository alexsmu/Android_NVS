package byuie499.auto_nvs;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by marlonvilorio on 5/5/16.
 */
public class BluetoothActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);

        getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#FF0000' >Bluetooth Disconnected</font><small>"));
        getSupportActionBar().setTitle("Bluetooth Settings");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.bluetoothtab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.bluetooth_tab_paired));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.bluetooth_tab_scan));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerb);
        final BluetoothPagerAdapter adapter = new BluetoothPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}