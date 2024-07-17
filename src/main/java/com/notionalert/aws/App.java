package com.notionalert.aws;

import com.notionalert.aws.service.MessageNotification;

/**
 * Hello world!
 *
 */
public class App 
{

    private static MessageNotification messageNotification = new MessageNotification();
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        messageNotification.sendNotification();
    }
}
