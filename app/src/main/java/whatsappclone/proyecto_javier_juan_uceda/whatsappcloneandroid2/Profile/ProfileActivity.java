package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private BottomSheetDialog bottomSheetDialog, bottomSheetEditNameDialog;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setUI();
    }

    private void setUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        if (firebaseUser != null){
            getInfo();
        }

        initClickAction();

    }

    private void initClickAction() {
        binding.fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        binding.layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
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
                Toast.makeText(getApplicationContext(), "Camera", Toast.LENGTH_SHORT).show();
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
    private void showBottomSheetEditNameDialog() {

        if (bottomSheetEditNameDialog == null) {
            bottomSheetEditNameDialog = new BottomSheetDialog(this);
        }
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name, null);

        view.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });
        EditText etUserName = view.findViewById(R.id.etUserName);
        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etUserName.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    updateProfile(etUserName.getText().toString());
                    bottomSheetDialog.dismiss();
                }



            }
        });

        bottomSheetEditNameDialog.setContentView(view);

        bottomSheetEditNameDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        bottomSheetEditNameDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                bottomSheetEditNameDialog = null;
            }
        });


        bottomSheetDialog.show();
    }

    private void updateProfile(String newName) {
        firestore.collection("Users").document(firebaseUser.getUid()).update("userName",newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Update succesful", Toast.LENGTH_SHORT).show();

                getInfo();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private static final int IMAGE_REQUEST_GALLERY = 1;

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST_GALLERY);
    }

    private Uri uri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_GALLERY && resultCode == RESULT_OK &&  data != null && data.getData() != null){
            uri = data.getData();
            uploadToFirebase();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                binding.imageProfile.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void uploadToFirebase() {
        if (uri!=null){
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("imageProfile/" + System.currentTimeMillis() + " " + getFileExtension(uri));

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    while (!result.isSuccessful());

                    Uri downloadUri = result.getResult();
                    String downloadUrl = String.valueOf(downloadUri);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageProfile",downloadUrl);
                    firestore.collection("Users").document(firebaseUser.getUid()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Upload succesfully", Toast.LENGTH_SHORT).show();

                            getInfo();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String userName = documentSnapshot.getString("userName");
                        String userPhone = documentSnapshot.getString("userPhone");
                        String imageProfile = documentSnapshot.getString("imageProfile");

                        binding.tvUsername.setText(userName);
                        binding.tvInfoPhone.setText(userPhone);
                        Glide.with(getApplicationContext()).load(imageProfile).into(binding.imageProfile);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}