package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view;

import static whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Profile.ProfileActivity.REQUEST_CAMERA_PHOTO;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.BuildConfig;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Contacts.ContactsActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu.CallsFragment;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu.CameraFragment;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu.ChatsFragment;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu.StatusFragment;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityMainBinding;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Profile.ProfileActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Settings.SettingsActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Status.StatusActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Uri imageUri;

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
                        break;
                    case 2:
                        binding.btnAddStatus.setVisibility(View.VISIBLE);
                        binding.fabAction.show();
                        break;
                    case 3:
                        binding.fabAction.show();
                        break;
                }
                //binding.fabAction.show();
            }
        },400);

        performOnClick(icon);
    }


    private void performOnClick(final int index){
        binding.fabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (index){
                    case 1: startActivity(new Intent(MainActivity.this, ContactsActivity.class));break;
                    case 2:
                        checkCameraPermissions();
                        Toast.makeText(MainActivity.this, "Camera", Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        Toast.makeText(MainActivity.this, "Call", Toast.LENGTH_SHORT).show();
                        break;
                    default: break;
                }
            }
        });
        binding.btnAddStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Add status... ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 222);
        }
        else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 222);
        }
        else {
            openCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("listPhotoName", String.valueOf(requestCode));
        Log.i("listPhotoName", String.valueOf(resultCode));

        if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == RESULT_OK && imageUri != null) {

            //imageUri = data.getData();
            //uri = data.getData();
            Log.i("listPhotoName", String.valueOf(imageUri));
            startActivity(new Intent(MainActivity.this, StatusActivity.class));
            //uploadToFirebase();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStmap = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStmap;

        try {
            File file = File.createTempFile(imageFileName, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);

            //uri = Uri.fromFile(file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("listPhotoName", file);
            Log.i("listPhotoName", file.getAbsolutePath());

            startActivityForResult(intent, REQUEST_CAMERA_PHOTO);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}