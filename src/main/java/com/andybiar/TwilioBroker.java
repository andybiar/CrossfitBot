package com.andybiar;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

public final class TwilioBroker {
    private static TwilioRestClient client;
    private static final String SID = "";
    private static final String AUTH = "";
    
    public static void send(String body) {
        client = new TwilioRestClient(SID, AUTH);
            
        List <NameValuePair> params = new ArrayList <NameValuePair>();
        params.add(new BasicNameValuePair("From", ""));
        params.add(new BasicNameValuePair("To", ""));
        params.add(new BasicNameValuePair("Body", body));
        
        MessageFactory messageFactory = client.getAccount().getMessageFactory();
   
        try {
            Message message = messageFactory.create(params);
            System.out.println(message.getSid());
        } catch (TwilioRestException e) {
            e.printStackTrace();
        }
    }
}
