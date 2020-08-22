package com.bigocoding.secretalbum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Photo> _mygallery ;
    private GridView _gridview ;
    private GridviewAdapter gridviewAdapter ;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MyString", "Haideptrai");
        verifyStoragePermissions(this);
        initComponent() ;
        updateGridview() ;
    }



    private void initComponent() {
        _gridview = findViewById(R.id.gridview);

    }
    private void updateGridview() {
        (new AsyncTaskLoadImage(this)).execute();
    }
    private class AsyncTaskLoadImage extends AsyncTask<Void, Void, ArrayList<Photo>> {
        private ProgressDialog _dialog;

        public AsyncTaskLoadImage(Activity activity) {
            _dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            _dialog.setTitle("Loading");
            _dialog.show();
        }

        @Override
        protected ArrayList<Photo> doInBackground(Void... voids) {
            // https://developer.android.com/training/data-storage/shared/media
            ArrayList<Photo> results = new ArrayList<>();

            String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME
            };
            String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " ASC";

            try (Cursor cursor = getApplicationContext().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    //MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
            )) {
                // Cache column indices.
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);

                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    Photo item = new Photo(name,contentUri);
                    results.add(item);
                }
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<Photo> results) {
            _mygallery = (ArrayList<Photo>) results;
            gridviewAdapter = new GridviewAdapter(MainActivity.this,
                    R.layout.gridview_item, _mygallery);
            _gridview.setAdapter(gridviewAdapter);
            if (_dialog.isShowing()) {
                _dialog.dismiss();
            }
        }
    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed()
    {
        // super.onBackPressed(); // Comment this super call to avoid calling finish() or fragmentmanager's backstack pop operation.
    }



}