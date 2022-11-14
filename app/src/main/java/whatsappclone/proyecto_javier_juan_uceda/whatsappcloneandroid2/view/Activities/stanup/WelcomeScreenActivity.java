package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.stanup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.MainActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.auth.PhoneLoginActivity;

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
                if (FirebaseAuth.getInstance().getCurrentUser() == null){
                    startActivity(new Intent(WelcomeScreenActivity.this, PhoneLoginActivity.class));
                }
                else {
                    startActivity(new Intent(WelcomeScreenActivity.this, MainActivity.class));
                }


            }
        });

    }
}