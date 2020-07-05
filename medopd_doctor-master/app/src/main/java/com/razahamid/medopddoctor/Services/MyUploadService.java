package com.razahamid.medopddoctor.Services;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Home.MainActivity;
import com.razahamid.medopddoctor.Models.UploadFilesModel;
import com.razahamid.medopddoctor.Models.UploadFilesOrignal;
import com.razahamid.medopddoctor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Service to handle uploading files to Firebase Storage.
 */
public class MyUploadService extends MyBaseTaskService {

    private static final String TAG = "MyUploadService";
    private FirebaseRef ref=new FirebaseRef();
    /** Intent Actions **/
    public static final String ACTION_UPLOAD = "action_upload";
    public static final String UPLOAD_COMPLETED = "upload_completed";
    public static final String UPLOAD_ERROR = "upload_error";
    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";
    public static final String Add_New_File="Add_New_File";
    private StorageReference mStorageRef;
    private ArrayList<UploadFilesModel>  filesForUpload;

    @Override
    public void onCreate() {
        super.onCreate();

        // [START get_storage_ref]
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // [END get_storage_ref]
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:" + intent + ":" + startId);
        if (ACTION_UPLOAD.equals(intent.getAction())) {
            filesForUpload = (ArrayList<UploadFilesModel>) intent.getExtras().getSerializable(EXTRA_FILE_URI);
            assert filesForUpload != null;
            uploadFromUri();
        }else if (Add_New_File.equals(intent.getAction())){
            ArrayList<UploadFilesModel> newUpload=(ArrayList<UploadFilesModel>) intent.getExtras().getSerializable(EXTRA_FILE_URI);
            assert newUpload != null;
            filesForUpload.addAll(newUpload);

        }
        return START_REDELIVER_INTENT;
    }

    // [START upload_from_uri]
    private void uploadFromUri() {
        if (filesForUpload.size()>0){
         uploadFile(0);
        }
    }

    private void  uploadFile(final  int index){
        // [START_EXCLUDE]
        UploadFilesModel uploadFilesModel=filesForUpload.get(index);
        UploadFilesOrignal selectedFiles = null;

        try {
            JSONObject jsonObject=new JSONObject(uploadFilesModel.getData());
            selectedFiles=new UploadFilesOrignal(Uri.parse(jsonObject.getString(ref.ImageUrl)),jsonObject.getString(ref.ChatLink),jsonObject.getString(ref.User),jsonObject.getString(ref.Message),jsonObject.getString(ref.Receiver));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        taskStarted();
        showProgressNotification(getString(R.string.progress_uploading)+" " + String.valueOf(index+1)+"/"+String.valueOf(filesForUpload.size()), 0, 0);
        // [END_EXCLUDE]

        // [START get_child_ref]
        // Get a reference to store file at photos/<FILENAME>.jpg
        StorageReference photoRef=null;
        assert selectedFiles != null;
        photoRef=mStorageRef.child("ChatImage/"+selectedFiles.getLink()).child(selectedFiles.getUri().getLastPathSegment());
        final StorageReference finalPhotoRef = photoRef;
        final UploadFilesOrignal finalSelectedFiles = selectedFiles;
        photoRef.putFile(selectedFiles.getUri()).
                addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        showProgressNotification(getString(R.string.progress_uploading)+" " + String.valueOf(index+1)+"/"+String.valueOf(filesForUpload.size()),
                                taskSnapshot.getBytesTransferred(),
                                taskSnapshot.getTotalByteCount());
                    }
                })
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        // Forward any exceptions
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        Log.d(TAG, "uploadFromUri: upload success");

                        // Request the public download URL
                        return finalPhotoRef.getDownloadUrl();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri downloadUri) {
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(ref.ChatMessages+"/"+ finalSelectedFiles.getLink()+"/"+ref.Chat);
                        Map<String, Object> user = new HashMap<>();
                        user.put(ref.Sender, finalSelectedFiles.getUserUid());
                        user.put(ref.Message, finalSelectedFiles.getMessage());
                        user.put(ref.Receiver, finalSelectedFiles.getReceiver());
                        user.put(ref.TimeStamp, ServerValue.TIMESTAMP);
                        user.put(ref.MessageType,ref.Image);
                        user.put(ref.ImageUrl,downloadUri.toString());
                        databaseReference.push().setValue(user);
                        FirebaseDatabase.getInstance().getReference(ref.LastMessage+"/"+finalSelectedFiles.getUserUid()+"/"+finalSelectedFiles.getReceiver()).updateChildren(user);
                        FirebaseDatabase.getInstance().getReference(ref.LastMessage+"/"+finalSelectedFiles.getReceiver()+"/"+finalSelectedFiles.getUserUid()).updateChildren(user);
                        if (index== filesForUpload.size()-1){
                            broadcastUploadFinished(downloadUri, finalSelectedFiles.getUri());
                            showUploadFinishedNotification(downloadUri, finalSelectedFiles.getUri());
                            taskCompleted();
                            stopSelf();
                        }else {
                            uploadFile(index+1);
                        }
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        // [START_EXCLUDE]
                        broadcastUploadFinished(null, finalSelectedFiles.getUri());
                        showUploadFinishedNotification(null, finalSelectedFiles.getUri());
                        taskCompleted();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END upload_from_uri]

    /**
     * Broadcast finished upload (success or failure).
     * @return true if a running receiver received the broadcast.
     */
    private boolean broadcastUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        boolean success = downloadUrl != null;

        String action = success ? UPLOAD_COMPLETED : UPLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);
    }

    /**
     * Show a notification for a finished upload.
     */
    private void showUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        // Hide the progress notification
        dismissProgressNotification();
        boolean success = downloadUrl != null;
        Intent intent = new Intent(this, MainActivity.class)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        String caption = success ? getString(R.string.upload_success) : getString(R.string.upload_failure);
        showFinishedNotification(caption, intent, success);
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPLOAD_COMPLETED);
        filter.addAction(UPLOAD_ERROR);

        return filter;
    }
    public void addNewFile(UploadFilesModel uploadFilesModel){
        this.filesForUpload.add(uploadFilesModel);
    }
}