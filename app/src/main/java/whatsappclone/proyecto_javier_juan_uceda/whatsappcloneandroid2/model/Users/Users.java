package whatsappclone.proyecto_javier_juan_uceda.whatsappcloneandroid2.model.Users;

public class Users {
   private String userId, userName, userPhone, imageProfile, imageCover, email, dateOfBirth, gender, status, bio;

   public Users(String userId, String userName, String userPhone, String imageProfile, String imageCover, String email, String dateOfBirth, String gender, String status, String bio) {
      this.userId = userId;
      this.userName = userName;
      this.userPhone = userPhone;
      this.imageProfile = imageProfile;
      this.imageCover = imageCover;
      this.email = email;
      this.dateOfBirth = dateOfBirth;
      this.gender = gender;
      this.status = status;
      this.bio = bio;
   }

   public Users() {

   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public String getUserName() {
      return userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getUserPhone() {
      return userPhone;
   }

   public void setUserPhone(String userPhone) {
      this.userPhone = userPhone;
   }

   public String getImageProfile() {
      return imageProfile;
   }

   public void setImageProfile(String imageProfile) {
      this.imageProfile = imageProfile;
   }

   public String getImageCover() {
      return imageCover;
   }

   public void setImageCover(String imageCover) {
      this.imageCover = imageCover;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getDateOfBirth() {
      return dateOfBirth;
   }

   public void setDateOfBirth(String dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
   }

   public String getGender() {
      return gender;
   }

   public void setGender(String gender) {
      this.gender = gender;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getBio() {
      return bio;
   }

   public void setBio(String bio) {
      this.bio = bio;
   }
}
