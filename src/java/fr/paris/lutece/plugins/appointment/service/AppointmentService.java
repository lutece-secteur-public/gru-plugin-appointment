/*
 * Copyright (c) 2002-2015, Mairie de Paris
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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.lang.time.DateUtils;

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentHoliDaysHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.listeners.IAppointmentFormListener;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;

/**
 * Service to manage calendars
 */
public class AppointmentService
{
    /**
     * Name of the bean of the service
     */
    public static final String BEAN_NAME = "appointment.appointmentService";

    /**
     * Get the number of characters of the random part of appointment reference
     */
    private static final int CONSTANT_REF_SIZE_RANDOM_PART = 5;

    /**
     * List of i18n keys of days of week
     */
    private static final String [ ] MESSAGE_LIST_DAYS_OF_WEEK = {
            "appointment.manageCalendarSlots.labelMonday", "appointment.manageCalendarSlots.labelTuesday", "appointment.manageCalendarSlots.labelWednesday",
            "appointment.manageCalendarSlots.labelThursday", "appointment.manageCalendarSlots.labelFriday", "appointment.manageCalendarSlots.labelSaturday",
            "appointment.manageCalendarSlots.labelSunday",
    };

    // Properties
    private static final String PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT = "appointment.form.nbWeekToCreate";
    private static final String PROPERTY_REF_SIZE_RANDOM_PART = "appointment.refSizeRandomPart";
    private static final String PROPERTY_REF_ENCRYPTION_ALGORITHM = "appointment.refEncryptionAlgorithm";

    // Constantes
    private static final String CONSTANT_MINUS = "-";
    private static final String CONSTANT_H = "h";
    private static final String CONSTANT_ZERO = "0";
    private static final int CONSTANT_MINUTES_IN_HOUR = 60;
    private static final int CONSTANT_NB_DAYS_IN_WEEK = 7;
    private static final long CONSTANT_MILISECONDS_IN_DAY = 86400000L;
    private static final String CONSTANT_SHA256 = "SHA-256";
    private static final int CONSTANT_MILL_WEEK = 24 * 7;

    /**
     * Instance of the service
     */
    private static volatile AppointmentService _instance;

    /**
     * Get an instance of the service
     * 
     * @return An instance of the service
     */
    public static AppointmentService getService( )
    {
        if ( _instance == null )
        {
            _instance = SpringContextService.getBean( BEAN_NAME );
        }

        return _instance;
    }

    /**
     * Get a string array containing a list of i118n keys of days of week
     * 
     * @return A string array containing a list of i118n keys of days of week
     */
    public static String [ ] getListDaysOfWeek( )
    {
        return MESSAGE_LIST_DAYS_OF_WEEK.clone( );
    }

    /**
     * Compute the list of days with the list of slots for a given form and a given week. The number of free places of slots are initialized to the number of
     * available places.
     * 
     * @param form
     *            The form to get days of. Opening and closing hour of the form are updated by this method
     * @return The list of days
     */
    public List<AppointmentDay> computeDayList( AppointmentForm form )
    {
        Date dateMin = getDateMonday( 0 );

        String [ ] strOpeningTime = form.getTimeStart( ).split( CONSTANT_H );
        String [ ] strClosingTime = form.getTimeEnd( ).split( CONSTANT_H );
        form.setOpeningHour( Integer.parseInt( strOpeningTime [0] ) );
        form.setOpeningMinutes( Integer.parseInt( strOpeningTime [1] ) );
        form.setClosingHour( Integer.parseInt( strClosingTime [0] ) );
        form.setClosingMinutes( Integer.parseInt( strClosingTime [1] ) );

        boolean [ ] bArrayIsOpen = {
                form.getIsOpenMonday( ), form.getIsOpenTuesday( ), form.getIsOpenWednesday( ), form.getIsOpenThursday( ), form.getIsOpenFriday( ),
                form.getIsOpenSaturday( ), form.getIsOpenSunday( ),
        };
        long lMilisecDate = dateMin.getTime( );
        List<AppointmentDay> listDays = new ArrayList<AppointmentDay>( bArrayIsOpen.length );

        for ( int i = 0; i < bArrayIsOpen.length; i++ )
        {
            AppointmentDay day = getAppointmentDayFromForm( form );
            day.setDate( new Date( lMilisecDate ) );
            day.setIsOpen( bArrayIsOpen [i] );
            day.setListSlots( computeDaySlots( day ) );

            listDays.add( day );
            lMilisecDate += CONSTANT_MILISECONDS_IN_DAY;
        }

        return listDays;
    }

    /**
     * Find a list of days with the list of slots for a given form and a given week, and compute missing data. The number of free places of slots are
     * initialized to the number of available places.
     * 
     * @param form
     *            The form to get days of. Opening and closing hour of the form are updated by this method
     * @param nOffsetWeeks
     *            The offset of the week to get
     * @param bLoadSlotsFromDb
     *            True if slots should be loaded from the database, false if they should be computed
     * @return The list of days
     */
    public List<AppointmentDay> findAndComputeDayList( AppointmentForm form, int nOffsetWeeks, boolean bLoadSlotsFromDb )
    {
        Date dateLimit = form.getDateLimit( );
        Date dateMin = getDateMonday( nOffsetWeeks );
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
        calendar.setTime( dateMin );
        calendar.add( Calendar.DAY_OF_MONTH, 6 );

        Date dateMax = new Date( calendar.getTimeInMillis( ) );

        if ( dateLimit != null )
        {
            Calendar calEnd = Calendar.getInstance( );
            calEnd.setTime( dateLimit );
            calEnd.add( Calendar.DAY_OF_MONTH, 6 );
            int diff = Calendar.SATURDAY - calEnd.get( Calendar.DAY_OF_WEEK );
            calEnd.add( Calendar.DATE, diff );
            dateLimit = new java.sql.Date( calEnd.getTime( ).getTime( ) );
        }

        if ( ( dateLimit != null ) && dateMax.after( dateLimit ) )
        {

            dateMax = dateLimit;
        }

        List<AppointmentDay> listDaysFound = AppointmentDayHome.getDaysBetween( form.getIdForm( ), dateMin, dateMax ); /*
                                                                                                                        * recupere une semaine entre lundi -
                                                                                                                        * samedi d'une semaine donnée c a d
                                                                                                                        * numweek = 2 de mars => 7/03 - 12/03
                                                                                                                        */

        String [ ] strOpeningTime = form.getTimeStart( ).split( CONSTANT_H );
        String [ ] strClosingTime = form.getTimeEnd( ).split( CONSTANT_H );
        form.setOpeningHour( Integer.parseInt( strOpeningTime [0] ) );
        form.setOpeningMinutes( Integer.parseInt( strOpeningTime [1] ) );
        form.setClosingHour( Integer.parseInt( strClosingTime [0] ) );
        form.setClosingMinutes( Integer.parseInt( strClosingTime [1] ) );

        boolean [ ] bArrayIsOpen = {
                form.getIsOpenMonday( ), form.getIsOpenTuesday( ), form.getIsOpenWednesday( ), form.getIsOpenThursday( ), form.getIsOpenFriday( ),
                form.getIsOpenSaturday( ), form.getIsOpenSunday( ),
        };
        long lMilisecDate = dateMin.getTime( );
        List<AppointmentDay> listDays = new ArrayList<AppointmentDay>( bArrayIsOpen.length );

        for ( int i = 0; i < bArrayIsOpen.length; i++ )
        {
            AppointmentDay day = null;

            if ( ( listDaysFound != null ) && ( listDaysFound.size( ) > 0 ) )
            {
                for ( AppointmentDay dayFound : listDaysFound )
                {
                    if ( ( dayFound.getDate( ).getTime( ) <= lMilisecDate )
                            && ( ( dayFound.getDate( ).getTime( ) + CONSTANT_MILISECONDS_IN_DAY ) > lMilisecDate ) )
                    {
                        day = dayFound;

                        break;
                    }
                }
            }

            if ( day == null )
            {
                day = getAppointmentDayFromForm( form );
                day.setDate( new Date( lMilisecDate ) );
                day.setIsOpen( bArrayIsOpen [i] );
            }

            if ( bLoadSlotsFromDb )
            {
                day.setListSlots( day.getIsOpen( ) ? AppointmentSlotHome.findByIdFormAndDayOfWeek( form.getIdForm( ), i + 1 ) : new ArrayList<AppointmentSlot>(
                        0 ) );
            }
            else
            {
                day.setListSlots( computeDaySlots( day ) );
            }

            listDays.add( day );

            if ( dateLimit != null )
            {
                if ( lMilisecDate < dateLimit.getTime( ) )
                {
                    lMilisecDate += CONSTANT_MILISECONDS_IN_DAY;
                }
                else
                {
                    break;
                }
            }
            else
            {
                lMilisecDate += CONSTANT_MILISECONDS_IN_DAY;
            }
        }

        return listDays;
    }

    /**
     * Compute dateDiff
     * 
     * @param date1
     *            first date
     * @param date2
     *            second date
     * @return diffrence of date
     */
    public static long getDateDiff( Date date1, Date date2 )
    {
        long diffInMillies = date2.getTime( ) - date1.getTime( );

        return TimeUnit.MILLISECONDS.convert( diffInMillies, TimeUnit.MILLISECONDS );
    }

    /**
     * Get Date to Midnight
     * 
     * @return calendar date midnight
     */
    private static Calendar getDateMidnight( )
    {
        Calendar cal = Calendar.getInstance( Locale.FRENCH );
        cal.set( Calendar.HOUR_OF_DAY, 23 );
        cal.set( Calendar.MINUTE, 59 );
        cal.set( Calendar.SECOND, 59 );

        return cal;
    }

    /**
     *
     * @param iDaysBeforeAppointment
     *            the days before the date
     * @param listDays
     *            list of days
     * @param calStart
     *            start clendar
     * @param calEnd
     *            end calendar
     * @param objNow
     *            calendar current date
     * @return a unvalid Appointments before Now
     */
    private static List<AppointmentDay> unvalidAppointmentsbeforeNow( int iDaysBeforeAppointment, List<AppointmentDay> listDays, Calendar calStart,
            Calendar calEnd, Calendar objNow )
    {
        Calendar objNowTmp = GregorianCalendar.getInstance( Locale.FRANCE );
        int nbMilli = Long.valueOf( TimeUnit.HOURS.toMillis( iDaysBeforeAppointment ) ).intValue( );

        int nIsEnbledDay = getIsEnbledDay( listDays );
        if ( nIsEnbledDay == 0 )
        {
            objNowTmp.add( Calendar.MILLISECOND, nbMilli );
        }
        if ( nIsEnbledDay == 1 )
        {
            int nbMilliWeek = Long.valueOf( TimeUnit.HOURS.toMillis( CONSTANT_MILL_WEEK ) ).intValue( );
            Calendar tmpCal = getFirstSlotEnbled( listDays, objNowTmp );
            if ( tmpCal != null )
            {
                nbMilli += (int) ( tmpCal.getTimeInMillis( ) - objNowTmp.getTimeInMillis( ) );
            }
            if ( nbMilli < nbMilliWeek )
                objNowTmp.add( Calendar.MILLISECOND, nbMilli );
        }

        for ( int i = 0; i < listDays.size( ); i++ )
        {
            if ( listDays.get( i ).getIsOpen( ) )
            {
                Calendar objEnd = getCalendarTime( listDays.get( i ).getDate( ), listDays.get( i ).getClosingHour( ), listDays.get( i ).getClosingMinutes( ) );

                if ( objEnd.after( calEnd ) )
                {
                    objEnd.setTime( calEnd.getTime( ) );
                }

                if ( listDays.get( i ).getListSlots( ) == null )
                {
                    listDays.get( i ).setListSlots( AppointmentSlotHome.findByIdDayWithFreePlaces( listDays.get( i ).getIdDay( ) ) );
                }

                for ( int index = 0; index < listDays.get( i ).getListSlots( ).size( ); index++ )
                {
                    Calendar tmpCal = getCalendarTime( listDays.get( i ).getDate( ), listDays.get( i ).getListSlots( ).get( index ).getStartingHour( ),
                            listDays.get( i ).getListSlots( ).get( index ).getStartingMinute( ) );
                    Calendar objNowClose = getCalendarTime( listDays.get( i ).getDate( ), objEnd.get( Calendar.HOUR_OF_DAY ), objEnd.get( Calendar.MINUTE ) );

                    if ( ( objNowTmp.after( tmpCal ) || tmpCal.after( objNowClose ) )
                            && ( listDays.get( i ).getListSlots( ).get( index ).getNbFreePlaces( ) > 0 ) ) // Already an appointments
                    {
                        listDays.get( i ).getListSlots( ).get( index ).setIsEnabled( false );
                    }
                }
            }

        }

        return listDays;
    }

    /**
     *
     * @param listDays
     *            list of days
     * @param objNow
     *            today
     */
    private static Calendar getFirstSlotEnbled( List<AppointmentDay> listDays, Calendar objNow )
    {
        for ( int i = 0; i < listDays.size( ); i++ )
        {
            if ( listDays.get( i ).getIsOpen( ) )
            {

                if ( listDays.get( i ).getListSlots( ) == null )
                {
                    listDays.get( i ).setListSlots( AppointmentSlotHome.findByIdDayWithFreePlaces( listDays.get( i ).getIdDay( ) ) );
                }

                for ( int index = 0; index < listDays.get( i ).getListSlots( ).size( ); index++ )
                {
                    Calendar tmpCal = getCalendarTime( listDays.get( i ).getDate( ), listDays.get( i ).getListSlots( ).get( index ).getStartingHour( ),
                            listDays.get( i ).getListSlots( ).get( index ).getStartingMinute( ) );

                    if ( !objNow.after( tmpCal ) && listDays.get( i ).getListSlots( ).get( index ).getNbFreePlaces( ) > 0 ) // Already an appointments
                    {
                        Calendar calReturn = getCalendarTime( listDays.get( i ).getDate( ), listDays.get( i ).getListSlots( ).get( index ).getStartingHour( ),
                                listDays.get( i ).getListSlots( ).get( index ).getStartingMinute( ) );
                        return calReturn;
                    }
                    /*
                     * if ( listDays.get( i ).getListSlots( ).get( index ).getIsEnabled() ) //Already an appointments { Calendar calReturn = getCalendarTime(
                     * listDays.get( i ).getDate( ), listDays.get( i ).getListSlots( ).get( index ).getStartingHour( ), listDays.get( i ).getListSlots( ).get(
                     * index ).getStartingMinute( ) ); return calReturn; }
                     */
                }
            }
        }

        return null;
    }

    /**
     *
     * @param listDays
     *            list of days
     */
    private static int getIsEnbledDay( List<AppointmentDay> listDays )
    {
        Calendar objNow = GregorianCalendar.getInstance( Locale.FRANCE );
        int nbMilli = Long.valueOf( TimeUnit.HOURS.toMillis( 24 * 7 ) ).intValue( );
        // objNowTmp.add( Calendar.MILLISECOND, nbMilli );
        int nIsEnbledDay = -1;
        while ( nIsEnbledDay == -1 && listDays.size( ) > 0 )
        {
            for ( int i = 0; i < listDays.size( ); i++ )
            {
                if ( listDays.get( i ).getDate( ).getYear( ) == objNow.getTime( ).getYear( )
                        && listDays.get( i ).getDate( ).getMonth( ) == objNow.getTime( ).getMonth( )
                        && listDays.get( i ).getDate( ).getDate( ) == objNow.getTime( ).getDate( ) )
                {
                    if ( listDays.get( i ).getIsOpen( ) )
                        return 0;
                    else
                        return 1;
                }
            }
            objNow.add( Calendar.MILLISECOND, nbMilli );
            nIsEnbledDay = -1;
        }

        return -1;
    }

    /**
     * Transform Date to Calendar
     * 
     * @param objTime
     *            the time
     * @param iHour
     *            the hours
     * @param iMinute
     *            the minutes
     * @return Calendar Time
     */
    private static Calendar getCalendarTime( Date objTime, int iHour, int iMinute )
    {
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRENCH );

        if ( objTime != null )
        {
            calendar.setTime( objTime );
        }

        calendar.set( Calendar.HOUR_OF_DAY, iHour );
        calendar.set( Calendar.MINUTE, iMinute );

        return calendar;
    }

    /**
     * Is week can be visible
     * 
     * @param listDays
     *            the list days
     * @return enable week
     */
    private static boolean isWeekEnabled( List<AppointmentDay> listDays )
    {
        boolean bRet = false;

        for ( AppointmentDay tmpDay : listDays )
        {
            if ( tmpDay.getIsOpen( ) )
            {
                for ( AppointmentSlot tmpApp : tmpDay.getListSlots( ) )
                {
                    if ( tmpApp.getIsEnabled( ) )
                    {
                        Calendar myCal = GregorianCalendar.getInstance( Locale.FRENCH );
                        Calendar tmpCal = getCalendarTime( tmpDay.getDate( ), tmpApp.getStartingHour( ), tmpApp.getStartingMinute( ) );

                        if ( tmpCal.after( myCal ) )
                        {
                            bRet = true;
                        }
                    }
                }
            }
        }

        return bRet;
    }

    /**
     * Check monday from the offset
     * 
     * @param nOffsetWeeks
     *            offset weeks
     * @return Monday Week
     */
    private static Calendar [ ] getMondayWeek( int nOffsetWeeks )
    {
        Calendar [ ] retCal = new Calendar [ 2];
        Calendar dateMin = GregorianCalendar.getInstance( Locale.FRANCE );
        // We set the week to the requested one
        dateMin.add( Calendar.DAY_OF_MONTH, 7 * nOffsetWeeks );

        // We get the current day of the week
        int nCurrentDayOfWeek = dateMin.get( Calendar.DAY_OF_WEEK );
        // We add the day of the week to Monday on the calendar
        dateMin.add( Calendar.DAY_OF_WEEK, Calendar.MONDAY - nCurrentDayOfWeek );
        retCal [0] = dateMin;

        Calendar dateMax = (Calendar) dateMin.clone( );
        dateMax.add( Calendar.DAY_OF_MONTH, 6 );
        retCal [1] = dateMax;

        return retCal;
    }

    /**
     * Get all ListDays beetween 2 dates
     * 
     * @param form
     *            the form
     * @param nOffsetWeeks
     *            nOffsetWeeks
     * @return list Days
     */
    private List<AppointmentDay> getListDays( AppointmentForm form, MutableInt nOffsetWeeks, Calendar objNow )
    {
        Calendar [ ] calendar = getMondayWeek( nOffsetWeeks.intValue( ) );

        Date dateMin = new Date( calendar [0].getTimeInMillis( ) );

        Date dateMax = new Date( calendar [1].getTimeInMillis( ) );

        if ( ( form.getDateLimit( ) != null ) && dateMax.after( form.getDateLimit( ) ) )
        {
            dateMax = form.getDateLimit( );
        }

        List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( form.getIdForm( ), dateMin, dateMax );
        Calendar calendarEnd = getCalendarTime( form.getDateEndValidity( ), form.getClosingHour( ), form.getClosingMinutes( ) );
        Calendar calendarStart = getCalendarTime( form.getDateStartValidity( ), form.getOpeningHour( ), form.getOpeningMinutes( ) );

        listDays = unvalidAppointmentsbeforeNow( form.getMinDaysBeforeAppointment( ), listDays, calendarStart, calendarEnd, objNow );

        return listDays;
    }

    /**
     * Get the list of days of a form to display them in a calendar. Days and slots are not computed by this method but loaded from the database. The number of
     * free places of each slot is also loaded. Pasted days are marked as closed.
     * 
     * @param form
     *            The form the get days of.
     * @param nOffsetWeeks
     *            The offset of weeks (0 for the current week, 1 for the next, ...). If the parameter bGetFirstEmptyWeek is set to true, then the value of this
     *            parameter will be updated with the offset of the loaded week
     * @param bIsForFront
     *            True if the calendar is for Front Office, false if it is for Back Office. If the calendar is for Front Office, then the n next days will be
     *            closed (n is set by the {@link AppointmentForm#getMinDaysBeforeAppointment()} attribute).
     * @param bGetFirstEmptyWeek
     *            True to automatically load the first week with an available slot, false to load the required week
     * @return The list of days found
     */
    public List<AppointmentDay> getDayListForCalendar( AppointmentForm form, MutableInt nOffsetWeeks, boolean bIsForFront, boolean bGetFirstEmptyWeek )
    {
        Calendar objNow = GregorianCalendar.getInstance( Locale.FRANCE );
        List<AppointmentDay> listDays = getListDays( form, nOffsetWeeks, objNow );
        objNow = calculateNextSlotOpen( listDays, form, nOffsetWeeks );

        if ( bIsForFront )
        {
            int nSave = nOffsetWeeks.intValue( );

            if ( !bGetFirstEmptyWeek )
            {
                while ( !isWeekEnabled( listDays ) && ( nOffsetWeeks.intValue( ) < form.getNbWeeksToDisplay( ) ) )
                {
                    nOffsetWeeks.increment( );
                    listDays = getListDays( form, nOffsetWeeks, objNow );
                }

                if ( !isWeekEnabled( listDays ) )
                {
                    listDays = getListDays( form, new MutableInt( nSave ), objNow );
                }
            }
            else
            {
                objNow = GregorianCalendar.getInstance( Locale.FRANCE );

                while ( !isWeekEnabled( listDays ) && ( nOffsetWeeks.intValue( ) > 0 ) )
                {
                    nOffsetWeeks.decrement( );
                    listDays = getListDays( form, nOffsetWeeks, GregorianCalendar.getInstance( Locale.FRANCE ) );
                }

                if ( !isWeekEnabled( listDays ) )
                {
                    listDays = getListDays( form, new MutableInt( 0 ), GregorianCalendar.getInstance( Locale.FRANCE ) );
                }
            }
        }

        return listDays;
    }

    /**
     * Get the list of every days between the current day and the last day of the form open for appointments.
     * 
     * @param form
     *            The form
     * @return The list of required days
     */
    public List<AppointmentDay> getAllAvailableDays( AppointmentForm form )
    {
        // Date date = getDateLastMonday( );
        // Date date = new Date( System.currentTimeMillis( ) );
        /*
         * Calendar calendarFrom = GregorianCalendar.getInstance( Locale.FRANCE ); calendarFrom.setTime( date ); calendarFrom.add( Calendar.HOUR,
         * form.getMinDaysBeforeAppointment( ) );
         */
        /*
         * Calendar calendarFrom = GregorianCalendar.getInstance( Locale.FRANCE ); List<AppointmentDay> listDays = getListDays( form, new MutableInt( 0 ),
         * calendarFrom ); calendarFrom = calculateNextSlotOpen( listDays, form, new MutableInt( 0 ) ); calendarFrom.add( Calendar.HOUR,
         * form.getMinDaysBeforeAppointment( ) );
         */
        Date DateForm = new Date( System.currentTimeMillis( ) );

        List<AppointmentDay> listeDay = getDayListForCalendar( form, new MutableInt( 0 ), true, false );
        if ( listeDay != null && listeDay.size( ) > 0 )
        {
            DateForm = listeDay.get( 0 ).getDate( );
        }

        Calendar calendarTo = GregorianCalendar.getInstance( Locale.FRANCE );
        calendarTo.setTime( getDateLastMonday( ) );
        calendarTo.add( Calendar.WEEK_OF_MONTH, AppointmentApp.getMaxWeek( form.getNbWeeksToDisplay( ), form ) );
        // We remove the last monday
        calendarTo.add( Calendar.DAY_OF_MONTH, -1 );

        return AppointmentDayHome.getDaysBetween( form.getIdForm( ), DateForm, new Date( calendarTo.getTimeInMillis( ) ) );
    }

    /**
     * Get the list of appointment slots for a given day
     * 
     * @param day
     *            the day to initialize
     * @return The list of slots computed from the day
     */
    public List<AppointmentSlot> computeDaySlots( AppointmentDay day )
    {
        if ( !day.getIsOpen( ) )
        {
            return new ArrayList<AppointmentSlot>( 0 );
        }

        return computeDaySlots( day, getDayOfWeek( day.getDate( ) ) );
    }

    /**
     * Get the list of appointment slots for a given day
     * 
     * @param day
     *            the day to initialize. The date of the day will NOT be used
     * @param nDayOfWeek
     *            The day of the week of the day
     * @return The list of slots computed from the day
     */
    public List<AppointmentSlot> computeDaySlots( AppointmentDay day, int nDayOfWeek )
    {
        List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>( );

        // We compute the total number of minutes the service is opened this day
        int nOpeningDuration = ( ( day.getClosingHour( ) * 60 ) + day.getClosingMinutes( ) ) - ( ( day.getOpeningHour( ) * 60 ) + day.getOpeningMinutes( ) );

        if ( nOpeningDuration > 0 )
        {
            int nNbSlots = nOpeningDuration / day.getAppointmentDuration( );
            int nStartingHour = day.getOpeningHour( );
            int nStartingMinutes = day.getOpeningMinutes( );

            for ( int i = 0; i < nNbSlots; i++ )
            {
                AppointmentSlot slot = new AppointmentSlot( );
                slot.setStartingHour( nStartingHour );
                slot.setStartingMinute( nStartingMinutes );
                slot.setNbPlaces( day.getPeoplePerAppointment( ) );
                slot.setNbFreePlaces( slot.getNbPlaces( ) );
                slot.setIdForm( day.getIdForm( ) );
                slot.setIdDay( day.getIdDay( ) );
                slot.setDayOfWeek( nDayOfWeek );
                // We compute the next starting minutes and hours
                nStartingMinutes += day.getAppointmentDuration( );
                nStartingHour += ( nStartingMinutes / CONSTANT_MINUTES_IN_HOUR );
                nStartingMinutes = nStartingMinutes % CONSTANT_MINUTES_IN_HOUR;
                slot.setEndingHour( nStartingHour );
                slot.setEndingMinute( nStartingMinutes );
                slot.setIsEnabled( day.getIsOpen( ) );

                listSlots.add( slot );
            }
        }

        return listSlots;
    }

    /**
     * Get a list of string that describes times of appointments available for a day
     * 
     * @param nAppointmentDuration
     *            The appointment duration
     * @param nOpeningHour
     *            The opening hour of the day
     * @param nOpeningMinutes
     *            The opening minutes of the day
     * @param nClosingHour
     *            The closing hour of the day
     * @param nClosingMinutes
     *            The closing minutes of the day
     * @return The list of times of appointments formatted as HHhMM. The closing time is not included in the list.
     */
    public List<String> getListAppointmentTimes( int nAppointmentDuration, int nOpeningHour, int nOpeningMinutes, int nClosingHour, int nClosingMinutes )
    {
        String strCacheKey = AppointmentFormCacheService.getListAppointmentTimesCacheKey( nAppointmentDuration, nOpeningHour, nOpeningMinutes, nClosingHour,
                nClosingMinutes );

        List<String> listTimes = (List<String>) AppointmentFormCacheService.getInstance( ).getFromCache( strCacheKey );

        if ( listTimes != null )
        {
            return new ArrayList<String>( listTimes );
        }

        listTimes = new ArrayList<String>( );

        int nOpeningDuration = ( ( nClosingHour * 60 ) + nClosingMinutes ) - ( ( nOpeningHour * 60 ) + nOpeningMinutes );
        int nNbSlots = nOpeningDuration / nAppointmentDuration;
        int nStartingHour = nOpeningHour;
        int nStartingMinutes = nOpeningMinutes;

        for ( int i = 0; i < nNbSlots; i++ )
        {
            listTimes.add( getFormatedStringTime( nStartingHour, nStartingMinutes ) );
            nStartingMinutes = nStartingMinutes + nAppointmentDuration;
            nStartingHour = nStartingHour + ( nStartingMinutes / CONSTANT_MINUTES_IN_HOUR );
            nStartingMinutes = nStartingMinutes % CONSTANT_MINUTES_IN_HOUR;
        }

        AppointmentFormCacheService.getInstance( ).putInCache( strCacheKey, new ArrayList<String>( listTimes ) );

        return listTimes;
    }

    /**
     * Get a string that describe a given time
     * 
     * @param nHour
     *            The hour of the time to describe
     * @param nMinute
     *            The minute of the time to describe
     * @return The string describing the given time. the returned string match the pattern <b>HHhMM</b>
     */
    public String getFormatedStringTime( int nHour, int nMinute )
    {
        StringBuilder sbTime = new StringBuilder( );

        if ( nHour < 10 )
        {
            sbTime.append( CONSTANT_ZERO );
        }

        sbTime.append( nHour );
        sbTime.append( CONSTANT_H );

        if ( nMinute < 10 )
        {
            sbTime.append( CONSTANT_ZERO );
        }

        sbTime.append( nMinute );

        return sbTime.toString( );
    }

    /**
     * Get an appointment day from an appointment form. The date of the day and its opening are not initialized.
     * 
     * @param appointmentForm
     *            The form
     * @return The day
     */
    public AppointmentDay getAppointmentDayFromForm( AppointmentForm appointmentForm )
    {
        AppointmentDay day = new AppointmentDay( );
        day.setOpeningHour( appointmentForm.getOpeningHour( ) );
        day.setOpeningMinutes( appointmentForm.getOpeningMinutes( ) );
        day.setClosingHour( appointmentForm.getClosingHour( ) );
        day.setClosingMinutes( appointmentForm.getClosingMinutes( ) );
        day.setAppointmentDuration( appointmentForm.getDurationAppointments( ) );
        day.setPeoplePerAppointment( appointmentForm.getPeoplePerAppointment( ) );
        day.setIdForm( appointmentForm.getIdForm( ) );

        return day;
    }

    /**
     * Get the day of the week of a date.
     * 
     * @param date
     *            The date to get the day of the week of
     * @return 1 for Monday, 2 for Tuesday, ..., 7 for Sunday
     */
    public int getDayOfWeek( Date date )
    {
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
        calendar.setTime( date );

        int nDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK ) - 1;

        if ( nDayOfWeek <= 0 )
        {
            nDayOfWeek = nDayOfWeek + 7;
        }

        return nDayOfWeek;
    }

    /**
     * Check that a form has every days created for its coming weeks
     * 
     * @param form
     *            The form to check
     */
    public void checkFormDays( AppointmentForm form, Boolean bForSlot )
    {
        AppLogService.info( "checkFormDays IN " );

        int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 );
        List<Date> listClosingDays = AppointmentHoliDaysHome.findByIdForm( form.getIdForm( ) );
        AppLogService.info( "listClosingDays SIZE IN " + listClosingDays.size( ) );

        // We synchronize the method by id form to avoid collisions between manual and daemon checks
        synchronized( AppointmentService.class + Integer.toString( form.getIdForm( ) ) )
        {
            int maxWeek = form.getNbWeeksToDisplay( ) + nNbWeeksToCreate;

            if ( form.getNbWeeksToDisplay( ) == 0 )
            {
                Calendar cal = GregorianCalendar.getInstance( Locale.FRANCE );
                Calendar calEnd = GregorianCalendar.getInstance( );

                if ( ( form.getDateEndValidity( ) != null ) && form.getDateEndValidity( ).after( form.getDateLimit( ) ) )
                {
                    calEnd.setTime( form.getDateEndValidity( ) );
                }
                else
                {
                    calEnd.setTime( form.getDateLimit( ) );
                }

                long diff = calEnd.getTimeInMillis( ) - cal.getTimeInMillis( );
                long diffDays = diff / ( 24 * 60 * 60 * 1000 );
                diffDays = diffDays + 1;
                maxWeek = (int) diffDays / 7;

                int nCurrentDayOfWeek = cal.get( cal.DAY_OF_WEEK );
                cal.add( Calendar.DAY_OF_WEEK, Calendar.MONDAY - nCurrentDayOfWeek );
                Date datMax = null;
                do
                {
                    cal.add( Calendar.WEEK_OF_YEAR, maxWeek );
                    datMax = new Date( cal.getTimeInMillis( ) );
                    if ( datMax.before( form.getDateLimit( ) ) )
                    {
                        maxWeek = maxWeek + 1;
                    }
                    cal = GregorianCalendar.getInstance( Locale.FRANCE );
                    cal.add( Calendar.DAY_OF_WEEK, Calendar.MONDAY - nCurrentDayOfWeek );
                }
                while ( datMax.before( form.getDateLimit( ) ) );

                maxWeek = maxWeek + 1;

            }
            /*
             * Map<String,Boolean> mapIsOpen = new HashedMap(); List<AppointmentDay> listDaysR = AppointmentDayHome.findByIdForm(form.getIdForm()); List listDay
             * = null; if (listDaysR != null) { for (AppointmentDay day : listDaysR) { mapIsOpen.put(day.getDate().toString(),day.getIsOpen()); listDay =
             * AppointmentSlotHome.findByCrossIdDay(day.getIdDay()); if(listDay != null && listDay.isEmpty()){ AppointmentDayHome.remove(day.getIdDay()); }
             * listDay = null;
             * 
             * 
             * } }
             */
            // We check every weeks from the current to the first not displayable
            for ( int nOffsetWeeks = 0; nOffsetWeeks < maxWeek; nOffsetWeeks++ )
            {
                Date date = new Date( System.currentTimeMillis( ) );
                Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
                calendar.setTime( date );
                // We set the week to the requested one
                calendar.add( Calendar.DAY_OF_MONTH, 7 * nOffsetWeeks );

                // We get the current day of the week
                int nCurrentDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );
                // We add the day of the week to Monday on the calendar
                calendar.add( Calendar.DAY_OF_WEEK, Calendar.MONDAY - nCurrentDayOfWeek );

                Date dateMin = new Date( calendar.getTimeInMillis( ) );
                calendar.add( Calendar.DAY_OF_MONTH, 6 );

                Date dateMax = new Date( calendar.getTimeInMillis( ) );

                List<AppointmentDay> listDaysFound = AppointmentDayHome.getDaysBetween( form.getIdForm( ), dateMin, dateMax );

                // If there is no days associated with the given week, or if some days does not exist
                if ( ( listDaysFound == null ) || ( listDaysFound.size( ) < CONSTANT_NB_DAYS_IN_WEEK ) )
                {
                    List<AppointmentDay> listDays = findAndComputeDayList( form, nOffsetWeeks, true );

                    for ( AppointmentDay day : listDays )
                    {

                        // set closing days
                        for ( Date closeDay : listClosingDays )
                        {
                            AppLogService.info( "closeDay " + closeDay.toString( ) );

                            if ( DateUtils.isSameDay( closeDay, day.getDate( ) ) )
                            {
                                day.setIsOpen( false );
                                AppLogService.info( "closing day : OK" );
                            }
                        }

                        // If the day has not already been created, we create it
                        if ( day.getIdDay( ) == 0 )
                        {
                            int nNbFreePlaces = 0;

                            for ( AppointmentSlot slot : day.getListSlots( ) )
                            {
                                if ( slot.getIsEnabled( ) )
                                {
                                    if ( day.getPeoplePerAppointment( ) != 0 )
                                    {
                                        if ( bForSlot )
                                        {
                                            slot.setNbPlaces( slot.getNbPlaces( ) /* day.getPeoplePerAppointment( ) */);
                                            // slot.setNbPlaces( day.getPeoplePerAppointment( ) /*day.getPeoplePerAppointment( )*/);
                                        }
                                        else
                                        {
                                            // slot.setNbPlaces( slot.getNbPlaces() /*day.getPeoplePerAppointment( )*/);
                                            slot.setNbPlaces( day.getPeoplePerAppointment( ) /* day.getPeoplePerAppointment( ) */);
                                        }
                                    }

                                    nNbFreePlaces += slot.getNbPlaces( );
                                }
                            }

                            day.setFreePlaces( nNbFreePlaces );

                            AppointmentDayHome.create( day );

                            for ( AppointmentSlot slot : day.getListSlots( ) )
                            {
                                slot.setIdDay( day.getIdDay( ) );
                                AppointmentSlotHome.create( slot );
                            }
                        }

                        // // If the day has not already been created, we create it
                        // if ( day.getIdDay( ) != 0 )
                        // {
                        // int nNbFreePlaces = 0;
                        //
                        // for ( AppointmentSlot slot : day.getListSlots( ) )
                        // {
                        // if ( slot.getIsEnabled( ) )
                        // {
                        // if ( day.getPeoplePerAppointment( ) != 0 )
                        // {
                        // slot.setNbPlaces(slot.getNbPlaces( ) );
                        // }
                        //
                        // nNbFreePlaces += slot.getNbPlaces( );
                        // }
                        // }
                        //
                        // day.setFreePlaces( nNbFreePlaces );
                        // AppointmentDayHome.create( day );
                        //
                        // for ( AppointmentSlot slot : day.getListSlots( ) )
                        // {
                        // slot.setIdDay( day.getIdDay( ) );
                        // AppointmentSlotHome.create( slot );
                        // }
                        // }

                    }
                }
            }
        }
    }

    /**
     * Get the date of the last Monday.
     * 
     * @return The date of the last Monday
     */
    public Date getDateLastMonday( )
    {
        return getDateMonday( 0 );
    }

    /**
     * Get the date of a Monday.
     * 
     * @param nOffsetWeek
     *            The offset of the week (0 for the current week, 1 for the next one, ...)
     * @return The date of the Monday of the requested week
     */
    public Date getDateMonday( int nOffsetWeek )
    {
        Date date = new Date( System.currentTimeMillis( ) );
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
        calendar.setTime( date );
        // We set the week to the requested one
        calendar.add( Calendar.DAY_OF_MONTH, 7 * nOffsetWeek );

        // We get the current day of the week
        int nCurrentDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );
        // We add the day of the week to Monday on the calendar
        calendar.add( Calendar.DAY_OF_WEEK, Calendar.MONDAY - nCurrentDayOfWeek );

        return new Date( calendar.getTimeInMillis( ) );
    }

    /**
     * Reset days and slots of a form. Each day and each associated slot of the form that are associated with a future date are removed and re-created
     * 
     * @param form
     *            The form to rest days of
     */
    public void resetFormDays( AppointmentForm form )
    {
        Date dateLastMonday = getDateLastMonday( );
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
        calendar.setTime( dateLastMonday );

        // We add
        int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 );
        calendar.add( Calendar.DAY_OF_WEEK, ( ( form.getNbWeeksToDisplay( ) + nNbWeeksToCreate ) * 7 ) - 1 );

        Date dateMax = new Date( calendar.getTimeInMillis( ) );

        List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( form.getIdForm( ), dateLastMonday, dateMax );

        for ( AppointmentDay day : listDays )
        {
            AppointmentDayHome.remove( day.getIdDay( ) );
        }

        checkFormDays( form, false );
    }

    /**
     * Get the minimum appointment duration in a list of days for a given form, and add the list of begin time in a given list
     * 
     * @param listDays
     *            The list of days to consider
     * @param form
     *            The form associated with the given days
     * @param listTimeBegin
     *            The list to insert begin times in
     * @return The minimum duration of appointments
     */
    public int getListTimeBegin( List<AppointmentDay> listDays, AppointmentForm form, List<String> listTimeBegin )
    {
        // We compute slots interval
        int nMinOpeningHour = form.getOpeningHour( );
        int nMinOpeningMinutes = form.getOpeningMinutes( );
        int nMaxClosingHour = form.getClosingHour( );
        int nMaxClosingMinutes = form.getClosingMinutes( );
        int nMinAppointmentDuration = form.getDurationAppointments( );
        int nMinOpeningTime = getTime( nMinOpeningHour, nMinOpeningMinutes );
        int nMaxClosingTime = getTime( nMaxClosingHour, nMaxClosingMinutes );

        for ( AppointmentDay appointmentDay : listDays )
        {
            if ( appointmentDay.getIsOpen( ) && ( appointmentDay.getIdDay( ) > 0 ) )
            {
                // we check that the day has not a longer or shorter appointment duration
                if ( appointmentDay.getAppointmentDuration( ) < nMinAppointmentDuration )
                {
                    nMinAppointmentDuration = appointmentDay.getAppointmentDuration( );
                }

                int nTime = getTime( appointmentDay.getOpeningHour( ), appointmentDay.getOpeningMinutes( ) );

                if ( nTime < nMinOpeningTime )
                {
                    nMinOpeningHour = appointmentDay.getOpeningHour( );
                    nMinOpeningMinutes = appointmentDay.getOpeningMinutes( );
                    nMinOpeningTime = nTime;
                }

                nTime = getTime( appointmentDay.getClosingHour( ), appointmentDay.getClosingMinutes( ) );

                if ( nTime > nMaxClosingTime )
                {
                    nMaxClosingHour = appointmentDay.getClosingHour( );
                    nMaxClosingMinutes = appointmentDay.getClosingMinutes( );
                    nMaxClosingTime = nTime;
                }

                // We only check for duration in slots because we assume that slots were computed not to exceed the limits of days
                if ( appointmentDay.getListSlots( ) != null )
                {
                    for ( AppointmentSlot slot : appointmentDay.getListSlots( ) )
                    {
                        int nSlotDuration = getTime( slot.getEndingHour( ), slot.getEndingMinute( ) )
                                - getTime( slot.getStartingHour( ), slot.getStartingMinute( ) );

                        if ( nSlotDuration < nMinAppointmentDuration )
                        {
                            nMinAppointmentDuration = nSlotDuration;
                        }
                    }
                }
            }
        }

        listTimeBegin.addAll( getListAppointmentTimes( nMinAppointmentDuration, nMinOpeningHour, nMinOpeningMinutes, nMaxClosingHour, nMaxClosingMinutes ) );

        return nMinAppointmentDuration;
    }

    /**
     * Get the size of the random part of the reference of appointments
     * 
     * @return The size of the random part of the reference of appointments
     */
    public int getRefSizeRandomPart( )
    {
        return AppPropertiesService.getPropertyInt( PROPERTY_REF_SIZE_RANDOM_PART, CONSTANT_REF_SIZE_RANDOM_PART );
    }

    /**
     * Compute the unique reference of an appointment
     * 
     * @param appointment
     *            The appointment
     * @return The unique reference of an appointment
     */
    public String computeRefAppointment( Appointment appointment )
    {
        return appointment.getIdAppointment( )
                + CryptoService.encrypt( appointment.getIdAppointment( ) + appointment.getEmail( ),
                        AppPropertiesService.getProperty( PROPERTY_REF_ENCRYPTION_ALGORITHM, CONSTANT_SHA256 ) ).substring( 0, getRefSizeRandomPart( ) );
    }

    /**
     * Parse a string representing a positive or negative integer
     * 
     * @param strNumber
     *            The string to parse
     * @return The integer value of the number represented by the string, or 0 if the string could not be parsed
     */
    public int parseInt( String strNumber )
    {
        int nNumber = 0;

        if ( StringUtils.isEmpty( strNumber ) )
        {
            return nNumber;
        }

        if ( strNumber.startsWith( CONSTANT_MINUS ) )
        {
            String strParseableNumber = strNumber.substring( 1 );

            if ( StringUtils.isNumeric( strParseableNumber ) )
            {
                nNumber = Integer.parseInt( strParseableNumber ) * -1;
            }
        }
        else
            if ( StringUtils.isNumeric( strNumber ) )
            {
                nNumber = Integer.parseInt( strNumber );
            }

        return nNumber;
    }

    /**
     * Reset days and slots of a form. Each day and each associated slot of the form that are associated with a future date are removed and re-created
     * 
     * @param form
     *            The form to rest days of
     */
    public void resetFormDays( AppointmentForm form, Date dateMin, Boolean bCheckForSlot )
    {
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
        calendar.setTime( dateMin );

        // We add
        int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 );
        calendar.add( Calendar.DAY_OF_WEEK, ( ( form.getNbWeeksToDisplay( ) + nNbWeeksToCreate ) * 7 ) - 1 );

        Date dateMax = new Date( calendar.getTimeInMillis( ) );

        List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( form.getIdForm( ), dateMin, dateMax );

        for ( AppointmentDay day : listDays )
        {
            AppointmentDayHome.remove( day.getIdDay( ) );
        }

        checkFormDays( form, bCheckForSlot );
    }

    /**
     * Modify the slots of a form
     * 
     * @param form
     *            The form to rest days of
     * @param dateMin
     *            the date after that the form will change
     */
    public void modifySlotsDays( AppointmentForm form, Date dateMin )
    {
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
        calendar.setTime( dateMin );

        // We add
        int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 );
        calendar.add( Calendar.DAY_OF_WEEK, ( ( form.getNbWeeksToDisplay( ) + nNbWeeksToCreate ) * 7 ) - 1 );

        Date dateMax = new Date( calendar.getTimeInMillis( ) );

        List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( form.getIdForm( ), dateMin, dateMax );

        for ( AppointmentDay day : listDays )
        {
            AppointmentDayHome.remove( day.getIdDay( ) );
        }

        for ( AppointmentDay day : listDays )
        {
            // If the day has not already been created, we create it
            if ( day.getIdDay( ) == 0 )
            {
                int nNbFreePlaces = 0;

                for ( AppointmentSlot slot : day.getListSlots( ) )
                {
                    if ( slot.getIsEnabled( ) )
                    {
                        nNbFreePlaces += slot.getNbPlaces( );
                    }
                }

                day.setFreePlaces( nNbFreePlaces );
                AppointmentDayHome.create( day );

                for ( AppointmentSlot slot : day.getListSlots( ) )
                {
                    slot.setIdDay( day.getIdDay( ) );
                    AppointmentSlotHome.create( slot );
                }
            }
        }
    }

    /**
     * Convert a duration from hours and minutes to its time format. The time format is equal to the number of minutes of the given duration.
     * 
     * @param nHour
     *            The number of hours
     * @param nMinute
     *            The number of minutes
     * @return The number of minutes of the given duration
     */
    private int getTime( int nHour, int nMinute )
    {
        return ( nHour * 60 ) + nMinute;
    }

    /**
     * return day
     * 
     * @param listDays
     * @return
     */
    private Calendar calculateNextSlotOpen( List<AppointmentDay> listDays, AppointmentForm form, MutableInt nOffsetWeeks )
    {
        List<AppointmentDay> day = AppointmentDayHome.findByIdForm( form.getIdForm( ) );
        boolean bool = false;
        Calendar objNow = GregorianCalendar.getInstance( Locale.FRANCE );

        for ( int index = 0; index < day.size( ); index++ )
        {
            AppointmentDay dayTmp = day.get( index );

            if ( dayTmp.getDate( ).after( objNow.getTime( ) ) )
            {
                if ( dayTmp.getListSlots( ) == null )
                {
                    dayTmp.setListSlots( AppointmentSlotHome.findByIdDayWithFreePlaces( dayTmp.getIdDay( ) ) );
                }

                for ( int i = 0; i < dayTmp.getListSlots( ).size( ); i++ )
                {
                    Calendar tmpCal = getCalendarTime( dayTmp.getDate( ), dayTmp.getListSlots( ).get( i ).getStartingHour( ), dayTmp.getListSlots( ).get( i )
                            .getStartingMinute( ) );

                    if ( dayTmp.getListSlots( ).get( i ).getIsEnabled( ) && ( dayTmp.getListSlots( ).get( i ).getNbFreePlaces( ) > 0 ) && tmpCal.after( objNow ) )
                    {
                        objNow = tmpCal;
                        bool = true;

                        break;
                    }
                }
            }

            if ( bool )
            {
                break;
            }
        }

        return objNow;
    }

    /**
     * Notify listeners, for example indexers
     * 
     * @param nAppointmentFormId
     *            The slot id
     */
    public void notifyAppointmentFormModified( int nAppointmentFormId )
    {
        Collection<IAppointmentFormListener> listeners = SpringContextService.getBeansOfType( IAppointmentFormListener.class );
        for ( IAppointmentFormListener listener : listeners )
        {
            listener.onFormModifed( nAppointmentFormId );
        }
    }

}
