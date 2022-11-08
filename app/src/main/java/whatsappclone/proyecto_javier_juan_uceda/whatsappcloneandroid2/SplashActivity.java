package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.stanup.WelcomeScreenActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, WelcomeScreenActivity.class));
                finish();
            }
        }, 3000);

    }
}