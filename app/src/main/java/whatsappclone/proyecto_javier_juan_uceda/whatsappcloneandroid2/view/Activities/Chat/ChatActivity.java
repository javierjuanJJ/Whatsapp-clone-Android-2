package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Dialogs.DialogReviewSendImage;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnImageSetCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnSendMessageCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnShowCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnUploadImageCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.onReadChatCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Managers.ChatServices;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Managers.FirebaseServices;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.adapter.ChatAdapter;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityChatBinding;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Chat.Chat;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Profile.UserProfileActivity;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();
    private ActivityChatBinding binding;
    private String receiver;
    private ChatAdapter chatAdapter;
    private ArrayList<Chat> listChats;
    private String userProfile;
    private String userName;
    private boolean isActionActive;
    private ChatServices chatServices;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        setUI();
    }

    private void setUI() {
        initialize();
        readChats();
    }

    private void initialize() {
        Intent intent = getIntent();

        userName = intent.getStringExtra("userName");
        String userId = intent.getStringExtra("userId");
        userProfile = intent.getStringExtra("userProfile");

        if (receiver != null) {
            if (userId != null) {
                binding.tvUsername.setText(userName);

                if (userProfile.equals("")) {
                    binding.ivPlaceHolder.setImageResource(R.drawable.placeholder);
                }
                else {
                    Glide.with(getApplicationContext()).load(userProfile).into(binding.ivPlaceHolder);
                }
            }
        }

        receiver = userId;
        initRecyclerView();
        initBtnClick();
        binding.recordButton.setRecordView(binding.recordView);

        /*binding.recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                if (!checkPermissionFromDevice()){
                    binding.ivEmoticon.setVisibility(View.INVISIBLE);
                    binding.ivAttachment.setVisibility(View.INVISIBLE);
                    binding.ivCamera.setVisibility(View.INVISIBLE);
                    binding.etMessage.setVisibility(View.INVISIBLE);
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(100);
                    }
                }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions();
                    }
                }

                startRecord();

            }

            @Override
            public void onCancel() {
                try {

                    //mediaRecorder.reset();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                binding.ivEmoticon.setVisibility(View.VISIBLE);
                binding.ivAttachment.setVisibility(View.VISIBLE);
                binding.ivCamera.setVisibility(View.VISIBLE);
                binding.etMessage.setVisibility(View.VISIBLE);

                sTime = getHumanTime(System.currentTimeMillis());

                stopRecord();

            }

            @Override
            public void onLessThanSecond() {

            }
        });

        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                binding.ivEmoticon.setVisibility(View.VISIBLE);
                binding.ivAttachment.setVisibility(View.VISIBLE);
                binding.ivCamera.setVisibility(View.VISIBLE);
                binding.etMessage.setVisibility(View.VISIBLE);

            }
        });

    }

    private String getHumanTime(long currentTimeMillis) {
        return String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis) - TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis));
    }

    private static final int REQUEST_CORD_PERMISSION = 332;
    private MediaRecorder mediaRecorder;
    private String sTime;

    private void stopRecord(){
        Log.i("sendVoice","stopRecord()");
        try {
            if (mediaRecorder != null){
                Log.i("sendVoice","mediaRecorder != null");
                mediaRecorder.stop();
                Log.i("sendVoice","mediaRecorder.stop();");
                mediaRecorder.reset();
                Log.i("sendVoice"," mediaRecorder.reset();");
                //mediaRecorder.release();
                Log.i("sendVoice"," mediaRecorder.release();");
                //mediaRecorder = null;
                Log.i("sendVoice","mediaRecorder = null;");
                chatServices.sendVoice(audioPath);
                Log.i("sendVoice","chatServices.sendVoice(audioPath);");
                //addVoiceToList(audioPath);
                //uploadVoice();
            }
            else {
                Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalStateException e) {
            Toast.makeText(this, "Error uploading voice IllegalStateException", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error uploading voice IllegalStateException" + e.getMessage());
        }
        catch (Exception e) {
            Toast.makeText(this, "Error uploading voice", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error uploading voice" + e.getMessage());
        }
    }

    private void startRecord() {

        mediaRecorder = new MediaRecorder();
        Log.i("sendVoice","mediaRecorder = new MediaRecorder();");
        setUpMediaRecorder();
        Log.i("sendVoice","setUpMediaRecorder();");

        try {
            mediaRecorder.prepare();
            Log.i("sendVoice","mediaRecorder.prepare();");
            mediaRecorder.start();
            Log.i("sendVoice","mediaRecorder.start();");
        } catch (IOException e) {
            Toast.makeText(ChatActivity.this, "Recording error, please restart the App", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage());
        }
        catch (Exception e) {
            Toast.makeText(ChatActivity.this, "Recording error, please restart the App", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage());
        }

    }

    private String audioPath;

    private void setUpMediaRecorder() {
        String pathName = "";
        File dir = Environment.getExternalStorageDirectory().getAbsoluteFile();

        pathName = (new File(dir + "/" + "audiorecordtest.m4a")).getAbsolutePath();

        Log.i(TAG, pathName);
        audioPath = pathName;



        try {

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(pathName);

        } catch (IllegalStateException e) {
            Log.e(TAG, "IllegalStateException " + e.getMessage());
            //e.printStackTrace();

        }
        catch (Exception e) {
            Log.e(TAG, "Exception " + e.getMessage());
            //e.printStackTrace();
        }


    }

    private boolean checkPermissionFromDevice(){
        int writeStorageStorageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordAudioResult = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int manageExternalStorageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        int readExternalStorageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        return writeStorageStorageResult == PackageManager.PERMISSION_DENIED
                || recordAudioResult == PackageManager.PERMISSION_DENIED
                || manageExternalStorageResult == PackageManager.PERMISSION_DENIED
                || readExternalStorageResult == PackageManager.PERMISSION_DENIED;


    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
        }, REQUEST_CORD_PERMISSION);
    }

    private void initRecyclerView() {
        chatServices = new ChatServices(ChatActivity.this, receiver);
        listChats = new ArrayList<>();
        chatAdapter = new ChatAdapter(listChats, ChatActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void readChats() {

        chatServices.readChatServices(new onReadChatCallback() {
            @Override
            public void onReadSuccess(ArrayList<Chat> listChatToAdd) {

                chatAdapter = new ChatAdapter(listChatToAdd, ChatActivity.this);
                binding.recyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onReadFailure(Exception e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(ChatActivity.this, "Error reading chats", Toast.LENGTH_SHORT).show();
            }
        });
    }



    /*private void readChats() {
        listChats.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotValue : snapshot.getChildren()) {
                    Chat chat = snapshotValue.getValue(Chat.class);
                    if (chat != null && !chat.getTextMessage().isEmpty() && chat.getSender().equals(user.getUid()) && chat.getReceiver().equals(receiver)
                    || chat.getReceiver().equals(user.getUid()) && chat.getSender().equals(receiver)) {
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
    }*/

    private void initBtnClick() {

        binding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                int layoutId = chatServices.isEmptyMessage(binding.etMessage.getText().toString()) ? R.drawable.ic_baseline_keyboard_voice_24 : R.drawable.ic_baseline_send_24;
//                binding.fabChat.setImageDrawable(getDrawable(layoutId));

                if (chatServices.isEmptyMessage(binding.etMessage.getText().toString())){
                    binding.fabChat.setVisibility(View.INVISIBLE);
                    binding.recordButton.setVisibility(View.VISIBLE);
                    binding.fabChat.setImageDrawable(getDrawable(R.drawable.ic_baseline_keyboard_voice_24));
                }
                else {
                    binding.fabChat.setVisibility(View.VISIBLE);
                    binding.recordButton.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.etMessage.getText().toString();
                if (!chatServices.isEmptyMessage(message)){

                    chatServices.sendMessageText(message, new OnSendMessageCallback() {
                        @Override
                        public void onSendMessageComplete() {
                            Log.i(TAG, "Message " + message + " sent.");
                            readChats();
                        }

                        @Override
                        public void onSendMessageFailure(Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(ChatActivity.this, "Error send message", Toast.LENGTH_SHORT).show();
                        }
                    });

                    binding.etMessage.setText("");
                }
            }
        });

        binding.ivPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, UserProfileActivity.class)
                        .putExtra("userId",receiver)
                        .putExtra("userName",userName)
                        .putExtra("userProfile",userProfile)
                );
            }
        });
        binding.ivAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActionActive){
                    binding.layoutActions.setVisibility(View.GONE);
                    binding.layoutActions2.setVisibility(View.GONE);
                    isActionActive = false;
                }
                else {
                    binding.layoutActions.setVisibility(View.VISIBLE);
                    binding.layoutActions2.setVisibility(View.VISIBLE);
                    isActionActive = true;
                }
            }
        });

        binding.btnAttachmentGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                binding.ivPlaceHolder.setImageBitmap(bitmap);
                mediaImage(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void mediaImage(Bitmap bitmap) {
        DialogReviewSendImage dialogReviewSendImage = new DialogReviewSendImage(ChatActivity.this, bitmap);
        dialogReviewSendImage.show(new OnShowCallback() {
            @Override
            public void onButtonSendClick() {
                Log.i(TAG, "onButtonSendClick()");
                if (uri != null) {
                    ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
                    FirebaseServices firebaseServices = new FirebaseServices(ChatActivity.this);

                    progressDialog.setMessage("Please wait... ");
                    progressDialog.show();

                    binding.layoutActions.setVisibility(View.GONE);
                    isActionActive = false;

                    firebaseServices.uploadToFirebase(uri, new OnImageSetCallback() {
                        @Override
                        public void onUploadSuccess(String downloadUrl) {
                            chatServices.setImage(downloadUrl, new OnUploadImageCallback() {
                                @Override
                                public void onUploadSuccess(String downloadUrl) {
                                    Log.i(TAG, "Image " + downloadUrl + " uploaded successly.");
                                }

                                @Override
                                public void onUploadFailure(Exception e) {
                                    Toast.makeText(ChatActivity.this, "Error set image", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, e.getMessage());
                                }
                            });
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onUploadFailure(Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    });
                }
            }
        });
    }

    /*private void sendMessageText(String textMessage) {
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

           // readChats();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}