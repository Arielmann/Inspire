package inspire.ariel.inspire.common.treatslist;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Date;

import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import io.realm.RealmModel;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import weborb.service.ExcludeProperties;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"image"})//Throws warning if not put as string literal
@ToString
@RealmClass
@ExcludeProperties(propertyNames = {AppStrings.KEY_IS_PURCHASED, AppStrings.KEY_USER_PURCHASES, AppStrings.KEY_IMAGE, AppStrings.BACKENDLESS_TABLE_TREAT_COLUMN_IS_VISIBLE_TO_USER})
public class Treat implements Parcelable, RealmModel {

    @PrimaryKey @Nullable private String objectId;
    @NonNull private String ownerId;
    @NonNull private String text;
    private int textColor;
    private int textSize;
    @NonNull private String fontPath;
    @NonNull private String bgImageName;
    @Nullable private Date created;
    @Ignore private Drawable image;
    @Builder.Default private int purchasesLimit = AppNumbers.DEFAULT_PURCHASES_LIMIT;
    @Index @Builder.Default private int userPurchases = AppNumbers.DEFAULT_TREAT_TIMES_PURCHASED;
    @Builder.Default private int allPurchases = AppNumbers.DEFAULT_TREAT_TIMES_PURCHASED;
    @Getter @Index @Builder.Default private boolean purchaseable = true;
    @Index @Builder.Default private boolean visible = true;

    public Treat() {
    }

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
}




