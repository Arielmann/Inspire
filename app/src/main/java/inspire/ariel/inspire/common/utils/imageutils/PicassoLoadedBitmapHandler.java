package inspire.ariel.inspire.common.utils.imageutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import inspire.ariel.inspire.common.quoteslist.Quote;

abstract class PicassoLoadedBitmapHandler {

    /*
    * This abstract class is inherited by chat and profile
    * image picasso targets. it saves code copying and
    * allows its children to retry loading the image upon error
    * */

    private ImageLoader loader;
    private Quote quote;
    private Context context;
    private String url;
    private String senderName;

    //Load profile image
    PicassoLoadedBitmapHandler(Context context, ImageLoader interfaceHolder, Quote group, String url) {
        this.context = context;
        this.quote = group;
        this.loader = interfaceHolder;
        this.url = url;
    }

    //Load image for chat item
    PicassoLoadedBitmapHandler(Context context, ImageLoader interfaceHolder, String senderName, String url) {
        this.context = context;
        this.senderName = senderName;
        this.loader = interfaceHolder;
        this.url = url;
    }

    void handleBitmap(Bitmap bitmap) {
        String TAG = PicassoChatImageTarget.class.getName();
        Log.d(TAG, "Bitmap loaded in Picasso bitmap handler");
        int[] imageSizes = ImageUtils.chooseImageSizes(context, 2, 2);
        //TODO: remove fixed sizes when done debugging
        Bitmap finalBitmap = Bitmap.createScaledBitmap(bitmap, 100, 200, true);
        Log.d(TAG, "final image created");
        ImageLoader loader = this.loader;

        try {
            quote.setImage(finalBitmap);
            Log.d(TAG, quote.getMessage() + "'s downloaded image is set as her bitmap property");
        } catch (NullPointerException e) {
            Log.e(TAG, "quote is null, chat item is handled. no need to set profile image");
        }
    }

    ImageLoader getLoader() {
        return loader;
    }

    public Quote getQuote() {
        return quote;
    }

    public Context getContext() {
        return context;
    }

    String getUrl() {
        return url;
    }

    String getSenderName() {
        return senderName;
    }
}
