package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.adapter.ContactsListAdapter;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityContactsBinding;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Users.Users;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = ContactsActivity.class.getSimpleName();
    private ActivityContactsBinding binding;
    private ArrayList<Users> listUsers = new ArrayList<>();
    private ContactsListAdapter contactsListAdapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    public static final int REQUEST_READ_CONTACTS = 79;
    private ListView listView;
    private ArrayList mobileArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ContactsActivity.this,R.layout.activity_contacts);
        setUI();
    }

    private void setUI() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            //getContactsFromPhone();

            getContactsList();
        }
    }

    private void getContactsFromPhone() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            mobileArray = getAllContacts();
        }
        else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                    finish();
                }
                return;
            }
        }
    }
    private ArrayList<String> getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {

                String id = "";
                int idTable = cur.getColumnIndex(ContactsContract.Contacts._ID);
                if(idTable > -1) {
                    id = cur.getString(idTable);
                }

                String name = "";
                int displayNameTable = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                if(displayNameTable > -1) {
                    name = cur.getString(idTable);
                }


                int hasPhoneTable = cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                if (cur.getInt(hasPhoneTable) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        int numberTable = pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String phoneNo = "";
                        if (numberTable > -1) {
                            phoneNo = pCur.getString(numberTable);

                            nameList.add(phoneNo);

                        }
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return nameList;
    }

    private void getContactsList() {
        firestore.collection("Users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapShot : queryDocumentSnapshots) {
                            Log.i(TAG, "OnSuccessListener:");

                            String userId = snapShot.getString("userId");
                            String userName = snapShot.getString("userName");
                            String imageProfile = snapShot.getString("imageProfile");
                            String bio = snapShot.getString("bio");
                            String phone = snapShot.getString("userPhone");

                            Users users = new Users();
                            users.setUserId(userId);
                            users.setUserName(userName);
                            users.setImageProfile(imageProfile);
                            users.setBio(bio);
                            users.setUserPhone(phone);

                            if (userId != null && !userId.equals(firebaseUser.getUid())) {
                                if (mobileArray.contains(users.getUserPhone())) {
                                    listUsers.add(users);
                                }
                            }

                        }

                        contactsListAdapter = new ContactsListAdapter(listUsers, ContactsActivity.this);
                        binding.recyclerView.setAdapter(contactsListAdapter);

                    }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "OnFailureListener:");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.i(TAG, "OnCompleteListener:");
                    }
                });


    }
}