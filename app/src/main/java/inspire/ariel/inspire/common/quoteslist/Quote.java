package inspire.ariel.inspire.common.quoteslist;

import android.graphics.Bitmap;

import inspire.ariel.inspire.common.utils.imageutils.ImageUtils;
import inspire.ariel.inspire.common.utils.listutils.ListPresentable;
import inspire.ariel.inspire.leader.Leader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data @NoArgsConstructor @AllArgsConstructor
public class Quote implements ListPresentable{

    @NonNull private String objectId;
    @NonNull private String message;
    private Bitmap image;
    //private String imageLocalPath;
    //String imageUrl;
}




