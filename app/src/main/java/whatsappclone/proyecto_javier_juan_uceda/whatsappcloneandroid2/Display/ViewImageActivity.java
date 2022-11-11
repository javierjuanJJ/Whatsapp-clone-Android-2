package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Common.Common;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityViewImageBinding;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ViewImageActivity.this,R.layout.activity_view_image);
        binding.imageView.setImageBitmap(Common.IMAGE_BITMAP);
    }
}