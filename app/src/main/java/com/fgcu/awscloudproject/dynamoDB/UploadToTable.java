package com.fgcu.awscloudproject.dynamoDB;

import android.support.v7.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.HandsomeNotHandsomeDO;

public class UploadToTable extends AppCompatActivity {

    private DynamoDBMapper dynamoDBMapper;
    private String photoName;
    private String dataModelResponse;

    public UploadToTable(DynamoDBMapper dynamoDBMapper, String photoName, String dataModelResponse) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.photoName = photoName;
        this.dataModelResponse = dataModelResponse;
    }

    public void addToTable() {
        final HandsomeNotHandsomeDO newPicture = new HandsomeNotHandsomeDO();

        if (dataModelResponse.equals("Handsome")) {
            newPicture.setImageName(photoName + "H");
        } else if (dataModelResponse.equals("Not So Handsome")) {
            newPicture.setImageName(photoName + "N");
        } else {
            newPicture.setImageName(photoName + "X");
        }

        newPicture.setDataModelResponse(dataModelResponse);

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(newPicture);
                // Item saved
            }
        }).start();
    }

}
