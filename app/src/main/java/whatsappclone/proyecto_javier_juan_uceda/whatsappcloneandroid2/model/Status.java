package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model;

public class Status {
   private String id, userId, createDate, imageStatus, textStatus, viewCount;

   public Status(String id, String userId, String createDate, String imageStatus, String textStatus, String viewCount) {
      this.id = id;
      this.userId = userId;
      this.createDate = createDate;
      this.imageStatus = imageStatus;
      this.textStatus = textStatus;
      this.viewCount = viewCount;
   }

   public Status() {

   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public String getCreateDate() {
      return createDate;
   }

   public void setCreateDate(String createDate) {
      this.createDate = createDate;
   }

   public String getImageStatus() {
      return imageStatus;
   }

   public void setImageStatus(String imageStatus) {
      this.imageStatus = imageStatus;
   }

   public String getTextStatus() {
      return textStatus;
   }

   public void setTextStatus(String textStatus) {
      this.textStatus = textStatus;
   }

   public String getViewCount() {
      return viewCount;
   }

   public void setViewCount(String viewCount) {
      this.viewCount = viewCount;
   }
}
