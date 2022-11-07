package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.concurrent.Delayed;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu.CallsFragment;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu.ChatsFragment;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu.StatusFragment;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


    }
    private void setUpWithViewPager(ViewPager viewPager){
        MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager());
        myAdapter.addFragment(new CallsFragment(), "Calls");
        myAdapter.addFragment(new StatusFragment(), "Status");
        myAdapter.addFragment(new ChatsFragment(), "Chats");
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
}