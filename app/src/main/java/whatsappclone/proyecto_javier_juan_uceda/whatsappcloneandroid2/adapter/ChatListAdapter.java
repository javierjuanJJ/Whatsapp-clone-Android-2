package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Dialogs.DialogViewUser;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.ChatList;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.Chat.ChatActivity;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {

   private List<ChatList> chatLists = new ArrayList<>(0);
   private Context context;




   public ChatListAdapter(List<ChatList> chatLists, Context context) {
      this.chatLists = chatLists;
      this.context = context;
   }

   @NonNull
   @Override
   public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.layout_chat_list, parent, false);

      return new Holder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull Holder holder, int position) {
      ChatList chatList = chatLists.get(position);

      if (holder.tvName != null && holder.tvDesc != null && holder.tvDate != null) {
         holder.tvName.setText(chatList.getUserName());
         holder.tvDesc.setText(chatList.getDescription());
         holder.tvDate.setText(chatList.getDate());

         if (chatList.getUrlProfile().equals("")) {
            holder.profile.setImageResource(R.drawable.placeholder);
         }
         else {
            Glide.with(context).load(chatList.getUrlProfile()).into(holder.profile);
         }

         holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               context.startActivity(new Intent(context, ChatActivity.class)
                       .putExtra("userId",chatList.getUserID())
                       .putExtra("userName",chatList.getUserName())
                       .putExtra("userProfile",chatList.getUrlProfile()));
            }
         });

         holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new DialogViewUser(context, chatList);
            }
         });
      }
   }

   /**
    * Returns the total number of items in the data set held by the adapter.
    *
    * @return The total number of items in this adapter.
    */
   @Override
   public int getItemCount() {
      return chatLists.size();
   }

   public class Holder extends RecyclerView.ViewHolder {

      private TextView tvName, tvDesc, tvDate;
      private CircleImageView profile;

      public Holder(View view) {
         super(view);
      }
   }
}
