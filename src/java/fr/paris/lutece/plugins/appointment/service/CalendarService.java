/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.sql.Date;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


/**
 * Service to manage calendars
 */
public class CalendarService
{
    /**
     * Name of the bean of the service
     */
    public static final String BEAN_NAME = "appointment.calendarService";
    private static final String CONSTANT_H = "h";
    private static final String CONSTANT_ZERO = "0";
    private static final int CONSTANT_MINUTES_IN_HOUR = 60;
    private static final long CONSTANT_MILISECONDS_IN_DAY = 86400000L;
    private static volatile CalendarService _instance;

    /**
     * Get an instance of the service
     * @return An instance of the service
     */
    public static CalendarService getService(  )
    {
        if ( _instance == null )
        {
            _instance = SpringContextService.getBean( BEAN_NAME );
        }

        return _instance;
    }

    /**
     * Get the list of days with the list of slots for a given form
     * @param form The form to get days of. Opening and closing hour of the form
     *            are updated by this method
     * @param nOffsetWeeks The offset of the week to get
     * @param bLoadSlotsFromDb True to load slots from the database, false to
     *            compute them
     * @return The list of days
     */
    public List<AppointmentDay> getDayListforCalendar( AppointmentForm form, int nOffsetWeeks, boolean bLoadSlotsFromDb )
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

        List<AppointmentDay> listDaysFound = AppointmentDayHome.getDaysBetween( form.getIdForm(  ), dateMin, dateMax );

        String[] strOpeningTime = form.getTimeStart(  ).split( CONSTANT_H );
        String[] strClosingTime = form.getTimeEnd(  ).split( CONSTANT_H );
        int nOpeningHour = Integer.parseInt( strOpeningTime[0] );
        int nOpeningMinutes = Integer.parseInt( strOpeningTime[1] );
        int nClosingHour = Integer.parseInt( strClosingTime[0] );
        int nClosingMinutes = Integer.parseInt( strClosingTime[1] );
        form.setOpeningHour( nOpeningHour );
        form.setOpeningMinutes( nOpeningMinutes );
        form.setClosingHour( nClosingHour );
        form.setClosingMinutes( nClosingMinutes );

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
                day = getAppointmentDayFromForm( form, nOpeningHour, nOpeningMinutes, nClosingHour, nClosingMinutes );
                day.setDate( new Date( lMilisecDate ) );
                day.setIsOpen( bArrayIsOpen[i] );
                day.setIdForm( form.getIdForm(  ) );
            }

            if ( day.getIsOpen(  ) )
            {
                if ( bLoadSlotsFromDb )
                {
                    if ( day.getIdDay(  ) > 0 )
                    {
                        day.setListSlots( AppointmentSlotHome.findByIdDay( day.getIdDay(  ) ) );
                    }
                    else
                    {
                        day.setListSlots( AppointmentSlotHome.findByIdFormAndDayOfWeek( form.getIdForm(  ), i + 1 ) );
                    }
                }
                else
                {
                    day.setListSlots( computeDaySlots( day ) );
                }
            }
            else
            {
                day.setListSlots( new ArrayList<AppointmentSlot>(  ) );
            }

            listDays.add( day );
            lMilisecDate += CONSTANT_MILISECONDS_IN_DAY;
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
            return new ArrayList<AppointmentSlot>(  );
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
                slot.setNbFreePlaces( day.getPeoplePerAppointment(  ) );
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
            StringBuilder sbTime = new StringBuilder(  );

            if ( nStartingHour < 10 )
            {
                sbTime.append( CONSTANT_ZERO );
            }

            sbTime.append( nStartingHour );
            sbTime.append( CONSTANT_H );

            if ( nStartingMinutes < 10 )
            {
                sbTime.append( CONSTANT_ZERO );
            }

            sbTime.append( nStartingMinutes );
            listTimes.add( sbTime.toString(  ) );
            nStartingMinutes = nStartingMinutes + nAppointmentDuration;
            nStartingHour = nStartingHour + ( nStartingMinutes / CONSTANT_MINUTES_IN_HOUR );
            nStartingMinutes = nStartingMinutes % CONSTANT_MINUTES_IN_HOUR;
        }

        return listTimes;
    }

    /**
     * Get an appointment day from an appointment form. The date of the day is
     * not initialized.
     * @param appointmentForm The form
     * @return The day
     */
    public AppointmentDay getAppointmentDayFromForm( AppointmentForm appointmentForm )
    {
        return getAppointmentDayFromForm( appointmentForm, appointmentForm.getOpeningHour(  ),
            appointmentForm.getOpeningMinutes(  ), appointmentForm.getClosingHour(  ),
            appointmentForm.getClosingMinutes(  ) );
    }

    /**
     * Creates a day from an appointment form.
     * @param form The form
     * @param nOpeningHour The opening hour of the day
     * @param nOpeningMinutes The opening minutes of the day
     * @param nClosingHour The closing hour of the day
     * @param nClosingMinutes The closing minutes of the day
     * @return The day
     */
    private AppointmentDay getAppointmentDayFromForm( AppointmentForm form, int nOpeningHour, int nOpeningMinutes,
        int nClosingHour, int nClosingMinutes )
    {
        AppointmentDay day = new AppointmentDay(  );
        day.setOpeningHour( nOpeningHour );
        day.setOpeningMinutes( nOpeningMinutes );
        day.setClosingHour( nClosingHour );
        day.setClosingMinutes( nClosingMinutes );
        day.setAppointmentDuration( form.getDurationAppointments(  ) );
        day.setPeoplePerAppointment( form.getPeoplePerAppointment(  ) );

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
}
