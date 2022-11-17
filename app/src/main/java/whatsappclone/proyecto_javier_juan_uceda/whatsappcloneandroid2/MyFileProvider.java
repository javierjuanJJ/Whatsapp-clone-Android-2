package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2;

import androidx.core.content.FileProvider;

public class MyFileProvider extends FileProvider {
   public MyFileProvider() {
      super(R.xml.file_paths);
   }
}
