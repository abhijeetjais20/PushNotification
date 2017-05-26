package com.push.notifications;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sns.model.Topic;
import com.amazonaws.services.sns.util.Topics;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Handle possible data accompanying notification message.
        Button subscribeButton = (Button) findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("news");
                String msg = getString(R.string.msg_subscribed);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        Button logTokenButton = (Button) findViewById(R.id.logTokenButton);
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = FirebaseInstanceId.getInstance().getToken();
                String msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                subscribeToTopic(token);
            }
        });
    }

    private static final String AWS_ACCESS_KEY = "AKIAJAYELUZDYTTAML4Q";
    private static final String AWS_SECRET_KEY = "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";
    private static final String platformApplicationArn = "arn:aws:sns:us-east-1:245846145792:app/GCM/PushNotification";
    private static final String topic = "arn:aws:sns:us-east-1:245846145792:PushNotificationTopic";
    private static final String EndPoinArn = "arn:aws:sns:us-east-1:245846145792:endpoint/GCM/PushNotification/f848c1ef-d99b-333f-bf9c-a1facd7ae77f";

    private void createTopic() {// http://docs.aws.amazon.com/sns/latest/dg/using-awssdkjava.html
        new AsyncTask() {
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
                    //create a new SNS client and set endpoint
                    AmazonSNSClient snsClient = new AmazonSNSClient(awsCredentials);
                    snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));

                    //create a new SNS topic
                    CreateTopicRequest createTopicRequest = new CreateTopicRequest("MyNewTopic");
                    CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
                    //print TopicArn
                    System.out.println(createTopicResult);
                    //get request id for CreateTopicRequest from SNS metadata
                    System.out.println("CreateTopicRequest - " + snsClient.getCachedResponseMetadata(createTopicRequest));
                } catch (Exception e) {
                    Log.i("Registration Error", e.getMessage());
                }
                return true;
            }
        }.execute(null, null, null);
    }


    private void subscribeToTopic(final String tokens) {// http://docs.aws.amazon.com/sns/latest/dg/using-awssdkjava.html
        new AsyncTask() {
            protected Object doInBackground(final Object... params) {
                String token = tokens;
                try {
                    AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
                    //create a new SNS client and set endpoint
                    AmazonSNSClient snsClient = new AmazonSNSClient(awsCredentials);
                    snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));

                    //subscribe to an SNS topic
                    SubscribeRequest subRequest = new SubscribeRequest(topic, "application",EndPoinArn );   //working
//                    SubscribeRequest subRequest = new SubscribeRequest(topic, "email", "abhinc.com");   //working
//                    SubscribeRequest subRequest = new SubscribeRequest(topic, "sms","9190169886" );   //working

                    snsClient.subscribe(subRequest);
                    //get request id for SubscribeRequest from SNS metadata
                    System.out.println("Check your email and confirm subscription.");

                } catch (Exception e) {
                    Log.i("Registration Error", e.getMessage());
                }
                return true;
            }
        }.execute(null, null, null);

    }
}
