package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jsibbold.zoomage.ZoomageView;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnShowCallback;
import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.R;

public class DialogReviewSendImage {
    private Context context;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private ZoomageView image;
    private FloatingActionButton btnSend;

    public DialogReviewSendImage(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
        progressDialog = new ProgressDialog(context);
        dialog = new Dialog(context);
        initialize();
    }

    private void initialize() {
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.activity_review_send_image);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.getWindow().setAttributes(lp);

        image = dialog.findViewById(R.id.imageView);
        btnSend = dialog.findViewById(R.id.fabSend);

    }
    public void show(OnShowCallback callback) {
        dialog.show();
        Log.i("TAG", "dialog.show();");
        image.setImageBitmap(bitmap);
        Log.i("TAG", "image.setImageBitmap(bitmap);");
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onButtonSendClick();
                dialog.dismiss();
            }
        });
    }




}
