package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.Interfaces;

import java.util.ArrayList;

import whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Chat.Chat;

public interface OnSendMessageCallback {
 void onSendMessageComplete();
 void onSendMessageFailure(Exception e);
}
