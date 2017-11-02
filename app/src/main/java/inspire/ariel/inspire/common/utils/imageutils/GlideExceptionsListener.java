package inspire.ariel.inspire.common.utils.imageutils;

import android.util.Log;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class GlideExceptionsListener implements RequestListener {

    private Object interfacedHolder;
    private String GLIDE_EXCEPTIONS_TAG = "Glide exceptions";

    public GlideExceptionsListener(Object interfacedHolder) {
        this.interfacedHolder = interfacedHolder;
    }

    @Override
    public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
        Log.d(GLIDE_EXCEPTIONS_TAG, e.toString());
        OnImageLoadingError error = (OnImageLoadingError) interfacedHolder;
        error.onImageLoadingError();
        return false;
    }

    @Override
    public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
        return false;
    }
}
