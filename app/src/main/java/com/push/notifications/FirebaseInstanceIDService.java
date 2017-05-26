package com.push.notifications;

/**
 * Created by abhijitk on 9/7/2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sns.util.Topics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FBInstanceIDService";
    private static String refreshedToken;
    private static final String AWS_ACCESS_KEY = "AKIAJAYELUZDY";
    private static final String AWS_SECRET_KEY = "+vM1Fqoz3XKQC03sTyOEJQKpPmiOS6Z3G";

    private static final String platformApplicationArn = "arn:aws:sns:us-east-1:245846145792:app/GCM/PushNotification";

    @Override
    public void onTokenRefresh() {
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String tokens) {
        new AsyncTask() {
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    token = tokens;//GCM project number
                    Log.i("registrationId", token);

                    AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
                    AmazonSNSClient pushClient = new AmazonSNSClient(awsCredentials);
                    CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();

                    platformEndpointRequest.setCustomUserData("abhijeet");
                    platformEndpointRequest.setToken(token);
                    platformEndpointRequest.setPlatformApplicationArn(platformApplicationArn);
                    pushClient.setRegion(Region.getRegion(Regions.US_EAST_1));

                    CreatePlatformEndpointResult result = pushClient.createPlatformEndpoint(platformEndpointRequest);
                    Log.e("Registration result", result.toString());
                } catch (Exception e) {
                    Log.i("Registration Error", e.getMessage());
                }
                return true;
            }
        }.execute(null, null, null);
    }

    String topic = "arn:aws:sns:us-east-1:245846145792:TrueInsightPushNotificationTopic";

    private void createTopic() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        snsService = new AmazonSNSClient(awsCredentials);

        CreateTopicRequest createReq = new CreateTopicRequest().withName(topic);
        CreateTopicResult createRes = snsService.createTopic(createReq);
        Log.i(TAG,"createTopic.createRes : " + createRes);
        SubscribeResult sr = snsService.subscribe(new SubscribeRequest(topic, "application", platformApplicationArn));
        Log.i(TAG,"createTopic.sr : " +  sr);
    }

    AmazonSNSClient snsService;
    public void createSNSService() throws Exception {
        try {
            AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
            snsService = new AmazonSNSClient(awsCredentials);

            SubscribeResult sr = snsService.subscribe(new SubscribeRequest(topic, "application", platformApplicationArn));
            Log.i(TAG,"createSNSService.sr : " + sr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification() {
        System.out.println("In send");
        snsService.publish(new PublishRequest(topic, "New Notification! Message from " + topic));
    }

}