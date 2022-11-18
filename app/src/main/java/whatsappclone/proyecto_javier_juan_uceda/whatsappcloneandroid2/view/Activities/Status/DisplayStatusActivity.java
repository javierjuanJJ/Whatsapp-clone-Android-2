package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Status;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityDisplayStatusBinding;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityStatusBinding;

public class DisplayStatusActivity extends AppCompatActivity {


    private ActivityDisplayStatusBinding binding;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(DisplayStatusActivity.this,R.layout.activity_display_status);

        setUI();

    }

    private void setUI() {
        binding.btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (uri != null) {
            Glide.with(DisplayStatusActivity.this).load(uri).into(binding.ivPlaceholder);
        }

    }
}