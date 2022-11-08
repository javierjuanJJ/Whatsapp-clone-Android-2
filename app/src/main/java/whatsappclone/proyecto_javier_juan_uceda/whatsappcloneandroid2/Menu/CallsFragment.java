package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.adapter.CallListAdapter;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.adapter.ChatListAdapter;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.CallList;

public class CallsFragment extends Fragment {


    private RecyclerView recyclerView;

    public CallsFragment() {
        // Required empty public constructor
    }

    public static CallsFragment newInstance() {
        CallsFragment fragment = new CallsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_calls, container, false);

        recyclerView = inflate.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        getChatList();
        return inflate;
    }

    private void getChatList() {
        List<CallList> list = new ArrayList<>();
        //list.add(new ChatList("","","","",""))
        recyclerView.setAdapter(new CallListAdapter(list,getContext()));
    }
}