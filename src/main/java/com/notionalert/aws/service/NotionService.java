package com.notionalert.aws.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class represents a service for retrieving Notion data by making requests to the Notion API.
 */
public class NotionService {

    /**
     * Retrieves Notion data by making a request to the Notion API.
     *
     * @return The Notion data as a JsonObject.
     * @throws IOException if an I/O error occurs while making the request.
     */
    public JsonObject getNotionData() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String jsonBody = buildRequestBody();
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
            .url("https://api.notion.com/v1/databases/{notion_database_id}/query")
            .addHeader("Authorization", "Bearer {Notion_API_Key}")
            .addHeader("Content-Type", "application/json")
            .addHeader("Notion-Version", "2022-06-28")
            .post(body)
            .build();

        Response response = client.newCall(request).execute();
        return JsonParser.parseString(response.body().string()).getAsJsonObject();
    }

    /**
     * Builds the request body for the Notion API request.
     *
     * @return The request body as a JSON string.
     */
    private String buildRequestBody() {
        ZonedDateTime now = ZonedDateTime.now();
        String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00"));
        String formattedHourAfter = now.plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00"));

        JsonObject statusCondition = buildCondition("Status", "does_not_equal", "Archived");
        JsonObject afterCondition = buildDateCondition("Next Deadline", "on_or_after", formattedNow);
        JsonObject beforeCondition = buildDateCondition("Next Deadline", "on_or_before", formattedHourAfter);

        JsonObject filter = new JsonObject();
        filter.add("and", new JsonArray());
        filter.getAsJsonArray("and").add(statusCondition);
        filter.getAsJsonArray("and").add(afterCondition);
        filter.getAsJsonArray("and").add(beforeCondition);

        JsonObject filterObject = new JsonObject();
        filterObject.add("filter", filter);

        return filterObject.toString();
    }

    /**
     * Builds a condition JsonObject with the given property, operator, and value.
     *
     * @param property The property to filter on.
     * @param operator The operator for the condition.
     * @param value The value for the condition.
     * @return The condition JsonObject.
     */
    private JsonObject buildCondition(String property, String operator, String value) {
        JsonObject condition = new JsonObject();
        JsonObject details = new JsonObject();
        details.addProperty(operator, value);
        condition.addProperty("property", property);
        condition.add("status", details);
        return condition;
    }

    /**
     * Builds a date condition JsonObject with the given property, operator, and value.
     *
     * @param property The property to filter on.
     * @param operator The operator for the condition.
     * @param value The value for the condition.
     * @return The date condition JsonObject.
     */
    private JsonObject buildDateCondition(String property, String operator, String value) {
        JsonObject condition = new JsonObject();
        JsonObject details = new JsonObject();
        JsonObject date = new JsonObject();
        date.addProperty(operator, value);
        details.add("date", date);
        condition.addProperty("property", property);
        condition.add("formula", details);
        return condition;
    }

}
