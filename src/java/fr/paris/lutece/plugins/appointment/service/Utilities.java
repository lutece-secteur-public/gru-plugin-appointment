package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Class of utilities
 * 
 * @author Laurent Payen
 *
 */
public final class Utilities
{

    public static final String FORMAT_DATE = "dd/MM/yyyy";
    private static DateTimeFormatter _formatter = DateTimeFormatter.ofPattern( FORMAT_DATE );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private Utilities( )
    {
    }

    /**
     * Getter for the formatter
     * 
     * @return the formatter
     */
    public static DateTimeFormatter getFormatter( )
    {
        return _formatter;
    }

    /**
     * Setter for the formatter
     * 
     * @param formatter
     *            the formatter to set
     */
    public static void setFormatter( DateTimeFormatter formatter )
    {
        _formatter = formatter;
    }

    /**
     * Return the closest date in past a list of date with the given date
     * 
     * @param listDate
     *            the list of date
     * @param dateToSearch
     *            the date to search
     * @return the closest date in past
     */
    public static LocalDate getClosestDateInPast( List<LocalDate> listDate, LocalDate dateToSearch )
    {
        return listDate.stream( ).filter( x -> x.isBefore( dateToSearch ) || x.isEqual( dateToSearch ) ).max( LocalDate::compareTo ).orElse( null );
    }

    /**
     * Return the closest date time in future in a list of date time and a given date time
     * 
     * @param listDateTime
     *            the list of date time
     * @param dateTimeToSearch
     *            the date time to search
     * @return the closest date time in the list in the future of the given date time
     */
    public static LocalDateTime getClosestDateTimeInFuture( List<LocalDateTime> listDateTime, LocalDateTime dateTimeToSearch )
    {
        return listDateTime.stream( ).filter( x -> x.isAfter( dateTimeToSearch ) || x.isEqual( dateTimeToSearch ) ).min( LocalDateTime::compareTo )
                .orElse( null );
    }

}
