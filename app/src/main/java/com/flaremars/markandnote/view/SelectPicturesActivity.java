package com.flaremars.markandnote.view;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flaremars.markandnote.R;
import com.flaremars.markandnote.adapter.SelectedPictureAdapter;
import com.flaremars.markandnote.bean.SelectedPictureItem;
import com.flaremars.markandnote.util.DisplayUtils;
import com.flaremars.markandnote.util.FileUtils;
import com.flaremars.markandnote.util.SecureUtils;
import com.flaremars.markandnote.util.StringUtils;
import com.flaremars.markandnote.widget.GridSpacingItemDecoration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 本地图片选择Activity
 * 数据返回格式：图片路径数组，以 ‘，’ 隔开
 */
public class SelectPicturesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "SelectPicturesActivity";

    private static final int REQUEST_PERMISSION_EXTERNAL_STORAGE = 1;

    public static final String ARG_PICTURE_SIZE_LIMIT = "pictureSizeLimit";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);

    private static final int ACTION_TAKE_PHOTO = 1;

    private String newPicturePath;
    private int pictureSize;

    private static final String[] STORE_IMAGES = {
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA
    };

    private Button takePhotoBtn;
    private RecyclerView picturesRecyclerView;
    private SelectedPictureAdapter adapter;

    private List<SelectedPictureItem> pictureItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pictures);

        int pictureSizeLimit = getIntent().getIntExtra(ARG_PICTURE_SIZE_LIMIT, 8);

        pictureSize = (DisplayUtils.INSTANCE.getSystemInfo(this).getScreenWidth() - DisplayUtils.INSTANCE.dp2px(this, 32.0f)) / 3;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white);
        }

        takePhotoBtn = (Button) findViewById(R.id.btn_take_photo);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File picturesDir = FileUtils.INSTANCE.getPhotosDirectory(SelectPicturesActivity.this);
                newPicturePath = picturesDir.getPath() + File.separator + DATE_FORMAT.format(new Date())+".jpg";
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(newPicturePath)));
                startActivityForResult(intent, ACTION_TAKE_PHOTO);
            }
        });

        pictureItemList = new ArrayList<>();
        adapter = new SelectedPictureAdapter(this, pictureItemList, pictureSizeLimit);
        picturesRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_pictures);
        picturesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        picturesRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, DisplayUtils.INSTANCE.dp2px(this,8.0f), true));
        picturesRecyclerView.setAdapter(adapter);

        boolean canLoadImages = true;
        if (SecureUtils.INSTANCE.getSDKVersionNumber() >= SecureUtils.SDK_INT_M &&
                !SecureUtils.INSTANCE.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            takePhotoBtn.setEnabled(false);
            takePhotoBtn.setText("无操作外部储存权限");
        }
        if (SecureUtils.INSTANCE.getSDKVersionNumber() >= SecureUtils.SDK_INT_M &&
                !SecureUtils.INSTANCE.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (SecureUtils.INSTANCE.shouldShowRequestPermissionDialog(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new MaterialDialog.Builder(this)
                        .title("请求授权")
                        .content("读取相册以及拍照需要使用sdCard授权，请允许")
                        .positiveText("授权")
                        .positiveColor(Color.GREEN)
                        .negativeText("残忍拒绝")
                        .negativeColor(Color.GRAY)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SecureUtils.INSTANCE.requestExternalStoragePermissions(SelectPicturesActivity.this, REQUEST_PERMISSION_EXTERNAL_STORAGE);
                            }
                        })
                        .show();
            } else {
                SecureUtils.INSTANCE.requestExternalStoragePermissions(this, REQUEST_PERMISSION_EXTERNAL_STORAGE);
            }
            canLoadImages = false;
        }
        if (canLoadImages) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_TAKE_PHOTO) {
                try {
                    pictureItemList.add(0, new SelectedPictureItem(writeThumbFile(newPicturePath), newPicturePath));
                    adapter.notifyItemInserted(0);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "拍照失败 : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSupportLoaderManager().initLoader(0, null, this);
                }
                if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takePhotoBtn.setEnabled(true);
                    takePhotoBtn.setText("拍照");
                }
                return;
            default:
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //保存缩略图
    private String writeThumbFile(String data) throws IOException {
        Bitmap bmp = getImageThumbnail(data, pictureSize, pictureSize);
        if (bmp == null) {
            throw new IOException("缩略图获取失败");
        }
        File thumbsDir = FileUtils.INSTANCE.getThumbPhotosDirectory(SelectPicturesActivity.this);
        String thumbPath = thumbsDir.getPath() + File.separator + DATE_FORMAT.format(new Date())+".jpg";
        FileOutputStream fileOut = new FileOutputStream(new File(thumbPath));
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);
        return thumbPath;
    }

    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        if (bitmap == null) {
            Log.e(TAG, "bitmap == null");
            return null;
        }
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height);
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_pictures_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                this.finish();
                break;
            case R.id.action_complete:
                String resultData = getSelectedPictures();
                String resultOriginalData = getSelectedOriginalPictures();
                if (StringUtils.INSTANCE.isEmpty(resultData)) {
                    setResult(Activity.RESULT_CANCELED);
                } else {
                    Intent data = new Intent();
                    data.putExtra("data", resultData);
                    data.putExtra("originalData", resultOriginalData);
                    setResult(RESULT_OK,data);
                }
                this.finish();
                break;
            default:
        }
        return true;
    }

    private String getSelectedPictures() {
        StringBuilder sb = new StringBuilder();
        for (SelectedPictureItem item : pictureItemList) {
            if (item.isSelected()) {
                sb.append(item.getPath());
                sb.append(",");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private String getSelectedOriginalPictures() {
        StringBuilder sb = new StringBuilder();
        for (SelectedPictureItem item : pictureItemList) {
            if (item.isSelected()) {
                sb.append(item.getOriginalPath());
                sb.append(",");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private String getOriginalPath(int id) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                appendPath(Long.toString(id)).build();
        return FileUtils.INSTANCE.getPath(this, uri);
    }

    private String getThumbPath(int id) {
        ContentResolver resolver = getContentResolver();
        String[] projection = {MediaStore.Images.Thumbnails.DATA };
        Cursor cursor = resolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
                MediaStore.Images.Thumbnails.IMAGE_ID + " == " + id, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String path = cursor.getString(0);
            cursor.close();
            return path;
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES,
                MediaStore.Images.ImageColumns.MIME_TYPE + " != ?", new String[]{"image/webp"}, MediaStore.Images.Media.DATE_MODIFIED + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            for(int curSize = 0; curSize < 200 && !data.isClosed() && data.moveToNext(); curSize++) {
                int id = data.getInt(0);
                String originalPath = data.getString(1);
                String thumbPath = getThumbPath(id);
                if (thumbPath != null) {
                    pictureItemList.add(new SelectedPictureItem(thumbPath, originalPath));
                }
            }

            adapter.notifyDataSetChanged();
            data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
