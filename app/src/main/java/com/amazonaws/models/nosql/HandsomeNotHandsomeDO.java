package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "awscloudproject-mobilehub-1664066856-Handsome-not-Handsome")

public class HandsomeNotHandsomeDO {
    private String _imageName;
    private String _dataModelResponse;

    @DynamoDBHashKey(attributeName = "image_name")
    @DynamoDBIndexHashKey(attributeName = "image_name", globalSecondaryIndexName = "handsome_app_index")
    public String getImageName() {
        return _imageName;
    }

    public void setImageName(final String _imageName) {
        this._imageName = _imageName;
    }
    @DynamoDBAttribute(attributeName = "data_model_response")
    public String getDataModelResponse() {
        return _dataModelResponse;
    }

    public void setDataModelResponse(final String _dataModelResponse) {
        this._dataModelResponse = _dataModelResponse;
    }

}
