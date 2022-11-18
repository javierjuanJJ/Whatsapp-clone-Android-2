package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Managers;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnSendMessageCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnUploadImageCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.onReadChatCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Chat.Chat;

public class ChatServices {
    private Context context;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String receiverId;

    public ChatServices(Context context, String receiverId) {
        this.context = context;
        this.receiverId = receiverId;
    }

    public ChatServices(Context context) {
        this.context = context;
    }

    public void readChatServices(onReadChatCallback callback){
        if (callback != null) {
            try {
                final ArrayList<Chat> list = new ArrayList<>();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshotValue : snapshot.getChildren()) {
                            Chat chat = snapshotValue.getValue(Chat.class);
                            if (chat != null && !chat.getTextMessage().isEmpty() && chat.getSender().equals(user.getUid()) && chat.getReceiver().equals(receiverId)
                                    || chat.getReceiver().equals(user.getUid()) && chat.getSender().equals(receiverId)) {
                                list.add(chat);
                            }
                        }
                        callback.onReadSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onReadFailure(error.toException());
                    }
                });
            } catch (Exception e) {
                callback.onReadFailure(e);
            }
        }
    }


    public void sendMessageText(String textMessage, OnSendMessageCallback onSendMessageCallback) {
        if (onSendMessageCallback != null) {
            try {

                Chat chat = new Chat(getCurrentTime(), textMessage, "TEXT", user.getUid(), receiverId,"");

                reference.child("Chats").push().setValue(chat)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                onSendMessageCallback.onSendMessageComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                onSendMessageCallback.onSendMessageFailure(e);
                            }
                        });

                DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(user.getUid()).child(receiverId);
                chatRef1.child("chatId").setValue(receiverId);

                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverId).child(user.getUid());
                chatRef2.child("chatId").setValue(user.getUid());

            } catch (Exception e) {
                onSendMessageCallback.onSendMessageFailure(e);
            }
        }
    }

    public String getCurrentTime() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        Calendar currentDateTime = Calendar.getInstance();
        String today = formatter.format(date);
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");

        String currentTime = df.format(currentDateTime.getTime());
        return today + ", " + currentTime;
    }

    public void setImage(String imageUrl, OnUploadImageCallback callback){
        if (callback != null) {
            try {

                Chat chat = new Chat(getCurrentTime(), imageUrl, "IMAGE", user.getUid(), receiverId, imageUrl);

                reference.child("Chats").push().setValue(chat)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                callback.onUploadSuccess(imageUrl);
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                callback.onUploadSuccess(imageUrl);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                callback.onUploadFailure(e);
                            }
                        });

                DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(user.getUid()).child(receiverId);
                chatRef1.child("chatId").setValue(receiverId);

                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverId).child(user.getUid());
                chatRef2.child("chatId").setValue(user.getUid());

            } catch (Exception e) {
                callback.onUploadFailure(e);
            }
        }
    }
    public void sendVoice(String audioPath){
        Uri uriAudio = Uri.fromFile(new File(audioPath));
        final StorageReference audioRef = FirebaseStorage.getInstance().getReference().child("Chats/Voice" + System.currentTimeMillis() + ".mp3");
        audioRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());

                Uri downloadUrl = urlTask.getResult();
                String voiceUrl = String.valueOf(downloadUrl);

                Chat chat = new Chat(getCurrentTime(), "", "VOICE", user.getUid(), receiverId, voiceUrl);

                reference.child("Chats").push().setValue(chat)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.i("sendVoice","onSuccess(Void unused)" + voiceUrl);
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i("sendVoice", "onComplete(@NonNull Task<Void> task)" + voiceUrl);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("sendVoice", "onFailure(@NonNull Exception e)" + voiceUrl + " " + e.getMessage());
                            }
                        });

                DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(user.getUid()).child(receiverId);
                chatRef1.child("chatId").setValue(receiverId);

                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverId).child(user.getUid());
                chatRef2.child("chatId").setValue(user.getUid());



            }
        });
    }

    public boolean isEmptyMessage(String message) {
        return TextUtils.isEmpty(message);
    }

}
