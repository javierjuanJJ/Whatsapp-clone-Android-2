package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Profile.ProfileActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setUI();
    }

    private void setUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firestore = FirebaseFirestore.getInstance();

        if (binding != null) {
            getInfo();
        }
        initClickAction();
    }

    private void initClickAction() {
        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });
    }

    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.i("SettingsActivity","On success successly");
                        String userName = Objects.requireNonNull(documentSnapshot.get("userName")).toString();
                        binding.username.setText(userName);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i("SettingsActivity","On Complete successly");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("SettingsActivity",e.getMessage());
                Toast.makeText(SettingsActivity.this, "Log in failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}