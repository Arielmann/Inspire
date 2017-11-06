package inspire.ariel.inspire.common.quoteslist;

import android.graphics.Bitmap;

import java.util.UUID;

import inspire.ariel.inspire.leader.Leader;
import inspire.ariel.inspire.common.utils.listutils.ListPresentable;

public class Quote implements ListPresentable{

    private String objectId;
    private String message;
    private Leader leader;
    //private String imageLocalPath;
    //String imageUrl;
    //private Bitmap image = ImageUtils.defaultProfileImage;

    //Convenience Constructor
    public Quote() {
        this.message = "This is a great quote";
    }

    //Called when first time created
    public Quote(String name, String description) { //Convenience Constructor
        this.objectId = UUID.randomUUID().toString();
        this.message = description;
        //this.imageLocalPath = "Entity's Local Image Path";
        //this.imageUrl = "Entity's Local Image Url";
    }

    //Called when loading from local OR remote database (with creation date)
    public Quote(String objectId, String name, String description) {
        this.objectId = objectId;
        this.message = description;
    }

    @Override
    public String toString() {
        return "Quote Info: Id: " + objectId +
                " Message: " + message;
                //" Creation Date: " + creationDate +
                //" Profile image local file path:" + imageLocalPath +
                //" Profile image url:" + imageUrl;
    }

    //**Getters**//
    public String getObjectId() {
        return objectId;
    }

    public String getMessage() {
        return message;
    }

  /*  public String getCreationDate() {
        return creationDate;
    }

    public String getImageLocalPath() {
        return imageLocalPath;
    }


    public String getImageUrl() {
        return imageUrl;
    }*/


    //**Setters**//


/*    public void setImageLocalPath(String entityImageLocalPath) {
        this.imageLocalPath = entityImageLocalPath;
    }

    public void setImageUrl(String entityImageUrl) {
        this.imageUrl = entityImageUrl;
    }*/

    public void setMessage(String message) {
        this.message = message;
    }

    public Bitmap getImage() {
        throw new NullPointerException("Image was not defined getter of Quote class");
    }

    public void setImage(Bitmap image)  {
        throw new NullPointerException("image is not defined in Quote class");
        //this.image = image;
    }

    public Leader getLeader() {
        return leader;
    }

    public void setLeader(Leader leader) {
        this.leader = leader;
    }
}




