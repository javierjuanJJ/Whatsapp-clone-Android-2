package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(UserProfileActivity.this, R.layout.activity_user_profile);

        setUI();

    }

    private void setUI() {
        Intent intent = getIntent();

        String userName = intent.getStringExtra("userName");
        String userId = intent.getStringExtra("userId");
        String userProfile = intent.getStringExtra("userProfile");


        if (userName != null) {
            if (userId != null) {
                binding.collapse.setTitle(userName);

                if (userProfile.equals("")) {
                    binding.profile.setImageResource(R.drawable.placeholder);
                }
                else {
                    Glide.with(getApplicationContext()).load(userProfile).into(binding.profile);
                }
            }
        }

        initToolbar();

    }

    private void initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        else {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}