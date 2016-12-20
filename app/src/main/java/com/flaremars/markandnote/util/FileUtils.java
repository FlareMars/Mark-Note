package com.flaremars.markandnote.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Url;

/**
 * Created by FlareMars on 2016/11/5.
 */
public enum  FileUtils {
    INSTANCE;

    public static final String ROOT_DIR = "MarkAndNote";

    /**
     * 获取本项目的图片文件夹
     */
    public File getPhotosDirectory(Context context) {
        return getDirectory(context, ROOT_DIR + File.separator + "Pictures", false);
    }

    /**
     * 获取本项目的图片缩略图文件夹
     */
    public File getThumbPhotosDirectory(Context context) {
        return getDirectory(context, ROOT_DIR + File.separator + "Thumbs", false);
    }

    /**
     * 获取指定的目录
     */
    public File getDirectory(Context context,String dir,boolean isPrivate) {
        File rootDir;
        if (isPrivate) {
            rootDir = getAppDir(context);
        } else {
            rootDir = getStorageDir();
        }

        File targetDir = new File(rootDir,dir);
        if (targetDir.exists()) {
            return targetDir;
        } else {
            if (targetDir.mkdirs()) {
                return targetDir;
            } else {
                return null;
            }
        }
    }

    /**
     * 获取app私有目录
     */
    public File getAppDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * 获取储存目录
     */
    public File getStorageDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return getExternalStorageDir();
        }
        return getRootDir();
    }

    /**
     * 获取手机外部存储目录
     */
    private File getExternalStorageDir() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 获取手机根目录
     */
    private File getRootDir() {
        return Environment.getRootDirectory();
    }

    /**
     * 获取文件的后缀名
     * @param file 待获取文件对象
     * @return 文件后缀名或空字串
     */
    public String getFileExtension(File file) {
        if (Check.isNull(file)) {
            return "";
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        } else {
            return fileName.substring(index, fileName.length())
                    .toLowerCase();
        }
    }

    /**
     * 文件写入
     * @param data 待写入数据流
     * @param target 目标文件位置
     * @return 是否写入成功
     */
    public boolean writeFile(byte[] data, File target) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        boolean success = true;
        try {
            fos = new FileOutputStream(target);
            bos = new BufferedOutputStream(fos);
            bos.write(data);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return success;
    }

    /**
     * 私有空间下的文件写入
     * @param context 程序上下文
     * @param fileName 文件名
     * @param data 待写入数据
     */
    public void writeFile(Context context, String fileName, byte[] data) {

        try {
            FileOutputStream fos = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            fos.write(data);

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> readFileToStringArray(File file) throws IOException {
        InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
        BufferedReader bufReader = new BufferedReader(inputReader);
        List<String> contentArray = new ArrayList<>();
        String line;
        while ((line = bufReader.readLine()) != null) {
            contentArray.add(line + "\n");
        }
        bufReader.close();
        return contentArray;
    }

    public String readFileToString(File file) throws IOException {
        return readFileToString(new FileInputStream(file));
    }

    public String readFileToString(InputStream inputStream) throws IOException {
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader bufReader = new BufferedReader(inputReader);
        String line = "";
        String result = "";
        while((line = bufReader.readLine()) != null) {
            result += line + "\n";
        }
        return result;
    }

    public void writeFile(File target, List<String> contentArray) throws IOException {
        OutputStreamWriter streamWriter = new OutputStreamWriter(new FileOutputStream(target));
        BufferedWriter bufWriter = new BufferedWriter(streamWriter);
        String line;
        for (int i = 0, size = contentArray.size();i < size;i++) {
            line = contentArray.get(i);
            bufWriter.write(line, 0, line.length());
        }
        bufWriter.close();
    }

    /**
     * 文件读取
     * @param fileName 待读取文件详细路径
     * @param context 程序上下文
     * @return 文件数据流
     * @throws IOException
     */
    public byte[] readFile(String fileName,Context context) throws IOException {
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(buffer) != -1) {
                arrayOutputStream.write(buffer, 0, buffer.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            return arrayOutputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 从asset文件夹中读取文件
     * @param context 程序上下文
     * @param assetName 资源名字
     * @return 资源数据流
     */
    public byte[] readFromAsset(Context context,String assetName) {
        try {
            InputStream inputStream = context.getAssets().open(assetName);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(buffer) != -1) {
                arrayOutputStream.write(buffer, 0, buffer.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 从网络获取文件数据
     * @param fileUrl 网络文件完整url
     * @return 文件数据流
     * @throws IOException
     */
    public byte[] readFromNetwork(String fileUrl) throws IOException {
        Retrofit retrofit = new Retrofit.Builder().build();
        DownloadFileService downloadFileService = retrofit.create(DownloadFileService.class);
        Call<ResponseBody> call = downloadFileService.downloadFileWithUrl(fileUrl);
        Response<ResponseBody> responseBody = call.execute();
        if (responseBody.isSuccessful()) {
            return responseBody.body().bytes();
        } else {
            return new byte[0];
        }
    }

    public interface DownloadFileService {
        Call<ResponseBody> downloadFileWithUrl(@Url String fileUrl);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
