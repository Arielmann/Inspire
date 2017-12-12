package inspire.ariel.inspire.common.treatslist;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(exclude={"image"})
public class Treat implements Parcelable {

    @NonNull private String text;
    @NonNull private String ownerId;
    private int textColor;
    private int textSize;
    @NonNull private String fontPath;
    @NonNull private String bgImageName;

    private String objectId;
    private Date created;
    private Drawable image;

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




