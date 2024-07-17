package com.notionalert.aws.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageNotificationTest {

    @Mock
    private NotionService mockNotionService;

    private MessageNotification messageNotification;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        messageNotification = new MessageNotification();
        messageNotification.notionService = mockNotionService;
    }

    @Test
    public void testSendNotificationNoResults() throws Exception {
        JsonObject emptyData = JsonParser.parseString("{\"results\":[]}").getAsJsonObject();
        when(mockNotionService.getNotionData()).thenReturn(emptyData);

        messageNotification.sendNotification();
        
        // Verify no messages are sent, possibly by checking logs or a mocked SNS client
    }

    @Test
    public void testSendNotificationNoUpcomingDeadlines() throws Exception {
        // Mock data with no deadlines within the next hour
        JsonObject dataWithNoUpcomingDeadlines = JsonParser.parseString("{\"results\":[...]}").getAsJsonObject(); // Fill in with appropriate JSON
        when(mockNotionService.getNotionData()).thenReturn(dataWithNoUpcomingDeadlines);

        messageNotification.sendNotification();

        // Verify no messages are sent
    }

    @Test
    public void testSendNotificationWithUpcomingDeadlines() throws Exception {
        // Mock data with a deadline within the next hour
        JsonObject dataWithUpcomingDeadline = JsonParser.parseString("{\"results\":[...]}").getAsJsonObject(); // Fill in with appropriate JSON
        when(mockNotionService.getNotionData()).thenReturn(dataWithUpcomingDeadline);

        messageNotification.sendNotification();

        // Verify a message is generated and sent
    }
}