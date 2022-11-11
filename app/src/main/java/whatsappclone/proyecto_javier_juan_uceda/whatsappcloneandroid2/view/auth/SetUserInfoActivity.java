package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.ProtectionDomain;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivitySetUserInfoBinding;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Users.Users;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.MainActivity;

public class SetUserInfoActivity extends AppCompatActivity {
    private ActivitySetUserInfoBinding binding;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_info);
        setUI();
    }

    private void setUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_user_info);
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pickImage();

                if (TextUtils.isEmpty(binding.etName.getText().toString())){
                    Toast.makeText(SetUserInfoActivity.this, "Please input the name first", Toast.LENGTH_SHORT).show();
                }
                else {
                    doUpdate();
                }



            }
        });
        progressDialog = new ProgressDialog(this);

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(binding.etName.getText().toString())){
                    Toast.makeText(SetUserInfoActivity.this, "Please input the name first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void doUpdate() {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            Users users = new Users(uid,binding.etName.getText().toString(),firebaseUser.getPhoneNumber(),"","","","","","","");
            //firebaseFirestore.collection("Users").document(firebaseUser.getUid()).update("username",binding.etName.getText().toString())
            firebaseFirestore.collection("Users").document(uid).set(users)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Toast.makeText(SetUserInfoActivity.this, "Update succesfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SetUserInfoActivity.this, "Update wrong", Toast.LENGTH_SHORT).show();
                    Log.e("errorFirebase", e.getMessage());
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "You need to login first", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}