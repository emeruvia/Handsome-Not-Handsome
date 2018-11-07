package com.fgcu.awscloudproject.s3Storage;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fgcu.awscloudproject.R;
import com.fgcu.awscloudproject.rekognition.DetectLabels;

import java.io.File;

public class UploadToS3 extends AppCompatActivity {

  private String imageFileName;
  private String mCurrentPhotoPath;
  private Context context;
  private DynamoDBMapper dynamoDBMapper;
  private ProgressBar mProgressBar;

  public UploadToS3(String imageFileName, String mCurrentPhotoPath, Context context,
      DynamoDBMapper dynamoDBMapper, ProgressBar progressBar) {
    this.imageFileName = imageFileName;
    this.mCurrentPhotoPath = mCurrentPhotoPath;
    this.context = context;
    this.dynamoDBMapper = dynamoDBMapper;
    this.mProgressBar = progressBar;
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

    mProgressBar.setVisibility(View.VISIBLE);

    // Attach a listener to the observer to get state update and progress notifications
    uploadObserver.setTransferListener(new TransferListener() {

      @Override
      public void onStateChanged(int id, TransferState state) {
        if (TransferState.COMPLETED == state) {
          // Handle a completed upload.
          Log.d("S3Upload", "Image has been uploaded successfully");
          //Triggers the rekognition to get the labels
          DetectLabels rekognitionLabels =
              new DetectLabels(imageFileName, context, dynamoDBMapper, mCurrentPhotoPath, mProgressBar);
          rekognitionLabels.runAsyncTask();
        }
      }

      @Override
      public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
        int percentDone = (int) percentDonef;

        Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
            + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
      }

      @Override
      public void onError(int id, Exception ex) {
        // Handle errors
        Log.d("S3UploadError", ex.toString());
        Toast.makeText(getApplicationContext(), "Error Uploading to S3", Toast.LENGTH_LONG).show();
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
