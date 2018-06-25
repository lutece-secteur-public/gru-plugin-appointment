package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDateTime;

public class Constants
{

    // Slot
    public final static LocalDateTime STARTING_DATE_1 = LocalDateTime.parse( "2017-01-27T09:00" );
    public final static LocalDateTime STARTING_DATE_2 = LocalDateTime.parse( "2017-01-28T09:30" );
    public final static LocalDateTime STARTING_DATE_3 = LocalDateTime.parse( "2017-01-29T09:30" );
    public final static LocalDateTime ENDING_DATE_1 = LocalDateTime.parse( "2017-01-27T09:30" );
    public final static LocalDateTime ENDING_DATE_2 = LocalDateTime.parse( "2017-01-28T10:00" );
    public final static LocalDateTime ENDING_DATE_3 = LocalDateTime.parse( "2017-01-29T10:00" );
    public final static boolean IS_OPEN_1 = true;
    public final static boolean IS_OPEN_2 = false;
    public final static int NB_REMAINING_PLACES_1 = 1;
    public final static int NB_REMAINING_PLACES_2 = 2;
    public final static int NB_REMAINING_PLACES_3 = 3;

    // User
    public final static String GUID_1 = "guid1";
    public final static String GUID_2 = "guid2";
    public final static String FIRST_NAME_1 = "firstName1";
    public final static String FIRST_NAME_2 = "firstName2";
    public final static String LAST_NAME_1 = "lastName1";
    public final static String LAST_NAME_2 = "lastName2";
    public final static String EMAIL_1 = "email1";
    public final static String EMAIL_2 = "email2";
    public final static String PHONE_NUMBER_1 = "0605040302";
    public final static String PHONE_NUMBER_2 = "0605040303";
}
