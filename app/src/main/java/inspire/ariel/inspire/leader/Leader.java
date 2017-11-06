package inspire.ariel.inspire.leader;

import com.backendless.BackendlessUser;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.common.quoteslist.Quote;

public class Leader {

    private String objectId;
    private String name;
    private String description;
    //private Bitmap image = ImageUtils.defaultProfileImage;
    private List<BackendlessUser> followers;
    private List<Quote> quotes;

    //Convenience Constructor
    public Leader() {
        this.name = "Leader Name";
        this.description = "Leader Description";
        this.followers = new ArrayList<>();
        this.followers.add(new BackendlessUser());
    }

    //************Setters****************//
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFollowers(List<BackendlessUser> followers) {
        this.followers = followers;
    }


    //*************Getters*******************//

    public String getObjectId() {
        return objectId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

   /* public Bitmap getImage() {
        return image;
    }*/

    public List<BackendlessUser> getFollowers() {
        return followers;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }


}
