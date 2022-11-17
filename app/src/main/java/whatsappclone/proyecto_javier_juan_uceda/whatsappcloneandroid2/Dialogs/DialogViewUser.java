package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Common.Common;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Display.ViewImageActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.ChatList;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Chat.ChatActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Profile.ProfileActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Profile.UserProfileActivity;

public class DialogViewUser {
   private Context context;
   private ChatList chatList;

   public DialogViewUser(Context context, ChatList chatList) {
      this.context = context;
      this.chatList = chatList;
      initialize();
   }

   public void initialize(){
      Dialog dialog = new Dialog(context);
      dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);

      dialog.setContentView(R.layout.dialog_view_user);
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      dialog.setCancelable(true);
      WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

      lp.copyFrom(dialog.getWindow().getAttributes());
      lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
      lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

      dialog.getWindow().setAttributes(lp);

      ImageButton btnChat, btnCall, btnVideoCall, btnInfo;
      ImageView profile;
      TextView userName;

      btnChat = dialog.findViewById(R.id.btnChat);
      btnCall = dialog.findViewById(R.id.btnCall);
      btnVideoCall = dialog.findViewById(R.id.btnVideoCall);
      btnInfo = dialog.findViewById(R.id.btnInfo);
      profile = dialog.findViewById(R.id.profile);
      userName = dialog.findViewById(R.id.userName);

      userName.setText(chatList.getUserName());
      Glide.with(context).load(chatList.getUrlProfile()).into(profile);


      btnChat.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            //Toast.makeText(context, "Chats click", Toast.LENGTH_SHORT).show();

            context.startActivity(new Intent(context, ChatActivity.class)
                    .putExtra("userId",chatList.getUserID())
                    .putExtra("userName",chatList.getUserName())
                    .putExtra("userProfile",chatList.getUrlProfile()));
            dialog.dismiss();
         }
      });

      btnCall.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Toast.makeText(context, "Chats click", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
         }
      });

      btnInfo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
//            Toast.makeText(context, "Chats click", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, UserProfileActivity.class)
                    .putExtra("userId",chatList.getUserID())
                    .putExtra("userName",chatList.getUserName())
                    .putExtra("userProfile",chatList.getUrlProfile())
            );
            dialog.dismiss();
         }

      });

      btnVideoCall.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Toast.makeText(context, "Chats click", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
         }
      });

      profile.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            profile.invalidate();
            Drawable drawable = profile.getDrawable();
            Common.IMAGE_BITMAP = ((BitmapDrawable)drawable).getBitmap();
            ActivityOptionsCompat image = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, profile, "image");
            Intent intent = new Intent(context, ViewImageActivity.class);
            context.startActivity(intent,image.toBundle());
         }
      });

      dialog.show();
   }
}
