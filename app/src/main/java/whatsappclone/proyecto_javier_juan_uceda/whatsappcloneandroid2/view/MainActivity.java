package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Contacts.ContactsActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu.CallsFragment;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu.CameraFragment;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu.ChatsFragment;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu.StatusFragment;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityMainBinding;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        View tab1 = LayoutInflater.from(this).inflate(R.layout.custom_camera_tab, null);
        try {
            binding.tabLayout.getTabAt(0).setCustomView(tab1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.viewPager.setCurrentItem(1);

        setSupportActionBar(binding.toolbar);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFabIcon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.newGroup:
                Toast.makeText(this, "New group", Toast.LENGTH_SHORT).show();
                break;
            case R.id.newBroadcast:
                Toast.makeText(this, "New broadcast", Toast.LENGTH_SHORT).show();
                break;
            case R.id.whatsappWeb:
                Toast.makeText(this, "Whatsapp web", Toast.LENGTH_SHORT).show();
                break;
            case R.id.standMessage:
                Toast.makeText(this, "Stand message", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpWithViewPager(ViewPager viewPager){
        MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager());
        myAdapter.addFragment(new CameraFragment(), "");
        myAdapter.addFragment(new CallsFragment(), "Calls");
        myAdapter.addFragment(new StatusFragment(), "Status");
        myAdapter.addFragment(new ChatsFragment(), "Chats");

        viewPager.setAdapter(myAdapter);


    }

    public static class MyAdapter extends FragmentPagerAdapter {

        private final ArrayList<Fragment> fragments = new ArrayList<>();
        private final ArrayList<String> fragmentTitles = new ArrayList<>();


        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }

    private static final int[] DRAWABLES = new int[]{R.drawable.call, R.drawable.call, R.drawable.status, R.drawable.chat};

    private void changeFabIcon(final int icon){
        binding.fabAction.hide();
        binding.fabAction.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                binding.fabAction.setImageDrawable(getDrawable(DRAWABLES[icon]));


                switch (icon){
                    case 0: binding.fabAction.hide(); break;
                    case 1:

                        binding.fabAction.show();
                        binding.fabAction.setOnClickListener(v -> {

                        });

                        break;
                    case 2:
                        binding.btnAddStatus.setVisibility(View.VISIBLE);
                        binding.fabAction.show();
                        binding.fabAction.setOnClickListener(v -> {

                        });
                        break;
                    case 3:
                        binding.fabAction.show();
                        binding.fabAction.setOnClickListener(v -> {
                            startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                        });

                        break;
                }
                //binding.fabAction.show();
            }
        },400);


    }
}