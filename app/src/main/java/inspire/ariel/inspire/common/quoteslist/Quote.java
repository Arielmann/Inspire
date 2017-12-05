package inspire.ariel.inspire.common.quoteslist;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Lombok;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(exclude={"image"})
public class Quote implements Parcelable {

    @NonNull private String text;
    @NonNull private String leaderId;
    @NonNull private int textColor;
    @NonNull private int textSize;
    @NonNull private String fontPath;
    @NonNull private String bgImageName;

    private String objectId;
    private Date created;
    private Drawable image;

    protected Quote(Parcel in) {
        text = in.readString();
        leaderId = in.readString();
        textColor = in.readInt();
        textSize = in.readInt();
        fontPath = in.readString();
        bgImageName = in.readString();
        objectId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(leaderId);
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

    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };

    /*public static Quote newNoQuotesToPresentQuote(Resources res){
        return Quote.builder().text(res.getString(R.string.error_no_quotes))
                .fontPath(FontsManager.Font.ALEF_BOLD.getPath())
                .bgImageName(AppStrings.BLUE_YELLOW_BG)
                .leaderId(AppStrings.VAL_LEADER_OBJECT_ID)
                .textSize(Math.round(res.getDimension(R.dimen.error_msg_text_size)))
                .textColor(Color.BLACK)
                .build();
    }*/
}




