package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnPlayCallBack;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Chat.Chat;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.tools.AudioServices;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

   private ArrayList<Chat> listChat;
   private Context context;
   private static final int MSG_TYPE_LEFT = 0;
   private static final int MSG_TYPE_RIGHT = 1;
   private ImageButton tmpBtnPlay;
   private AudioServices audioServices;


   public void setListChat(ArrayList<Chat> listChatToAdd) {
      this.listChat.clear();
      this.listChat.addAll(listChatToAdd);
      notifyDataSetChanged();
   }

   public ChatAdapter(ArrayList<Chat> listChat, Context context) {
      this.listChat = listChat;
      this.context = context;
      audioServices = new AudioServices(context);
   }



   @NonNull
   @Override
   public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      int layoutId = viewType == MSG_TYPE_LEFT ? R.layout.chat_item_left : R.layout.chat_item_right;
      View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

      return new ChatAdapter.ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
      holder.bind(listChat.get(position));
   }

   @Override
   public int getItemCount() {
      return listChat.size();
   }

   @Override
   public int getItemViewType(int position) {
      return listChat.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ? MSG_TYPE_RIGHT : MSG_TYPE_LEFT ;
   }

   public class ViewHolder extends RecyclerView.ViewHolder {

      private TextView textMessage;
      private LinearLayout layoutText, layoutImage, layoutVoice;
      private ImageView imageMessage;
      private ImageButton imageButton;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         textMessage = itemView.findViewById(R.id.tvTextMenssage);
         layoutText = itemView.findViewById(R.id.layoutText);
         layoutImage = itemView.findViewById(R.id.layoutImage);
         layoutVoice = itemView.findViewById(R.id.layoutAudio);
         imageMessage = itemView.findViewById(R.id.ivImageMessage);
         imageButton = itemView.findViewById(R.id.btnPlayVoice);
      }

      void bind(Chat chat){
         textMessage.setText(chat.getTextMessage());
         switch (chat.getType()){
            case "TEXT":
               layoutText.setVisibility(View.VISIBLE);
               layoutImage.setVisibility(View.GONE);
               layoutVoice.setVisibility(View.GONE);

               textMessage.setText(chat.getTextMessage());
               break;
            case "IMAGE":
               layoutText.setVisibility(View.GONE);
               layoutImage.setVisibility(View.VISIBLE);
               layoutVoice.setVisibility(View.GONE);

               Glide.with(context).load(chat.getUri()).into(imageMessage);
               break;

            case "VOICE":
               layoutText.setVisibility(View.GONE);
               layoutImage.setVisibility(View.GONE);
               layoutVoice.setVisibility(View.VISIBLE);

               layoutVoice.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                     if (tmpBtnPlay != null) {
                        tmpBtnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_24));
                     }

                     imageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_circle_24));

                     audioServices.playAudioFromUrl(chat.getUri(), new OnPlayCallBack() {
                        @Override
                        public void OnFinished() {
                           imageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_24));
                        }
                     });

                     tmpBtnPlay = imageButton;

                  }
               });

               break;
         }
      }
   }
}
