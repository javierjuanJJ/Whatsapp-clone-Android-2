package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.tools;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces.OnPlayCallBack;

public class AudioServices {
   private Context context;
   private MediaPlayer mediaPlayer;
   private OnPlayCallBack onPlayCallBack;

   public AudioServices(Context context) {
      this.context = context;
      this.mediaPlayer = new MediaPlayer();
   }

   public void playAudioFromUrl(String url, OnPlayCallBack onPlayCallBack){

      if (this.onPlayCallBack == null && onPlayCallBack != null){
         this.onPlayCallBack = onPlayCallBack;
      }

      if (this.mediaPlayer != null) {
         this.mediaPlayer.stop();
      }

      MediaPlayer mediaPlayer = new MediaPlayer();

         try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            this.mediaPlayer = mediaPlayer;

         } catch (IOException e) {

         }

         this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
               mp.release();
               if (onPlayCallBack != null) {
                  onPlayCallBack.OnFinished();
               }
            }
         });
   }


}
