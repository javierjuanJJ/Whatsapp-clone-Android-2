package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Status;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URI;
import java.util.UUID;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnImageSetCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnStatusUploadCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Managers.ChatServices;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Managers.FirebaseServices;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityStatusBinding;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Status;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.MainActivity;

public class StatusActivity extends AppCompatActivity {

    private static final String TAG = StatusActivity.class.getSimpleName() + " Own";
    private ActivityStatusBinding binding;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(StatusActivity.this, R.layout.activity_status);

        setUI();
        initClick();

    }

    private void initClick() {
        binding.btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(binding.etCaption.getText().toString())){
                    Toast.makeText(StatusActivity.this, "Add caption. Caption can not empty.", Toast.LENGTH_SHORT).show();
                }
                else {
                    new FirebaseServices(StatusActivity.this).uploadToFirebase(uri, new OnImageSetCallback() {
                        @Override
                        public void onUploadSuccess(String downloadUrl) {
                            Status status = new Status();
                            status.setId(UUID.randomUUID().toString());
                            status.setCreateDate(new ChatServices(StatusActivity.this).getCurrentTime());
                            status.setImageStatus(downloadUrl);
                            status.setTextStatus(binding.etCaption.getText().toString());
                            status.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            status.setViewCount("0");

                            new FirebaseServices(StatusActivity.this).uploadStatus(status, new OnStatusUploadCallback() {
                                @Override
                                public void onUploadSuccess(String downloadUrl) {
                                    Toast.makeText(StatusActivity.this, "Status added", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "Status added " + downloadUrl);
                                    finish();
                                }

                                @Override
                                public void onUploadFailure(Exception e) {
                                    Toast.makeText(StatusActivity.this, "Status not added", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Status not added " + e.getMessage());
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onUploadFailure(Exception e) {

                        }
                    });
                }
            }
        });

    }

    private void setUI() {
        if (MainActivity.imageCamera != null) {
            uri = MainActivity.imageCamera;

            Glide.with(StatusActivity.this).load(uri).into(binding.ivProfile);
        }

    }
}