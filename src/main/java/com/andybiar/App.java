package com.andybiar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 
 */
public class App 
{
    public static void main( String[] args )
    {    
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet getRequest = new HttpGet("http://crossfit.com");     
        CloseableHttpResponse response = null;

        // Execute the GET request
        try {
            response = httpClient.execute(getRequest);
            HttpEntity entity = response.getEntity();
     
            BufferedReader br = new BufferedReader(
                             new InputStreamReader((response.getEntity().getContent())));
     
            // Begin Crossfit-specific scraping logic
            
            // Get and format today's date
            String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
            String[] splitdate = new SimpleDateFormat("dd-MM-yyyy").format(new Date()).split("-");
            
            StringBuilder sb = new StringBuilder();
            sb.append(months[Integer.parseInt(splitdate[1]) - 1]);
            sb.append(" " + splitdate[0] + ", " + splitdate[2]);
            String formattedDate = new String(sb);
            //formattedDate = "July 17, 2015"; // optional manual override
            
            // Loop through HTML lines
            StringBuilder workout = new StringBuilder();
            String output;
            boolean scraping = false;
            while ((output = br.readLine()) != null) {
                
                // Detect today's workout delineator
                if (output.contains("<div class=\"date\">") && output.contains(formattedDate)) {
                    String[] firstLine = output.split("<p>");
                    String reps = firstLine[firstLine.length - 1];
                    workout.append(sanitize(reps) + "\n");
                    
                    scraping = true;
                }
                
                // Scrape each following line of the workout
                else if (scraping == true) {
                    if (output.contains("<p>")) {
                        scraping = false;
                    }
                    
                    workout.append(sanitize(output) + "\n");
                }
            }
            
            TwilioBroker.send(new String(workout));
            
            // End Crossfit-specific scraping logic
     
            EntityUtils.consume(entity);     
          } catch (IOException e) {    
            e.printStackTrace();
          } finally {
              try {
                  if (response != null) {
                      response.close();
                  }
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
    }
    
    private static String sanitize(String html) {
        return html.split("<")[0];
    }
}
