package com.notionalert.aws.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import software.amazon.awssdk.regions.Region; 
import software.amazon.awssdk.services.sns.SnsClient; 
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler; 

import java.io.IOException; 
import java.time.LocalDateTime; 
import java.time.temporal.ChronoUnit; 

import java.util.Map;
import java.time.format.DateTimeFormatter;


/**
 * This class is responsible for sending notification messages based on certain conditions.
 * It implements the RequestHandler interface to handle incoming requests.
 */
public class MessageNotification implements RequestHandler<Map<String, Object>, String> {

    protected NotionService notionService = new NotionService();

    /**
     * Handles the incoming request and sends the notification.
     * 
     * @param input   The input data for the request.
     * @param context The context object for the request.
     * @return A string indicating the success of the notification.
     */
    @Override
    public String handleRequest(Map<String,Object> input, Context context) {
        System.out.println("Received request: " + input);
        sendNotification();
        return "Notification sent successfully";
    }

    /**
     * Retrieves data from Notion and sends notifications for upcoming deadlines.
     */
    public void sendNotification() {
        try {
            JsonObject notionData = notionService.getNotionData();
            JsonArray results = notionData.getAsJsonArray("results");

            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
            LocalDateTime oneHourAhead = now.plusHours(1).truncatedTo(ChronoUnit.HOURS);
            for (JsonElement result : results) {
                JsonObject properties = result.getAsJsonObject().getAsJsonObject("properties");
                JsonObject nextStep = properties.getAsJsonObject("Next Deadline");

                if(nextStep != null && nextStep.has("formula") && !nextStep.get("formula").isJsonNull()) {
                    JsonObject formula = nextStep.getAsJsonObject("formula");
                    if(formula != null && formula.has("date") && !formula.get("date").isJsonNull()) {
                        JsonObject date = formula.getAsJsonObject("date");
                        String deadline = date.get("start").getAsString();
                        if(deadline != null) { 
                            LocalDateTime deadlineTime = LocalDateTime.parse(deadline, DateTimeFormatter.ISO_DATE_TIME);
                            if(deadlineTime.isAfter(now) && deadlineTime.isBefore(oneHourAhead)) {
                                StringBuilder message = new StringBuilder();
                                
                                message.append("Company: ")
                                .append(properties.getAsJsonObject("﻿Company").getAsJsonArray("title").get(0).getAsJsonObject().getAsJsonObject("text").get("content").getAsString())
                                .append(",")
                                .append(" deadline: ")
                                .append(deadline)
                                .append(" is coming up in the next hour. ")
                                .append("Person: ")
                                .append(properties.getAsJsonObject("Person").get("url").getAsString())
                                .append(", ")
                                .append("Status: ")
                                .append(properties.getAsJsonObject("Status").getAsJsonObject("status").get("name").getAsString())
                                .append(". ")
                                .append("Check it out here: ")
                                .append(result.getAsJsonObject().get("url").getAsString());

                                 
                                System.out.println(message.toString());
                                SnsClient snsClient = SnsClient.builder()
                                    .region(Region.US_EAST_1)
                                    .build();

                                    pubTextSMS(snsClient, message.toString(), "+19062312956");
                                    snsClient.close();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Publishes a text message notification using the SNS client.
     * 
     * @param snsClient   The SNS client object.
     * @param message     The message content.
     * @param phoneNumber The phone number to send the message to.
     */
    private void pubTextSMS(SnsClient snsClient, String message, String phoneNumber) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();

            PublishResponse result = snsClient.publish(request);
            System.out
                    .println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

}

