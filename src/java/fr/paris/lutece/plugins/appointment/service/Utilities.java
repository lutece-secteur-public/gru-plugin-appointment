/*
 * Copyright (c) 2002-2018, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Class of utilities
 * 
 */
public final class Utilities
{
    private static DateTimeFormatter _date_formatter;

    private static DateTimeFormatter _time_formatter;

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private Utilities( )
    {
    }

    /**
     * Getter for the date formatter
     * 
     * @return the formatter
     */
    public static DateTimeFormatter getDateFormatter( )
    {
        if( _date_formatter == null )
        {
           // _formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale( AppointmentPlugin.getPluginLocale() );
            _date_formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        }
        return _date_formatter;
    }

    /**
     * Getter for the time formatter
     *
     * @return the formatter
     */
    public static DateTimeFormatter getTimeFormatter( )
    {
        if( _time_formatter == null )
        {
            _time_formatter = DateTimeFormatter.ISO_LOCAL_TIME;
        }
        return _time_formatter;
    }

    /**
     * Setter for the formatter
     * 
     * @param formatter
     *            the formatter to set
     * @deprecated Useless setter
     */
    @Deprecated
    public static void setFormatter( DateTimeFormatter formatter )
    {
        _date_formatter = formatter;
    }

    /**
     * Reset formatter scope package to be only used by unit tests
     */
    static void resetDateFormatter()
    {
        _date_formatter = null;
    }


    /**
     * Reset formatter scope package to be only used by unit tests
     */
    static void resetTimeFormatter()
    {
        _time_formatter = null;
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

