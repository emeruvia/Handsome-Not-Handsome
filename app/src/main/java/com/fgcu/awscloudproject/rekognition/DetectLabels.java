package com.fgcu.awscloudproject.rekognition;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Emotion;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.fgcu.awscloudproject.AWSConstants;

import java.util.List;

import java.util.List;

/**
 * Created by emeruvia on 4/23/2018.
 */
public class DetectLabels extends AppCompatActivity {

    private String photoName;
    private String bucketName = "aws-cloud-project";
    private Context context;
    private AWSCredentials credentials;
    private AmazonRekognitionClient amazonRekognitionClient;
    private AWSConstants constants = new AWSConstants();

    public DetectLabels(String photoName, Context context) {
        this.photoName = photoName;
        this.context = context;
    }

    private void init() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                constants.identityPool(),
                constants.awsRegion() // Region
        );
        amazonRekognitionClient = new AmazonRekognitionClient(credentialsProvider);

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(photoName).withBucket(bucketName)));

        DetectFacesRequest facesRequest = new DetectFacesRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(photoName).withBucket(bucketName)));
        
        try {
            DetectLabelsResult result = amazonRekognitionClient.detectLabels(request);
            List<Label> labels = result.getLabels();

            DetectFacesResult facesResult = amazonRekognitionClient.detectFaces(facesRequest);
            List<FaceDetail> faceLabels = facesResult.getFaceDetails();

            System.out.println("Detected labels for " + photoName);
            for (Label label : labels) {
                System.out.println(label.getName() + ": " + label.getConfidence().toString());
            }
            System.out.println("Detected Faces Labels for " + photoName);
            for (FaceDetail faceDetail : faceLabels) {
                System.out.print(faceDetail.getAgeRange() + " ");
                System.out.print(faceDetail.getBeard() + " ");
                System.out.print(faceDetail.getConfidence() + " ");
                System.out.print(faceDetail.getEmotions() + " ");
                System.out.print(faceDetail.getGender() + "\n");
            }
        } catch (AmazonClientException e) {
            e.printStackTrace();
        }
    }

    public void runAsyncTask() {
        new AsyncTaskRunner().execute();
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            init();
            return null;
        }
    }

}

