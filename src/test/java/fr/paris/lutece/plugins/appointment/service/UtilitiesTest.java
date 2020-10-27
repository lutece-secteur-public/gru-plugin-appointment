package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.test.LuteceTestCase;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.junit.Test;

public class UtilitiesTest extends LuteceTestCase
{

    /**
     * Return the closest date in past a list of date with the given date
     */
    @Test
    public void testGetClosestDateInPast( )
    {
        LocalDate localDate1 = LocalDate.parse( "2022-06-05" );
        LocalDate localDate2 = LocalDate.parse( "2022-06-10" );
        LocalDate localDate3 = LocalDate.parse( "2022-06-25" );

        List<LocalDate> listDates = new ArrayList<>( );
        listDates.add( localDate1 );
        listDates.add( localDate2 );
        listDates.add( localDate3 );
        assertEquals( localDate2, Utilities.getClosestDateInPast( listDates, LocalDate.parse( "2022-06-15" ) ) );
    }

    /**
     * Return the closest date time in future in a list of date time and a given date time
     */
    @Test
    public void testGetClosestDateTimeInFuture( )
    {
        LocalDateTime localDateTime1 = LocalDateTime.parse( "2022-06-05T10:15" );
        LocalDateTime localDateTime2 = LocalDateTime.parse( "2022-06-10T10:30" );
        LocalDateTime localDateTime3 = LocalDateTime.parse( "2022-06-25T11:15" );

        List<LocalDateTime> listDateTime = new ArrayList<>( );
        listDateTime.add( localDateTime1 );
        listDateTime.add( localDateTime2 );
        listDateTime.add( localDateTime3 );
        assertEquals( localDateTime3, Utilities.getClosestDateTimeInFuture( listDateTime, LocalDateTime.parse( "2022-06-10T12:30" ) ) );
    }

    /**
     * Test of getDateFormatter method, of class Utilities.
     * Change was made to use ISO formatter - should be the same for any locale
     */
    @Test
    public void testGetDateFormatter() {
        System.out.println("getFormatter");
        
        AppointmentPlugin.setPluginLocale( Locale.ENGLISH );
        Utilities.resetDateFormatter();
        DateTimeFormatter formatterEn = Utilities.getDateFormatter();
        LocalDateTime localDateTimeEn = LocalDateTime.parse( "2022-06-25T00:00" );
        String strDateEn = localDateTimeEn.format(formatterEn);
        assertEquals( "2022-06-25" , strDateEn );

        AppointmentPlugin.setPluginLocale( Locale.FRENCH );
        Utilities.resetDateFormatter();
        DateTimeFormatter formatterFr = Utilities.getDateFormatter();
        LocalDateTime localDateTimeFr = LocalDateTime.parse( "2022-06-25T00:00" );
        String strDateFr = localDateTimeFr.format(formatterFr);
        assertEquals( "2022-06-25" , strDateFr );

    }

    /**
     * Test of getRimeFormatter method, of class Utilities.
     * Change was made to use ISO formatter - should be the same for any locale
     */
    @Test
    public void testGetTimeFormatter() {
        System.out.println("getFormatter");

        AppointmentPlugin.setPluginLocale( Locale.ENGLISH );
        Utilities.resetTimeFormatter();
        DateTimeFormatter formatterEn = Utilities.getTimeFormatter();
        LocalDateTime localDateTimeEn = LocalDateTime.parse( "2022-06-25T00:00" );
        String strDateEn = localDateTimeEn.format(formatterEn);
        assertEquals( "00:00:00" , strDateEn );

        AppointmentPlugin.setPluginLocale( Locale.FRENCH );
        Utilities.resetTimeFormatter();
        DateTimeFormatter formatterFr = Utilities.getTimeFormatter();
        LocalDateTime localDateTimeFr = LocalDateTime.parse( "2022-06-25T00:00" );
        String strDateFr = localDateTimeFr.format(formatterFr);
        assertEquals( "00:00:00" , strDateFr );

    }


}
