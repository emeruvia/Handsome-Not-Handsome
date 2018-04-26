package com.fgcu.awscloudproject.rekognition;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.fgcu.awscloudproject.AWSConstants;
import com.fgcu.awscloudproject.dataObject.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by emeruvia on 4/23/2018.
 */
public class DetectLabels extends AppCompatActivity {

    List<Label> labels;
    private String photoName;
    private String bucketName = "aws-cloud-project";
    private Context context;
    private AWSConstants constants = new AWSConstants();
    private String dataModelResponse;
        private DynamoDBMapper dynamoDBMapper;

    public DetectLabels(String photoName, Context context, DynamoDBMapper dynamoDBMapper) {
        this.photoName = photoName;
        this.context = context;
        this.dynamoDBMapper = dynamoDBMapper;
    }

    private void init() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                constants.identityPool(),
                constants.awsRegion() // Region
        );
        AmazonRekognitionClient amazonRekognitionClient = new AmazonRekognitionClient(credentialsProvider);

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(photoName)
                                .withBucket(bucketName)));

        DetectFacesRequest facesRequest = new DetectFacesRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(photoName).withBucket(bucketName)));

        UserData userData = new UserData();
        try {
            DetectLabelsResult result = amazonRekognitionClient.detectLabels(request);
            labels = result.getLabels();

            DetectFacesResult facesResult = amazonRekognitionClient.detectFaces(facesRequest);
            StringBuilder stringBuilder = new StringBuilder();
            System.out.println("Detected labels for " + photoName);

            System.out.println(result);
            System.out.println(facesResult.getFaceDetails());
            userData.setUserLabels(stringBuilder);


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

        @Override
        protected void onPostExecute(Void aVoid) {
            Label suit = null;
            Label tuxedo = null;
            Label smile = null;
            Label human = null;
            Label dressShirt = null;
            Label glasses = null;

            for (Label label : labels) {
                if (label.getName().equals("Human")) {
                    human = label;
                }
                if (label.getName().equals("Suit")) {
                    suit = label;
                }
                if (label.getName().equals("Tuxedo")) {
                    tuxedo = label;
                }
                if (label.getName().equals("Dimples")) {
                    smile = label;
                }
                if(label.getName().equals("Dress Shirt")){
                    dressShirt = label;
                }
                if (label.getName().equals("Glasses")){

                }

            }
            
            if(human!= null && human.getConfidence() > 30){
                if((tuxedo!=null) && (tuxedo.getConfidence() > 30)){
                    Toast.makeText(context,"Handsome just because of the tux", Toast.LENGTH_LONG).show();
                } else if((suit!=null) && (suit.getConfidence() > 30)){
                    Toast.makeText(context,"Handsome just because of the suit", Toast.LENGTH_LONG).show();
                } else if((dressShirt!=null) && (dressShirt.getConfidence() > 30)){
                    Toast.makeText(context,"Handsome just cause of dress shirt", Toast.LENGTH_LONG).show();
                } else if((smile!=null) && (smile.getConfidence() > 30)){
                    Toast.makeText(context,"Handsome because you smiling", Toast.LENGTH_LONG).show();
                }else if((glasses!=null) && (glasses.getConfidence() > 30)){
                    Toast.makeText(context,"Handsome because of the glasses", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context,"Not so handsome", Toast.LENGTH_LONG).show();
                }
            } else{
                Toast.makeText(context,"No person detected", Toast.LENGTH_LONG).show();

            }
            super.onPostExecute(aVoid);
        }
    }
}

