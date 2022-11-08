package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.CallList;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.Holder> {

   private List<CallList> chatLists = new ArrayList<>(0);
   private Context context;

   public CallListAdapter(List<CallList> chatLists, Context context) {
      this.chatLists = chatLists;
      this.context = context;
   }

   @NonNull
   @Override
   public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.layout_call_list, parent, false);

      return new Holder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull Holder holder, int position) {
      CallList callList = chatLists.get(position);
      holder.tvName.setText(callList.getUserName());
      holder.tvDate.setText(callList.getDate());

      switch (callList.getCallType()) {
         case "missed":
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.arrow));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_red_light));
            break;
         case "income":
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_upward_24));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_light));
            break;
         default:
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_upward_24));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_light));
            break;
      }
      Glide.with(context).load(callList.getUrlProfile()).into(holder.profile);
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

      private TextView tvName, tvDate;
      private CircleImageView profile;
      private ImageView arrow;

      public Holder(View view) {
         super(view);
         tvName = view.findViewById(R.id.tvName);
         tvDate = view.findViewById(R.id.tvDate);
         profile = view.findViewById(R.id.profile);
         arrow = view.findViewById(R.id.imgArrow);
      }
   }
}
