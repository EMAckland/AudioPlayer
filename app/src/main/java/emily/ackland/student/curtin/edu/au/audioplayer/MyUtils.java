package emily.ackland.student.curtin.edu.au.audioplayer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by Emily on 12/1/2017.
 */

public class MyUtils {
	public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
		Drawable drawable = ContextCompat.getDrawable(context, drawableId);

		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);

			return bitmap;
		} else {
			throw new IllegalArgumentException("unsupported drawable type");
		}
	}
	public static boolean havePermissions(Activity activity, Context ctx, String[] permissions) {
		boolean permission = false;
		if(ActivityCompat.checkSelfPermission(ctx,
						Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
						ActivityCompat.checkSelfPermission(ctx,
										Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
						ActivityCompat.checkSelfPermission(ctx,
										Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED)
		{
			permission = false;
			if (ActivityCompat.shouldShowRequestPermissionRationale(
							activity, Manifest.permission.READ_EXTERNAL_STORAGE))
			{
				ActivityCompat.requestPermissions(activity, permissions, 1);
			}
		}
		else
			permission = true;
		return permission;
	}
	public static void print(String msg) {
		Log.v("DEBUG", msg);
	}
	public static String timeInMilliSecToFormattedTimeMM_SS(int time) {
		String formattedTime;
		Log.d("time", String.valueOf(time));
		try {
			int seconds = time / 1000;
			int minutes = seconds / 60;
			seconds = seconds % 60;
			if (seconds < 10) {
				formattedTime = " " + String.valueOf(minutes) + ":0" + String.valueOf(seconds) + " ";
				Log.d("formattedTime", formattedTime);
			} else {
				formattedTime = " " + String.valueOf(minutes) + ":" + String.valueOf(seconds) + " ";
				Log.d("formattedTime", formattedTime);
			}
		} catch (NumberFormatException e) {
			formattedTime = "error";
		}
		return formattedTime;
	}
	public static Bitmap getAlbumart(Context context, Long album_id) {
		Bitmap albumArtBitMap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		try {
			final Uri sArtworkUri = Uri
							.parse("content://media/external/audio/albumart");
			Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
			ParcelFileDescriptor pfd = context.getContentResolver()
							.openFileDescriptor(uri, "r");
			if (pfd != null) {
				FileDescriptor fd = pfd.getFileDescriptor();
				albumArtBitMap = BitmapFactory.decodeFileDescriptor(fd, null,
								options);
				pfd = null;
				fd = null;
			}
		} catch (Error ee) {
		} catch (Exception e) {
		}
		return albumArtBitMap;
	}
}
