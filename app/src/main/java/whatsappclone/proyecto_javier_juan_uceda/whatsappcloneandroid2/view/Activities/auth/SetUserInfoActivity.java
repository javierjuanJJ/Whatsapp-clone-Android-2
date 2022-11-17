package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.BuildConfig;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivitySetUserInfoBinding;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Users.Users;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Profile.ProfileActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.MainActivity;

public class SetUserInfoActivity extends AppCompatActivity {
    private static final String TAG = SetUserInfoActivity.class.getSimpleName();
    private ActivitySetUserInfoBinding binding;

    private BottomSheetDialog bottomSheetDialog;
    private ProgressDialog progressDialog;


    private Uri imageUri;
    private Uri uri;
    private static final int IMAGE_REQUEST_GALLERY = 1;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_info);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            binding.etName.setText(task.getResult().getString("userName"));
                            Glide.with(SetUserInfoActivity.this).load(task.getResult().getString("userName")).into(binding.ivProfile);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


        setUI();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST_GALLERY);
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
                    uploadToFirebase();
                }



            }
        });
        progressDialog = new ProgressDialog(this);

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showBottomSheetDialog();
            }
        });
    }
/*

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
*/

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(SetUserInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SetUserInfoActivity.this, new String[]{Manifest.permission.CAMERA}, 222);
        }
        else if (ContextCompat.checkSelfPermission(SetUserInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SetUserInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 222);
        }
        else {
            openCamera();
        }
    }

    private void showBottomSheetDialog() {

        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(this);
        }

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick, null);
        view.findViewById(R.id.layoutGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });

        view.findViewById(R.id.layoutCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Camera", Toast.LENGTH_SHORT).show();

                checkCameraPermissions();

                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                bottomSheetDialog = null;
            }
        });


        bottomSheetDialog.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStmap = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStmap + ".jpg";

        try {
            File file = File.createTempFile("IMG_" + timeStmap, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(SetUserInfoActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("listPhotoName", imageFileName);

            startActivityForResult(intent, 440);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            //uploadToFirebase();
            Glide.with(SetUserInfoActivity.this).load(imageUri).into(binding.ivProfile);
        }

        if (requestCode == 440 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            //uploadToFirebase();
            Glide.with(SetUserInfoActivity.this).load(imageUri).into(binding.ivProfile);
        }
    }

    private void uploadToFirebase() {
        if (uri != null) {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("imageProfile/" + System.currentTimeMillis() + " " + getFileExtension(uri));

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    while (!result.isSuccessful()) ;

                    Uri downloadUri = result.getResult();
                    String downloadUrl = String.valueOf(downloadUri);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageProfile", downloadUrl);
                    map.put("userName", binding.etName.getText().toString());

                    db.collection("Users").document(firebaseUser.getUid()).update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Upload succesfully firebase", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    //Toast.makeText(getApplicationContext(), "Upload succesfully", Toast.LENGTH_SHORT).show();

                                    //progressDialog.dismiss();
                                    Toast.makeText(SetUserInfoActivity.this, "Update succesfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("ProfileActivity",e.getMessage());
                                    Toast.makeText(getApplicationContext(), "Upload wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.e("ProfileActivity", e.getMessage());
                    Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}