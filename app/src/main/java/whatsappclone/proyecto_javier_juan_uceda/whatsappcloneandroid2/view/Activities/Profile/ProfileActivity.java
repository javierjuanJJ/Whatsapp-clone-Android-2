package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.BuildConfig;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Common.Common;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Display.ViewImageActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityProfileBinding;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.stanup.WelcomeScreenActivity;

public class ProfileActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA_PHOTO = 440;
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

        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        if (firebaseUser != null) {
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
                showBottomSheetEditNameDialog();
            }
        });
        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.imageProfile.invalidate();
                Drawable drawable = binding.imageProfile.getDrawable();
                Common.IMAGE_BITMAP = ((BitmapDrawable)drawable).getBitmap();
                ActivityOptionsCompat image = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this, binding.imageProfile, "image");
                Intent intent = new Intent(ProfileActivity.this, ViewImageActivity.class);
                startActivity(intent,image.toBundle());

            }
        });
        binding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogSignOut();
            }
        });
    }





    private Uri imageUri;



    private void showBottomSheetEditNameDialog() {

        if (bottomSheetEditNameDialog == null) {
            bottomSheetEditNameDialog = new BottomSheetDialog(this);
        }
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name, null);

        view.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetEditNameDialog.dismiss();
            }
        });
        EditText etUserName = view.findViewById(R.id.etUserName);
        view.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etUserName.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    updateProfile(etUserName.getText().toString());
                    bottomSheetEditNameDialog.dismiss();
                }
            }
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetEditNameDialog.dismiss();
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


        bottomSheetEditNameDialog.show();
    }



    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, 222);
        }
        else if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 222);
        }
        else {
            //openCamera();
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
                openCamera();
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
        String imageFileName = "IMG_" + timeStmap;

        try {
            File file = File.createTempFile(imageFileName, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(ProfileActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);

            //uri = Uri.fromFile(file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("listPhotoName", file);
            Log.i("listPhotoName", file.getAbsolutePath());

            startActivityForResult(intent, REQUEST_CAMERA_PHOTO);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void updateProfile(String newName) {
        firestore.collection("Users").document(firebaseUser.getUid()).update("userName", newName).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        Log.i("listPhotoName", String.valueOf(requestCode));
        Log.i("listPhotoName", String.valueOf(resultCode));
        //Log.i("listPhotoName", String.valueOf(data.getData()));
        if (requestCode == IMAGE_REQUEST_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();

            uploadToFirebase();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                binding.imageProfile.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        else if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
        else if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == RESULT_OK) {

            //imageUri = data.getData();
            //uri = data.getData();
            uri = imageUri;
            Log.i("listPhotoName", String.valueOf(uri));
            uploadToFirebase();
        }
    }

    private void uploadToFirebase() {
        Log.i("listPhotoName", String.valueOf(uri));
        if (uri != null) {
            Log.i("listPhotoName", String.valueOf(uri));
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
                    firestore.collection("Users").document(firebaseUser.getUid()).update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Upload succesfully firebase", Toast.LENGTH_SHORT).show();

                                    getInfo();
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Upload succesfully", Toast.LENGTH_SHORT).show();

                                    getInfo();
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
                        Glide.with(getApplicationContext()).load(imageProfile).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(binding.imageProfile);
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

    private void showDialogSignOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setMessage("Do you want to sign out?");
        builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), WelcomeScreenActivity.class));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}