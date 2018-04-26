package com.fgcu.awscloudproject.dynamoDB;

import android.support.v7.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

public class UploadToTable extends AppCompatActivity{

    private DynamoDBMapper dynamoDBMapper;

    public UploadToTable(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public void addToTable() {

    }

}
