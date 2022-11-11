package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        setUI();
    }

    private void setUI() {
        Intent intent = getIntent();

        String userName = intent.getStringExtra("userName");
        String userId = intent.getStringExtra("userId");
        String userProfile = intent.getStringExtra("userProfile");

        if (userId != null) {
            binding.tvUsername.setText(userName);

            Glide.with(ChatActivity.this).load(userProfile).into(binding.ivPlaceHolder);
        }

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}