package inspire.ariel.inspire.common.treatslist;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import inspire.ariel.inspire.common.constants.AppStrings;
import io.realm.RealmModel;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.RealmClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import weborb.service.ExcludeProperty;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(exclude = {AppStrings.KEY_IMAGE}, callSuper = false)
@ToString
@RealmClass
@ExcludeProperty( propertyName = AppStrings.KEY_IS_PURCHASED )
public class Treat implements Parcelable, RealmModel {

    @NonNull private String ownerId;
    @NonNull private String text;
    private int textColor;
    private int textSize;
    @NonNull private String fontPath;
    @NonNull private String bgImageName;
    private String objectId;
    @NonNull @Index private Date created;
    @Ignore private Drawable image;
    @Setter private boolean isPurchased;

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }


    public Treat(){}

    protected Treat(Parcel in) {
        text = in.readString();
        ownerId = in.readString();
        textColor = in.readInt();
        textSize = in.readInt();
        fontPath = in.readString();
        bgImageName = in.readString();
        objectId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(ownerId);
        dest.writeInt(textColor);
        dest.writeInt(textSize);
        dest.writeString(fontPath);
        dest.writeString(bgImageName);
        dest.writeString(objectId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Treat> CREATOR = new Creator<Treat>() {
        @Override
        public Treat createFromParcel(Parcel in) {
            return new Treat(in);
        }

        @Override
        public Treat[] newArray(int size) {
            return new Treat[size];
        }
    };

    /*public static Quote newQuote(String text){
        return Quote.builder().text(text)
                .fontPath(FontsManager.Font.ALEF_BOLD.getPath())
                .bgImageName(AppStrings.BLUE_YELLOW_BG)
                .ownerId(AppStrings.VAL_OWNER_OBJECT_ID)
                .textSize(Math.round(res.getDimension(R.dimen.error_msg_text_size)))
                .textColor(Color.BLACK)
                .build();
    }*/
}




