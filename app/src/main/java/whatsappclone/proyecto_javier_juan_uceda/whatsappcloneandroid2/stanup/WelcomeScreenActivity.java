package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.stanup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.MainActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;

public class WelcomeScreenActivity extends AppCompatActivity {

    private Button btnAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        setUI();
    }

    private void setUI() {
        btnAgree = findViewById(R.id.btnAgree);

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeScreenActivity.this, MainActivity.class));
            }
        });

    }
}