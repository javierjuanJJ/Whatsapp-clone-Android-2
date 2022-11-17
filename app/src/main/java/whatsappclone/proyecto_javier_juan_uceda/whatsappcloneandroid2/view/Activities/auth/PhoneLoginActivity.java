package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.view.Activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.databinding.ActivityPhoneLoginBinding;

public class PhoneLoginActivity extends AppCompatActivity {

    private ActivityPhoneLoginBinding binding;

    private FirebaseAuth mAuth;
    private String mVerificationId;


    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationIdInProgress;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private  FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        setUI();

    }

    private void setUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){

            startActivity(new Intent(this, SetUserInfoActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        binding.btnCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnCountry.getText().toString().equals("Next")) {

                    progressDialog.setMessage("Please wait");
                    progressDialog.show();

                    String phone = "+" +  binding.etCodeCountry.getText().toString() +binding.etPhoneNumber.getText().toString();
                    startPhoneNumberVerification(phone);

                } else {
                    progressDialog.setMessage("Verifying");

                    verifyPhoneNumberWithCode(mVerificationId, binding.etCode.getText().toString());
                    progressDialog.show();
                }
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                signInWithAuthCredential(phoneAuthCredential);
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("PhoneLoginActivity",e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = s;

                mResendToken = forceResendingToken;

                binding.btnCountry.setText("Confirm");
                binding.etCodeCountry.setVisibility(View.VISIBLE);

                progressDialog.dismiss();
            }
        };
    }


    private void startPhoneNumberVerification(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );

        //mVerificationIdInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithAuthCredential(credential);
    }

    private void signInWithAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    progressDialog.dismiss();

                    FirebaseUser user = task.getResult().getUser();
                    startActivity(new Intent(PhoneLoginActivity.this, SetUserInfoActivity.class));
//                    if (user != null) {
//                        String uid = user.getUid();
//                        Users users = new Users("",user.getPhoneNumber(),"","","","","","","","");
//                        firestore.collection("Users").document("UserInfo").collection(uid).add(users).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentReference> task) {
//                                startActivity(new Intent(PhoneLoginActivity.this, SetUserInfoActivity.class));
//                            }
//                        });
//                    }
//                    else {
//                        Toast.makeText(PhoneLoginActivity.this, "Something error", Toast.LENGTH_SHORT).show();
//                    }
                }
                else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        Log.e("PhoneLoginActivity",task.getException().getMessage());
                    }

                }
            }
        });
    }

}