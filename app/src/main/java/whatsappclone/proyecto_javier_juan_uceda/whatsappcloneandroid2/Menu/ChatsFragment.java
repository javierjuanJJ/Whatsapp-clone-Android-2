package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.adapter.ChatListAdapter;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.FragmentChatsBinding;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.ChatList;

public class ChatsFragment extends Fragment {


    private static final String TAG = ChatsFragment.class.getSimpleName();
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private DatabaseReference reference;
    private ChatListAdapter adapter;

    public ChatsFragment() {
        // Required empty public constructor
    }

    private List<ChatList> list = new ArrayList<>();
    private List<String> allUserId = new ArrayList<>();
    private FragmentChatsBinding binding;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(list, getContext());
        binding.recyclerView.setAdapter(adapter);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        if (firebaseUser != null) {
            getChatList();
        }

        return binding.getRoot();
    }

    private void getChatList() {
        binding.prograssbar.setVisibility(View.VISIBLE);
        reference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                allUserId.clear();
                for (DataSnapshot snapshotChilden : snapshot.getChildren()) {
                    String userId = snapshotChilden.child("chatId").getValue().toString();
                    Log.i(TAG, "onDataChange: userId:" + userId);
                    allUserId.add(userId);
                }

                getUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUserInfo() {

        handler.post(() -> {
            binding.prograssbar.setVisibility(View.GONE);
            for (String userId : allUserId) {
                firestore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            ChatList chatList = new ChatList(
                                    documentSnapshot.getString("userID"),
                                    documentSnapshot.getString("userName"),
                                    "This is a description",
                                    "",
                                    documentSnapshot.getString("imageProfile")
                            );
                            list.add(chatList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (adapter != null) {
                            adapter.notifyItemInserted(0);
                            adapter.notifyDataSetChanged();
                        }

//                        Log.i(TAG, "onDataChange: userId:" + userId);
//                        allUserId.add(userId);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    }
                });
            }
        });
        binding.recyclerView.setAdapter(new ChatListAdapter(list, getContext()));

    }
}