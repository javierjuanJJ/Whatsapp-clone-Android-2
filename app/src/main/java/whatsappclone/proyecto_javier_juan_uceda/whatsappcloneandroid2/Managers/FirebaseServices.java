package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Managers;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnImageSetCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnUploadImageCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Chat.Chat;

public class FirebaseServices {

    private Context context;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public FirebaseServices(Context context) {
        this.context = context;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadToFirebase(Uri uri, OnImageSetCallback callback) {
        if (uri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ImagesChats/" + System.currentTimeMillis() + " " + getFileExtension(uri));

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    while (!result.isSuccessful()) ;

                    Uri downloadUri = result.getResult();
                    String downloadUrl = String.valueOf(downloadUri);

                    callback.onUploadSuccess(downloadUrl);

//                    HashMap<String, Object> map = new HashMap<>();
//                    map.put("imageProfile", downloadUrl);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onUploadFailure(e);

                }
            });
        }
    }



}
