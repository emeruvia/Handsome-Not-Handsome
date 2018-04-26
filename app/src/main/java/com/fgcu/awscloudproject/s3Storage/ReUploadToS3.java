package com.fgcu.awscloudproject.s3Storage;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fgcu.awscloudproject.rekognition.DetectLabels;

import java.io.File;

public class ReUploadToS3 extends AppCompatActivity {

    private String imageFileName;
    private String mCurrentPhotoPath;
    private Context context;
    private DynamoDBMapper dynamoDBMapper;

    public ReUploadToS3(String imageFileName, String mCurrentPhotoPath, Context context, DynamoDBMapper dynamoDBMapper) {
        this.imageFileName = imageFileName;
        this.mCurrentPhotoPath = mCurrentPhotoPath;
        this.context = context;
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
    public String getImageFileName() {
        return imageFileName;
    }

    public void uploadWithTransferUtility() {

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(context)
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        TransferObserver uploadObserver =
                transferUtility.upload(
                        imageFileName,
                        new File(mCurrentPhotoPath));

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                    Log.d("S3ReUpload", "Image has been uploaded successfully");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("S3ReUpload", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                Log.d("S3ReUploadError", ex.toString());
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            // Handle a completed upload.
            Log.d("S3Completed", "YEAY");
        }

        Log.d("YourActivity", "Bytes Transferrred: " + uploadObserver.getBytesTransferred());
        Log.d("YourActivity", "Bytes Total: " + uploadObserver.getBytesTotal());
    }

}
