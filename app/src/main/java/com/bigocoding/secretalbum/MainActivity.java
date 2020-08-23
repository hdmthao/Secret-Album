package com.bigocoding.secretalbum;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.GridView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BiometricAssistant mBiometricAssistant;
    private final String phrase = "Never forget tomorrow is a new day";
    private final String userId = "usr_5abcb05344d141e398f1489b159d5ae5";
    private final String contentLanguage = "en-US";
    private ArrayList<Photo> _mygallery;
    private GridView _gridview;
    private GridviewAdapter gridviewAdapter;
    private static final int RESULT_CODE_PICKER_IMAGES = 9000;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    ArrayList<Upload> mUploads;
    /**
     * Checks if the app has permission to write to device storage
     * <p>
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

        initComponent();
        populateGridView();

//        mBiometricAssistant = new BiometricAssistant("key_186c7a4a0d0f42e3a6adc06e61339d09", "tok_a4d34a9a28f44841bf083dfd6acbc2a0");
    }

    private void initComponent() {
        mUploads = new ArrayList<>();
        mStorageRef = FirebaseStorage.getInstance().getReference( userId + "/uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(userId + "/uploads");
        _gridview = findViewById(R.id.gridview);
    }

    private void populateGridView() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                gridviewAdapter = new GridviewAdapter(MainActivity.this, R.layout.gridview_item, mUploads);
                _gridview.setAdapter(gridviewAdapter);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addphoto:
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(MainActivity.this, "Upload in progess", Toast.LENGTH_SHORT).show();
                } else {
                    openFileChooser();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<Photo> result;
        result = new ArrayList<>();
        try {
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && data != null) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {
                    Uri mImageUri = data.getData();
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();
                    String name = mImageUri.toString().substring(mImageUri.toString().length() - 4, mImageUri.toString().length() - 1);
                    result.add(new Photo(name, mImageUri));
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }

                        for (Uri uri : mArrayUri) {
                            String name = System.currentTimeMillis() + "." + getFileExtension(uri);
                            result.add(new Photo(name, uri));
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
        uploadImageToFirebase(result);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImageToFirebase(ArrayList<Photo> result) {
        for (final Photo photo : result) {
            final String name = System.currentTimeMillis() + '.' + getFileExtension(photo.get_uri());
            Log.d(TAG, "Uploading " + name);
            StorageReference fileReference = mStorageRef.child(name);
            mUploadTask = fileReference.putFile(photo.get_uri())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "Success");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, 1000);
//                            Toast.makeText(MainActivity.this, "Upload complete", Toast.LENGTH_LONG).show();
                            final String uploadId = mDatabaseRef.push().getKey();
                            final Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String photoLink = uri.toString();
                                    Upload upload = new Upload(name, photoLink);
                                    Log.d(TAG, photoLink);
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).
                    addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); // Comment this super call to avoid calling finish() or fragmentmanager's backstack pop operation.
    }

    public void encapsulatedVoiceEnrollment(View view) {
        mBiometricAssistant.encapsulatedVoiceEnrollment(this, userId, contentLanguage, phrase, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedVoiceEnrollment onSuccess Result : " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                checkResponse(errorResponse);
                System.out.println("encapsulatedVoiceEnrollment onFailure Result : " + errorResponse.toString());
            }
        });
    }

    public void checkResponse(JSONObject response) {
        try {
            if (response.getString("responseCode").equals("IFVD")
                    || response.getString("responseCode").equals("ACLR")
                    || response.getString("responseCode").equals("IFAD")
                    || response.getString("responseCode").equals("SRNR")
                    || response.getString("responseCode").equals("UNFD")
                    || response.getString("responseCode").equals("MISP")
                    || response.getString("responseCode").equals("DAID")
                    || response.getString("responseCode").equals("UNAC")
                    || response.getString("responseCode").equals("CLNE")
                    || response.getString("responseCode").equals("INCP")
                    || response.getString("responseCode").equals("NPFC")) {
//                Toast.makeText(this, "responseCode: " + response.getString("responseCode")
//                        + ", " + getString(com.voiceit.voiceit2.R.string.CHECK_CODE), Toast.LENGTH_LONG).show();
                Log.e("MainActivity", "responseCode: " + response.getString("responseCode")
                        + ", " + getString(com.bigocoding.secretalbum.R.string.CHECK_CODE));
            }
        } catch (JSONException e) {
            Log.d("MainActivity", "JSON exception : " + e.toString());
        }
    }
}