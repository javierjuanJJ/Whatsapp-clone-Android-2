package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;

public class CameraFragment extends Fragment {

    public CameraFragment() {
        // Required empty public constructor
    }

    public static CameraFragment newInstance(){
        return new CameraFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_camera, container, false);



        return inflate;
    }
}