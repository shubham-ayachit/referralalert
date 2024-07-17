package com.notionalert.aws.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Method;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.stream.*; // Add this import statement

/**
 * This class contains unit tests for the NotionService class.
 */
public class NotionServiceTest {

    private NotionService notionService;
    private Method buildRequestBodyMethod;

    /**
     * Sets up the necessary objects and methods for testing.
     * @throws Exception if an exception occurs during setup
     */
    @Before
    public void setUp() throws Exception {
        notionService = new NotionService();
        buildRequestBodyMethod = NotionService.class.getDeclaredMethod("buildRequestBody");
        buildRequestBodyMethod.setAccessible(true);
    }

    /**
     * Tests the structure of the request body built by the buildRequestBody method.
     * The request body should have a "filter" object with an "and" array containing 3 elements.
     * @throws Exception if an exception occurs during the test
     */
    @Test
    public void testBuildRequestBodyStructure() throws Exception {
        String jsonResult = (String) buildRequestBodyMethod.invoke(notionService);
        JsonObject jsonObject = JsonParser.parseString(jsonResult).getAsJsonObject();
        
        assertTrue(jsonObject.has("filter"));
        assertTrue(jsonObject.getAsJsonObject("filter").has("and"));
        assertEquals(3, jsonObject.getAsJsonObject("filter").getAsJsonArray("and").size());
    }

    /**
     * Tests the status condition in the request body built by the buildRequestBody method.
     * The status condition should check if the "status" does not equal "Archived".
     * @throws Exception if an exception occurs during the test
     */
    @Test
    public void testBuildRequestBodyStatusCondition() throws Exception {
        String jsonResult = (String) buildRequestBodyMethod.invoke(notionService);
        JsonObject jsonObject = JsonParser.parseString(jsonResult).getAsJsonObject();
        
        boolean statusConditionFound = StreamSupport.stream(jsonObject.getAsJsonObject("filter").getAsJsonArray("and").spliterator(), false)
            .anyMatch(element -> element.getAsJsonObject().has("status") &&
                                 element.getAsJsonObject().get("status").getAsJsonObject().has("does_not_equal") &&
                                 element.getAsJsonObject().get("status").getAsJsonObject().get("does_not_equal").getAsString().equals("Archived"));
        
        assertTrue(statusConditionFound);
    }

    /**
     * Tests the next deadline conditions in the request body built by the buildRequestBody method.
     * The next deadline conditions should check if the "formula.date" is on or after a certain date
     * and on or before another date.
     * @throws Exception if an exception occurs during the test
     */
    @Test
    public void testBuildRequestBodyNextDeadlineConditions() throws Exception {
        String jsonResult = (String) buildRequestBodyMethod.invoke(notionService);
        JsonObject jsonObject = JsonParser.parseString(jsonResult).getAsJsonObject();
        
        boolean afterConditionFound = StreamSupport.stream(jsonObject.getAsJsonObject("filter").getAsJsonArray("and").spliterator(), false)
            .anyMatch(element -> element.getAsJsonObject().has("formula") &&
                                 element.getAsJsonObject().get("formula").getAsJsonObject().has("date") &&
                                 element.getAsJsonObject().get("formula").getAsJsonObject().get("date").getAsJsonObject().has("on_or_after"));
        
        boolean beforeConditionFound = StreamSupport.stream(jsonObject.getAsJsonObject("filter").getAsJsonArray("and").spliterator(), false)
            .anyMatch(element -> element.getAsJsonObject().has("formula") &&
                                 element.getAsJsonObject().get("formula").getAsJsonObject().has("date") &&
                                 element.getAsJsonObject().get("formula").getAsJsonObject().get("date").getAsJsonObject().has("on_or_before"));
        
        assertTrue(afterConditionFound && beforeConditionFound);
    }
}