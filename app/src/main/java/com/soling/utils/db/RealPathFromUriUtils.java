package com.soling.utils.db;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class RealPathFromUriUtils {
    /*
    根据Uri获取图片的绝对路径
     */
    public static String getRealPathFromUri(Context context,Uri uri){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >=19){
            return getRealPathFromUriAboveApi19(context,uri);
        }else{
            return getRealPathFromUriBelowApi19(context,uri);
        }
    }

    /*
   适配api19以下（不包括api19）,根据uri获取图片的绝对路径
    */
    private static String getRealPathFromUriBelowApi19(Context context, Uri uri) {
        return getDataColum(context,uri,null,null);
    }

    /*
    适配api19及以上（不包括api19）,根据uri获取图片的绝对路径
     */

    public static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if(DocumentsContract.isDocumentUri(context,uri)){
            String documentId = DocumentsContract.getDocumentId(uri);
            if(isMediaDocument(uri)){
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID +"=?";
                String[] selectionArgs = {id};
                filePath = getDataColum(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection,selectionArgs);

            }else if(isDownloadDocument(uri)){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(documentId));
                filePath = getDataColum(context,contentUri,null,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            filePath = getDataColum(context,uri,null,null);
        }else if("file".equals(uri.getScheme())){
            filePath = uri.getPath();
        }
        return filePath;
    }

    private static boolean isDownloadDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColum(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri,projection,selection,selectionArgs,null);
            if(cursor != null&& cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        }catch(Exception e){
            e.printStackTrace();
            cursor.close();
        }

        return path;
    }
}
