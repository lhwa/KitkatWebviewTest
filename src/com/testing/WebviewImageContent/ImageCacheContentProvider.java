package com.testing.WebviewImageContent;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.*;

public class ImageCacheContentProvider extends ContentProvider {

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) {
        Log.d("ImageCacheContentProvider", "fetching image " + uri.toString());

        String filePath = uri.getPath().substring(1);
        ParcelFileDescriptor parcel = null;
        try {
            File imgFile = getCachedImageFile(filePath);
            parcel = ParcelFileDescriptor.open(imgFile, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException ex) {
            Log.e("ImageCacheContentProvider", "failed to fetch " + uri.toString(), ex);
        } catch (IOException ex) {
            Log.e("ImageCacheContentProvider", "failed to open file " + filePath, ex);
        }

        return  parcel;
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) {
        ParcelFileDescriptor fd = openFile(uri, mode);
        return (fd != null) ? new AssetFileDescriptor(fd, 0, AssetFileDescriptor.UNKNOWN_LENGTH) : null;
    }

    @Override
    public AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts) {
        return openAssetFile(uri, "r");
    }

    @Override
    public final boolean onCreate() {
        return true;
    }

    @Override
    public int delete(Uri uri, String s, String[] as) {
        throw new UnsupportedOperationException("Not supported by this provider");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentvalues) {
        throw new UnsupportedOperationException("Not supported by this provider");
    }

    @Override
    public Cursor query(Uri uri, String[] as, String s, String[] as1, String s1) {
        throw new UnsupportedOperationException("Not supported by this provider");
    }

    @Override
    public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
        throw new UnsupportedOperationException("Not supported by this provider");
    }

    private File getCachedImageFile(String filename) throws IOException {
        File cacheFile = new File(getContext().getCacheDir(), filename);
        if (!cacheFile.exists()) {
            InputStream ins = null;
            FileOutputStream outs = null;
            try {
                ins = getContext().getAssets().open(filename);
                outs = new FileOutputStream(cacheFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = ins.read(buf)) > 0) {
                    outs.write(buf, 0, len);
                }
            } catch (IOException ex) {
                throw new IOException("Unable to open image file " + filename);
            } finally {
                if (outs != null) {
                    outs.close();
                    outs = null;
                }
                if (ins != null) {
                    ins.close();
                    ins = null;
                }
            }
        }

        return cacheFile;
    }
}
