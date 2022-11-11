package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.adapter.ChatAdapter;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityChatBinding;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Chat.Chat;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();
    private ActivityChatBinding binding;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String receiver;
    private ChatAdapter chatAdapter;
    private ArrayList<Chat> listChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        setUI();
    }

    private void setUI() {
        Intent intent = getIntent();

        String userName = intent.getStringExtra("userName");
        String userId = intent.getStringExtra("userId");
        String userProfile = intent.getStringExtra("userProfile");
        listChats = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        if (userId != null) {
            binding.tvUsername.setText(userName);

            Glide.with(ChatActivity.this).load(userProfile).into(binding.ivPlaceHolder);
        }

        receiver = userId;

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageText(binding.etMessage.getText().toString());
            }
        });

        binding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int layoutId = isEmptyMessage() ? R.drawable.ic_baseline_keyboard_voice_24 : R.drawable.ic_baseline_send_24;
                binding.fabChat.setImageDrawable(getDrawable(layoutId));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        initBtnClick();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        readChats();


    }

    private void readChats() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotValue : snapshot.getChildren()) {
                    Chat chat = snapshotValue.getValue(Chat.class);
                    if (chat != null && chat.getSender().equals(user.getUid()) && chat.getReceiver().equals(receiver) ) {
                        listChats.add(chat);
                    }
                }

                if (chatAdapter != null) {
                    chatAdapter.notifyDataSetChanged();
                }
                else {
                    chatAdapter = new ChatAdapter(listChats, ChatActivity.this);
                    binding.recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isEmptyMessage() {
        return TextUtils.isEmpty(binding.etMessage.getText().toString());
    }

    private void initBtnClick() {
        if (isEmptyMessage()){
            sendMessageText(binding.etMessage.getText().toString());

            binding.etMessage.setText("");
        }
    }

    private void sendMessageText(String textMessage) {
        try {
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

            Calendar currentDateTime = Calendar.getInstance();
            String today = formatter.format(date);
            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");

            String currentTime = df.format(currentDateTime.getTime());
            Chat chat = new Chat(today + ", " + currentTime, textMessage, "TEXT", user.getUid(), receiver);

            reference.child("Chats").push().setValue(chat)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i(TAG,"onSuccess:");
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG,"onComplete:");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"onFailure:");
                        }
                    });

            DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(user.getUid()).child(receiver);
            chatRef1.child("chatId").setValue(receiver);

            DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiver).child(user.getUid());
            chatRef2.child("chatId").setValue(user.getUid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}