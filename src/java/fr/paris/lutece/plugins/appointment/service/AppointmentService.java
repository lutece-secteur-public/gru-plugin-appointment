/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;

import org.apache.commons.lang.StringUtils;

import java.sql.Date;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


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
    private static final String[] MESSAGE_LIST_DAYS_OF_WEEK = 
        {
            "appointment.manageCalendarSlots.labelMonday", "appointment.manageCalendarSlots.labelTuesday",
            "appointment.manageCalendarSlots.labelWednesday", "appointment.manageCalendarSlots.labelThursday",
            "appointment.manageCalendarSlots.labelFriday", "appointment.manageCalendarSlots.labelSaturday",
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

    /**
     * Instance of the service
     */
    private static volatile AppointmentService _instance;

    /**
     * Get an instance of the service
     * @return An instance of the service
     */
    public static AppointmentService getService(  )
    {
        if ( _instance == null )
        {
            _instance = SpringContextService.getBean( BEAN_NAME );
        }

        return _instance;
    }

    /**
     * Get a string array containing a list of i118n keys of days of week
     * @return A string array containing a list of i118n keys of days of week
     */
    public static String[] getListDaysOfWeek(  )
    {
        return MESSAGE_LIST_DAYS_OF_WEEK.clone(  );
    }

    /**
     * Compute the list of days with the list of slots for a given form and a
     * given week. The number of free places of slots are initialized to the
     * number of available places.
     * @param form The form to get days of. Opening and closing hour of the form
     *            are updated by this method
     * @return The list of days
     */
    public List<AppointmentDay> computeDayList( AppointmentForm form )
    {
        Date dateMin = getDateMonday( 0 );

        String[] strOpeningTime = form.getTimeStart(  ).split( CONSTANT_H );
        String[] strClosingTime = form.getTimeEnd(  ).split( CONSTANT_H );
        form.setOpeningHour( Integer.parseInt( strOpeningTime[0] ) );
        form.setOpeningMinutes( Integer.parseInt( strOpeningTime[1] ) );
        form.setClosingHour( Integer.parseInt( strClosingTime[0] ) );
        form.setClosingMinutes( Integer.parseInt( strClosingTime[1] ) );

        boolean[] bArrayIsOpen = 
            {
                form.getIsOpenMonday(  ), form.getIsOpenTuesday(  ), form.getIsOpenWednesday(  ),
                form.getIsOpenThursday(  ), form.getIsOpenFriday(  ), form.getIsOpenSaturday(  ),
                form.getIsOpenSunday(  ),
            };
        long lMilisecDate = dateMin.getTime(  );
        List<AppointmentDay> listDays = new ArrayList<AppointmentDay>( bArrayIsOpen.length );

        for ( int i = 0; i < bArrayIsOpen.length; i++ )
        {
            AppointmentDay day = getAppointmentDayFromForm( form );
            day.setDate( new Date( lMilisecDate ) );
            day.setIsOpen( bArrayIsOpen[i] );
            day.setListSlots( computeDaySlots( day ) );

            listDays.add( day );
            lMilisecDate += CONSTANT_MILISECONDS_IN_DAY;
        }

        return listDays;
    }

    /**
     * Find a list of days with the list of slots for a given form and a given
     * week, and compute missing data. The number of free places of slots are
     * initialized to the number of available places.
     * @param form The form to get days of. Opening and closing hour of the form
     *            are updated by this method
     * @param nOffsetWeeks The offset of the week to get
     * @param bLoadSlotsFromDb True if slots should be loaded from the database,
     *            false if they should be computed
     * @return The list of days
     */
    public List<AppointmentDay> findAndComputeDayList( AppointmentForm form, int nOffsetWeeks, boolean bLoadSlotsFromDb )
    {
        Date dateMin = getDateMonday( nOffsetWeeks );
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
        calendar.setTime( dateMin );
        calendar.add( Calendar.DAY_OF_MONTH, 6 );

        Date dateMax = new Date( calendar.getTimeInMillis(  ) );

        List<AppointmentDay> listDaysFound = AppointmentDayHome.getDaysBetween( form.getIdForm(  ), dateMin, dateMax );

        String[] strOpeningTime = form.getTimeStart(  ).split( CONSTANT_H );
        String[] strClosingTime = form.getTimeEnd(  ).split( CONSTANT_H );
        form.setOpeningHour( Integer.parseInt( strOpeningTime[0] ) );
        form.setOpeningMinutes( Integer.parseInt( strOpeningTime[1] ) );
        form.setClosingHour( Integer.parseInt( strClosingTime[0] ) );
        form.setClosingMinutes( Integer.parseInt( strClosingTime[1] ) );

        boolean[] bArrayIsOpen = 
            {
                form.getIsOpenMonday(  ), form.getIsOpenTuesday(  ), form.getIsOpenWednesday(  ),
                form.getIsOpenThursday(  ), form.getIsOpenFriday(  ), form.getIsOpenSaturday(  ),
                form.getIsOpenSunday(  ),
            };
        long lMilisecDate = dateMin.getTime(  );
        List<AppointmentDay> listDays = new ArrayList<AppointmentDay>( bArrayIsOpen.length );

        for ( int i = 0; i < bArrayIsOpen.length; i++ )
        {
            AppointmentDay day = null;

            if ( ( listDaysFound != null ) && ( listDaysFound.size(  ) > 0 ) )
            {
                for ( AppointmentDay dayFound : listDaysFound )
                {
                    if ( ( dayFound.getDate(  ).getTime(  ) <= lMilisecDate ) &&
                            ( ( dayFound.getDate(  ).getTime(  ) + CONSTANT_MILISECONDS_IN_DAY ) > lMilisecDate ) )
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
                day.setIsOpen( bArrayIsOpen[i] );
            }

            if ( bLoadSlotsFromDb )
            {
                day.setListSlots( day.getIsOpen(  )
                    ? AppointmentSlotHome.findByIdFormAndDayOfWeek( form.getIdForm(  ), i + 1 )
                    : new ArrayList<AppointmentSlot>( 0 ) );
            }
            else
            {
                day.setListSlots( computeDaySlots( day ) );
            }

            listDays.add( day );
            lMilisecDate += CONSTANT_MILISECONDS_IN_DAY;
        }

        return listDays;
    }

    /**
     * Get the list of days of a form to display them in a calendar. Days and
     * slots are not computed by this method but loaded from the database. The
     * number of free places of each slot is also loaded. Pasted days are marked
     * as closed.
     * @param form The form the get days of.
     * @param nOffsetWeeks The offset of weeks (0 for the current week, 1 for
     *            the next, ...).
     * @param bIsForFront True if the calendar is for Front Office, false if it
     *            is for Back Office. If the calendar is for Front Office, then
     *            the n next days will be closed (n is set by the
     *            {@link AppointmentForm#getMinDaysBeforeAppointment()}
     *            attribute).
     * @return The list of days found
     */
    public List<AppointmentDay> getDayListForCalendar( AppointmentForm form, int nOffsetWeeks, boolean bIsForFront )
    {
        Date date = new Date( System.currentTimeMillis(  ) );
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
        calendar.setTime( date );

        int nRealOffset = nOffsetWeeks;

        if ( bIsForFront && ( form.getMinDaysBeforeAppointment(  ) > 7 ) )
        {
            nRealOffset += ( form.getMinDaysBeforeAppointment(  ) / 7 );
        }

        // We set the week to the requested one 
        calendar.add( Calendar.DAY_OF_MONTH, 7 * nRealOffset );

        // We get the current day of the week
        int nCurrentDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );
        // We add the day of the week to Monday on the calendar
        calendar.add( Calendar.DAY_OF_WEEK, Calendar.MONDAY - nCurrentDayOfWeek );

        Date dateMin = new Date( calendar.getTimeInMillis(  ) );
        calendar.add( Calendar.DAY_OF_MONTH, 6 );

        Date dateMax = new Date( calendar.getTimeInMillis(  ) );

        List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( form.getIdForm(  ), dateMin, dateMax );

        long lTimeOfYesterday = date.getTime(  ) - CONSTANT_MILISECONDS_IN_DAY;

        if ( bIsForFront )
        {
            lTimeOfYesterday += ( form.getMinDaysBeforeAppointment(  ) * CONSTANT_MILISECONDS_IN_DAY );
        }

        for ( AppointmentDay day : listDays )
        {
            if ( day.getDate(  ).getTime(  ) < lTimeOfYesterday )
            {
                day.setIsOpen( false );
            }
            else
            {
                if ( day.getIsOpen(  ) )
                {
                    day.setListSlots( AppointmentSlotHome.findByIdDayWithFreePlaces( day.getIdDay(  ) ) );
                }
                else
                {
                    day.setListSlots( new ArrayList<AppointmentSlot>(  ) );
                }
            }
        }

        return listDays;
    }

    /**
     * Get the list of appointment slots for a given day
     * @param day the day to initialize
     * @return The list of slots computed from the day
     */
    public List<AppointmentSlot> computeDaySlots( AppointmentDay day )
    {
        if ( !day.getIsOpen(  ) )
        {
            return new ArrayList<AppointmentSlot>( 0 );
        }

        return computeDaySlots( day, getDayOfWeek( day.getDate(  ) ) );
    }

    /**
     * Get the list of appointment slots for a given day
     * @param day the day to initialize. The date of the day will NOT be used
     * @param nDayOfWeek The day of the week of the day
     * @return The list of slots computed from the day
     */
    public List<AppointmentSlot> computeDaySlots( AppointmentDay day, int nDayOfWeek )
    {
        List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>(  );

        // We compute the total number of minutes the service is opened this day
        int nOpeningDuration = ( ( day.getClosingHour(  ) * 60 ) + day.getClosingMinutes(  ) ) -
            ( ( day.getOpeningHour(  ) * 60 ) + day.getOpeningMinutes(  ) );

        if ( nOpeningDuration > 0 )
        {
            int nNbSlots = nOpeningDuration / day.getAppointmentDuration(  );
            int nStartingHour = day.getOpeningHour(  );
            int nStartingMinutes = day.getOpeningMinutes(  );

            for ( int i = 0; i < nNbSlots; i++ )
            {
                AppointmentSlot slot = new AppointmentSlot(  );
                slot.setStartingHour( nStartingHour );
                slot.setStartingMinute( nStartingMinutes );
                slot.setNbPlaces( day.getPeoplePerAppointment(  ) );
                slot.setNbFreePlaces( slot.getNbPlaces(  ) );
                slot.setIdForm( day.getIdForm(  ) );
                slot.setIdDay( day.getIdDay(  ) );
                slot.setDayOfWeek( nDayOfWeek );
                // We compute the next starting minutes and hours
                nStartingMinutes += day.getAppointmentDuration(  );
                nStartingHour += ( nStartingMinutes / CONSTANT_MINUTES_IN_HOUR );
                nStartingMinutes = nStartingMinutes % CONSTANT_MINUTES_IN_HOUR;
                slot.setEndingHour( nStartingHour );
                slot.setEndingMinute( nStartingMinutes );
                slot.setIsEnabled( day.getIsOpen(  ) );

                listSlots.add( slot );
            }
        }

        return listSlots;
    }

    /**
     * Get a list of string that describes times of appointments available in
     * for a day
     * @param nAppointmentDuration The appointment duration
     * @param nOpeningHour The opening hour of the day
     * @param nOpeningMinutes The opening minutes of the day
     * @param nClosingHour The closing hour of the day
     * @param nClosingMinutes The closing minutes of the day
     * @return The list of times of appointments formatted as HHhMM. The closing
     *         time is not included in the list.
     */
    public List<String> getListAppointmentTimes( int nAppointmentDuration, int nOpeningHour, int nOpeningMinutes,
        int nClosingHour, int nClosingMinutes )
    {
        List<String> listTimes = new ArrayList<String>(  );
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

        return listTimes;
    }

    /**
     * Get a string that describe a given time
     * @param nHour The hour of the time to describe
     * @param nMinute The minute of the time to describe
     * @return The string describing the given time. the returned string match
     *         the pattern <b>HHhMM</b>
     */
    public String getFormatedStringTime( int nHour, int nMinute )
    {
        StringBuilder sbTime = new StringBuilder(  );

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

        return sbTime.toString(  );
    }

    /**
     * Get an appointment day from an appointment form. The date of the day and
     * its opening are not initialized.
     * @param appointmentForm The form
     * @return The day
     */
    public AppointmentDay getAppointmentDayFromForm( AppointmentForm appointmentForm )
    {
        AppointmentDay day = new AppointmentDay(  );
        day.setOpeningHour( appointmentForm.getOpeningHour(  ) );
        day.setOpeningMinutes( appointmentForm.getOpeningMinutes(  ) );
        day.setClosingHour( appointmentForm.getClosingHour(  ) );
        day.setClosingMinutes( appointmentForm.getClosingMinutes(  ) );
        day.setAppointmentDuration( appointmentForm.getDurationAppointments(  ) );
        day.setPeoplePerAppointment( appointmentForm.getPeoplePerAppointment(  ) );
        day.setIdForm( appointmentForm.getIdForm(  ) );

        return day;
    }

    /**
     * Get the day of the week of a date.
     * @param date The date to get the day of the week of
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
     * @param form The form to check
     */
    public void checkFormDays( AppointmentForm form )
    {
        int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 );

        // We synchronize the method by id form to avoid collisions between manual and daemon checks
        synchronized ( AppointmentService.class + Integer.toString( form.getIdForm(  ) ) )
        {
            // We check every weeks from the current to the first not displayable
            for ( int nOffsetWeeks = 0; nOffsetWeeks < ( form.getNbWeeksToDisplay(  ) + nNbWeeksToCreate );
                    nOffsetWeeks++ )
            {
                Date date = new Date( System.currentTimeMillis(  ) );
                Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
                calendar.setTime( date );
                // We set the week to the requested one 
                calendar.add( Calendar.DAY_OF_MONTH, 7 * nOffsetWeeks );

                // We get the current day of the week
                int nCurrentDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );
                // We add the day of the week to Monday on the calendar
                calendar.add( Calendar.DAY_OF_WEEK, Calendar.MONDAY - nCurrentDayOfWeek );

                Date dateMin = new Date( calendar.getTimeInMillis(  ) );
                calendar.add( Calendar.DAY_OF_MONTH, 6 );

                Date dateMax = new Date( calendar.getTimeInMillis(  ) );

                List<AppointmentDay> listDaysFound = AppointmentDayHome.getDaysBetween( form.getIdForm(  ), dateMin,
                        dateMax );

                // If there is no days associated with the given week, or if some days does not exist
                if ( ( listDaysFound == null ) || ( listDaysFound.size(  ) < CONSTANT_NB_DAYS_IN_WEEK ) )
                {
                    List<AppointmentDay> listDays = findAndComputeDayList( form, nOffsetWeeks, true );

                    for ( AppointmentDay day : listDays )
                    {
                        // If the day has not already been created, we create it
                        if ( day.getIdDay(  ) == 0 )
                        {
                            AppointmentDayHome.create( day );

                            for ( AppointmentSlot slot : day.getListSlots(  ) )
                            {
                                slot.setIdDay( day.getIdDay(  ) );
                                AppointmentSlotHome.create( slot );
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the date of the last Monday.
     * @return The date of the last Monday
     */
    public Date getDateLastMonday(  )
    {
        return getDateMonday( 0 );
    }

    /**
     * Get the date of a Monday.
     * @param nOffsetWeek The offset of the week (0 for the current week, 1 for
     *            the next one, ...)
     * @return The date of the Monday of the requested week
     */
    public Date getDateMonday( int nOffsetWeek )
    {
        Date date = new Date( System.currentTimeMillis(  ) );
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
        calendar.setTime( date );
        // We set the week to the requested one 
        calendar.add( Calendar.DAY_OF_MONTH, 7 * nOffsetWeek );

        // We get the current day of the week
        int nCurrentDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );
        // We add the day of the week to Monday on the calendar
        calendar.add( Calendar.DAY_OF_WEEK, Calendar.MONDAY - nCurrentDayOfWeek );

        return new Date( calendar.getTimeInMillis(  ) );
    }

    /**
     * Reset days and slots of a form. Each day and each associated slot of the
     * form that are associated with a future date are removed and re-created
     * @param form The form to rest days of
     */
    public void resetFormDays( AppointmentForm form )
    {
        Date dateLastMonday = getDateLastMonday(  );
        Calendar calendar = GregorianCalendar.getInstance( Locale.FRANCE );
        calendar.setTime( dateLastMonday );

        // We add 
        int nNbWeeksToCreate = AppPropertiesService.getPropertyInt( PROPERTY_NB_WEEKS_TO_CREATE_FOR_BO_MANAGEMENT, 1 );
        calendar.add( Calendar.DAY_OF_WEEK, ( ( form.getNbWeeksToDisplay(  ) + nNbWeeksToCreate ) * 7 ) - 1 );

        Date dateMax = new Date( calendar.getTimeInMillis(  ) );

        List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( form.getIdForm(  ), dateLastMonday, dateMax );

        for ( AppointmentDay day : listDays )
        {
            AppointmentDayHome.remove( day.getIdDay(  ) );
        }

        checkFormDays( form );
    }

    /**
     * Get the list of begin times for a given list of days
     * @param listDays The list of days to consider
     * @param form The form associated with the given days
     * @param listTimeBegin The list to insert begin times in
     * @return The minimum duration of appointments
     */
    public int getListTimeBegin( List<AppointmentDay> listDays, AppointmentForm form, List<String> listTimeBegin )
    {
        // We compute slots interval
        int nMinOpeningHour = form.getOpeningHour(  );
        int nMinOpeningMinutes = form.getOpeningMinutes(  );
        int nMaxClosingHour = form.getClosingHour(  );
        int nMaxClosingMinutes = form.getClosingMinutes(  );
        int nMinAppointmentDuration = form.getDurationAppointments(  );

        for ( AppointmentDay appointmentDay : listDays )
        {
            if ( appointmentDay.getIsOpen(  ) && ( appointmentDay.getIdDay(  ) > 0 ) )
            {
                // we check that the day has is not a longer or has a shorter appointment duration 
                if ( appointmentDay.getAppointmentDuration(  ) < nMinAppointmentDuration )
                {
                    nMinAppointmentDuration = appointmentDay.getAppointmentDuration(  );
                }

                if ( appointmentDay.getOpeningHour(  ) < nMinOpeningHour )
                {
                    nMinOpeningHour = appointmentDay.getOpeningHour(  );
                }

                if ( appointmentDay.getOpeningMinutes(  ) < nMinOpeningMinutes )
                {
                    nMinOpeningMinutes = appointmentDay.getOpeningMinutes(  );
                }

                if ( appointmentDay.getClosingHour(  ) > nMaxClosingHour )
                {
                    nMaxClosingHour = appointmentDay.getClosingHour(  );
                }

                if ( appointmentDay.getClosingMinutes(  ) > nMaxClosingMinutes )
                {
                    nMaxClosingMinutes = appointmentDay.getClosingMinutes(  );
                }
            }
        }

        listTimeBegin.addAll( getListAppointmentTimes( nMinAppointmentDuration, nMinOpeningHour, nMinOpeningMinutes,
                nMaxClosingHour, nMaxClosingMinutes ) );

        return nMinAppointmentDuration;
    }

    /**
     * Get the size of the random part of the reference of appointments
     * @return The size of the random part of the reference of appointments
     */
    public int getRefSizeRandomPart(  )
    {
        return AppPropertiesService.getPropertyInt( PROPERTY_REF_SIZE_RANDOM_PART, CONSTANT_REF_SIZE_RANDOM_PART );
    }

    /**
     * Compute the unique reference of an appointment
     * @param appointment The appointment
     * @return The unique reference of an appointment
     */
    public String computeRefAppointment( Appointment appointment )
    {
        return appointment.getIdAppointment(  ) +
        CryptoService.encrypt( appointment.getIdAppointment(  ) + appointment.getEmail(  ),
            AppPropertiesService.getProperty( PROPERTY_REF_ENCRYPTION_ALGORITHM, CONSTANT_SHA256 ) )
                     .substring( 0, getRefSizeRandomPart(  ) );
    }

    /**
     * Parse a string representing a positive or negative integer
     * @param strNumber The string to parse
     * @return The integer value of the number represented by the string, or 0
     *         if the string could not be parsed
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
        else if ( StringUtils.isNumeric( strNumber ) )
        {
            nNumber = Integer.parseInt( strNumber );
        }

        return nNumber;
    }
}
