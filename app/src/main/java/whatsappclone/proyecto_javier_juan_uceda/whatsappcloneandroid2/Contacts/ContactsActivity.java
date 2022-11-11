package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ContactsActivity.this,R.layout.activity_contacts);
        setUI();
    }

    private void setUI() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            getContactsList();
        }
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

                            Users users = new Users();
                            users.setUserId(userId);
                            users.setUserName(userName);
                            users.setImageProfile(imageProfile);
                            users.setBio(bio);

                            if (userId != null && !userId.equals(firebaseUser.getUid())) {
                                listUsers.add(users);
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