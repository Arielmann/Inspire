package inspire.ariel.inspire.common;

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
import lombok.NonNull;
import weborb.service.ExcludeProperties;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"image", "userPurchases"}) //Throws warning if not put as string literal
@RealmClass
@ExcludeProperties(propertyNames = {AppStrings.KEY_IS_PURCHASED, AppStrings.KEY_USER_PURCHASES, AppStrings.KEY_IMAGE, AppStrings.BACKENDLESS_TABLE_TREAT_COLUMN_IS_VISIBLE_TO_USER})
public class Treat implements Parcelable, RealmModel {

    @PrimaryKey
    @Nullable
    private String objectId;
    @NonNull
    private String ownerId;
    @NonNull
    private String text;
    private int textColor;
    private int textSize;
    @NonNull
    private String fontPath;
    @NonNull
    private String bgImageName;
    @Nullable
    private Date created;
    @Ignore
    private Drawable image;
    @Builder.Default
    private int purchasesLimit = AppNumbers.DEFAULT_PURCHASES_LIMIT;
    @Index
    @Builder.Default
    private int userPurchases = AppNumbers.DEFAULT_TREAT_USER_PURCHASED;
    @Builder.Default
    private int allPurchases = AppNumbers.DEFAULT_TREAT_USER_PURCHASED;
    @Index
    @Builder.Default
    private boolean visible = true;

    public Treat(){}

    protected Treat(Parcel in) {
        objectId = in.readString();
        ownerId = in.readString();
        text = in.readString();
        textColor = in.readInt();
        textSize = in.readInt();
        fontPath = in.readString();
        bgImageName = in.readString();
        purchasesLimit = in.readInt();
        userPurchases = in.readInt();
        allPurchases = in.readInt();
        visible = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectId);
        dest.writeString(ownerId);
        dest.writeString(text);
        dest.writeInt(textColor);
        dest.writeInt(textSize);
        dest.writeString(fontPath);
        dest.writeString(bgImageName);
        dest.writeInt(purchasesLimit);
        dest.writeInt(userPurchases);
        dest.writeInt(allPurchases);
        dest.writeByte((byte) (visible ? 1 : 0));
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




