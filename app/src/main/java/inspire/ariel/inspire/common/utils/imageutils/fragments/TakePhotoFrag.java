package inspire.ariel.inspire.common.utils.imageutils.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.io.Serializable;

import inspire.ariel.inspire.common.utils.imageutils.ImageUtils;
import inspire.ariel.inspire.common.utils.sharedprefutils.SharedPrefManager;

public class TakePhotoFrag extends Fragment implements Serializable {
    private static final int CAM_REQUEST = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivityForResult(takePictureIntent, CAM_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //****The actual camera inbuilt application opens here****///
        try {
            //String senderName = SharedPrefManager.getInstance(getContext()).getUserName();
            if (requestCode == CAM_REQUEST && resultCode == Activity.RESULT_OK) {
                Uri selectedImageUri = data.getData();
                int[] imageSizes = ImageUtils.chooseImageSizes(getActivity(), 2, 2);
                ImageUtils.createBitmapFromImageSource("Method is mocked. Check Take Photo Frag for this error", getActivity(), selectedImageUri, imageSizes[0], imageSizes[1]);
                //Adding Image to array method occurs on parent class

            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
}








