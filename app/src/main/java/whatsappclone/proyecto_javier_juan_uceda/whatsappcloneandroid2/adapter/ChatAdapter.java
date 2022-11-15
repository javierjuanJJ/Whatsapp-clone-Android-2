package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Chat.Chat;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

   private ArrayList<Chat> listChat;
   private Context context;
   private static final int MSG_TYPE_LEFT = 0;
   private static final int MSG_TYPE_RIGHT = 1;

   public void setListChat(ArrayList<Chat> listChatToAdd) {
      this.listChat.clear();
      this.listChat.addAll(listChatToAdd);
      notifyDataSetChanged();
   }

   public ChatAdapter(ArrayList<Chat> listChat, Context context) {
      this.listChat = listChat;
      this.context = context;
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

      public ViewHolder(@NonNull View itemView) {
         super(itemView);
         textMessage = itemView.findViewById(R.id.tvTextMenssage);
      }

      void bind(Chat chat){
         textMessage.setText(chat.getTextMessage());
      }
   }
}
