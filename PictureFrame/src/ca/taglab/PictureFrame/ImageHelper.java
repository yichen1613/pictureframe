package ca.taglab.PictureFrame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.*;

public class ImageHelper {

    private static final int IMAGE_MAX_SIZE = 80;

    /**
     * Write the bitmap in the application directory and store the file path.
     *
     * @param bitmap
     *            - Bitmap to save to the application directory.
     */
    public static String setImage(Bitmap bitmap, String name) {
        if (bitmap == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
        byte[] bitmapData = baos.toByteArray();

        // Create new file
        String filename = name + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(),
                filename);

        if (photo.exists())
            photo.delete();

        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());
            fos.write(bitmapData);
            fos.close();
            return filename;
        } catch (java.io.IOException e) {
            Log.e("ImageHelper", "Exception in photoCallback", e);
            return null;
        }
    }

    /**
     * Return the saved image for this word.
     *
     * @return Image Bitmap for this word. Null if it does not exist.
     */
    public static Bitmap getImage(String filename) {
        File f = new File(Environment.getExternalStorageDirectory(), filename);
        Bitmap b = null;

        try {
            FileInputStream fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (IOException e) {
            Log.e("ImageHelper", "Image: " + filename + " does not exist");
        }
        return b;
    }
}
