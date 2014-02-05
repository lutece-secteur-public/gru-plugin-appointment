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
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import org.apache.commons.lang.StringUtils;

import java.util.List;


/**
 * Service to manage appointment forms
 */
public class AppointmentSlotService
{
    private static final String BEAN_NAME = "appointment.appointmentSlotService";
    private static volatile AppointmentSlotService _instance;

    /**
     * Get the instance of the service
     * @return The instance of the service
     */
    public static AppointmentSlotService getInstance(  )
    {
        if ( _instance == null )
        {
            _instance = SpringContextService.getBean( BEAN_NAME );
        }

        return _instance;
    }

    /**
     * Compute slots for a given day, and create them
     * @param day The day to create slots of. The day must have been inserted in
     *            the database.
     * @param form The form associated with the day.
     */
    public void computeAndCreateSlotsForDay( AppointmentDay day, AppointmentForm form )
    {
        List<AppointmentSlot> listSlots;

        if ( ( form.getOpeningHour(  ) == day.getOpeningHour(  ) ) &&
                ( form.getOpeningMinutes(  ) == day.getOpeningMinutes(  ) ) &&
                ( form.getClosingHour(  ) == day.getClosingHour(  ) ) &&
                ( form.getClosingMinutes(  ) == day.getClosingMinutes(  ) ) &&
                ( form.getDurationAppointments(  ) == day.getAppointmentDuration(  ) ) )
        {
            int nDayOfWeek = AppointmentService.getService(  ).getDayOfWeek( day.getDate(  ) );
            listSlots = AppointmentSlotHome.findByIdFormAndDayOfWeek( form.getIdForm(  ), nDayOfWeek );
        }
        else
        {
            listSlots = AppointmentService.getService(  ).computeDaySlots( day );
        }

        for ( AppointmentSlot slot : listSlots )
        {
            slot.setIdDay( day.getIdDay(  ) );
            slot.setNbPlaces( day.getPeoplePerAppointment(  ) );
            AppointmentSlotHome.create( slot );
        }
    }

    /**
     * Compute slots for a given day, and create them
     * @param appointmentForm The form to create slots of. The form must have
     *            been inserted in the database.
     */
    public void computeAndCreateSlotsForForm( AppointmentForm appointmentForm )
    {
        List<AppointmentDay> listAppointmentDay = AppointmentService.getService(  ).computeDayList( appointmentForm );

        for ( AppointmentDay day : listAppointmentDay )
        {
            for ( AppointmentSlot slot : day.getListSlots(  ) )
            {
                AppointmentSlotHome.create( slot );
            }
        }
    }

    /**
     * Check if a day has changed its appointment duration or has been enabled
     * or disabled, and update slots to be compliant with new parameters of the
     * day.
     * @param day The day with new values of attributes. New values should be
     *            saved in the database before this method is called.
     * @param dayFromDb The day with old values of attributes.
     * @param form The form associated with the day.
     * @return True if some slots was modified, false otherwise
     */
    public boolean checkForDayModification( AppointmentDay day, AppointmentDay dayFromDb, AppointmentForm form )
    {
        // If the appointment duration or the opening or closing time of the day has changed, we reinitialized slots
        if ( ( dayFromDb.getAppointmentDuration(  ) != day.getAppointmentDuration(  ) ) ||
                ( ( ( dayFromDb.getOpeningHour(  ) * 60 ) + dayFromDb.getOpeningMinutes(  ) ) != ( ( day.getOpeningHour(  ) * 60 ) +
                day.getOpeningMinutes(  ) ) ) ||
                ( ( ( dayFromDb.getClosingHour(  ) * 60 ) + dayFromDb.getClosingMinutes(  ) ) != ( ( day.getClosingHour(  ) * 60 ) +
                day.getClosingMinutes(  ) ) ) )
        {
            AppointmentSlotHome.deleteByIdDay( day.getIdDay(  ) );
            AppointmentSlotService.getInstance(  ).computeAndCreateSlotsForDay( day, form );

            return true;
        }

        if ( dayFromDb.getIsOpen(  ) != day.getIsOpen(  ) )
        {
            if ( day.getIsOpen(  ) )
            {
                // If the day has been opened, we create slots 
                computeAndCreateSlotsForDay( day, form );
            }
            else
            {
                // If the day has been closed, we remove slots
                AppointmentSlotHome.deleteByIdDay( day.getIdDay(  ) );
            }

            return true;
        }

        //        if ( day.getDate(  ).getTime(  ) != dayFromDb.getDate(  ).getTime(  ) )
        //        {
        //            // If the date changed, we must update the day of week of each slot.
        //            int nDayOfWeek = AppointmentService.getService(  ).getDayOfWeek( day.getDate(  ) );
        //            int nOldDayOfWeek = AppointmentService.getService(  ).getDayOfWeek( dayFromDb.getDate(  ) );
        //
        //            if ( nDayOfWeek != nOldDayOfWeek )
        //            {
        //                List<AppointmentSlot> listSlots = AppointmentSlotHome.findByIdDay( day.getIdDay(  ) );
        //
        //                for ( AppointmentSlot slot : listSlots )
        //                {
        //                    slot.setDayOfWeek( nDayOfWeek );
        //                    AppointmentSlotHome.update( slot );
        //                }
        //                bRes = true;
        //            }
        //        }
        if ( day.getPeoplePerAppointment(  ) != dayFromDb.getPeoplePerAppointment(  ) )
        {
            List<AppointmentSlot> listSlots = AppointmentSlotHome.findByIdDay( day.getIdDay(  ) );

            for ( AppointmentSlot slot : listSlots )
            {
                slot.setNbPlaces( day.getPeoplePerAppointment(  ) );
                AppointmentSlotHome.update( slot );
            }

            return true;
        }

        return false;
    }

    /**
     * Check if a form has changed its appointment duration or has enabled or
     * disabled a day of the week, and update slots to be compliant with new
     * parameters of the form.
     * @param appointmentForm The form with new values of attributes. New values
     *            should be saved in the database before this method is called.
     * @param formFromDb The form with old values of attributes.
     * @return True if some slots was modified, false otherwise
     */
    public boolean checkForFormModification( AppointmentForm appointmentForm, AppointmentForm formFromDb )
    {
        // If the duration of appointments, the starting time or the ending time has changed  we recreate appointments slots associated with this form
        if ( ( formFromDb.getDurationAppointments(  ) != appointmentForm.getDurationAppointments(  ) ) ||
                !StringUtils.equals( formFromDb.getTimeStart(  ), appointmentForm.getTimeStart(  ) ) ||
                !StringUtils.equals( formFromDb.getTimeEnd(  ), appointmentForm.getTimeEnd(  ) ) )
        {
            AppointmentSlotHome.deleteByIdForm( appointmentForm.getIdForm(  ) );

            List<AppointmentDay> listAppointmentDay = AppointmentService.getService(  ).computeDayList( appointmentForm );

            for ( AppointmentDay day : listAppointmentDay )
            {
                for ( AppointmentSlot slot : day.getListSlots(  ) )
                {
                    AppointmentSlotHome.create( slot );
                }
            }

            return true;
        }

        boolean[] bArrayDayOpenedfromDb = 
            {
                formFromDb.getIsOpenMonday(  ), formFromDb.getIsOpenTuesday(  ), formFromDb.getIsOpenWednesday(  ),
                formFromDb.getIsOpenThursday(  ), formFromDb.getIsOpenFriday(  ), formFromDb.getIsOpenSaturday(  ),
                formFromDb.getIsOpenSunday(  ),
            };
        boolean[] bArrayDayOpened = 
            {
                appointmentForm.getIsOpenMonday(  ), appointmentForm.getIsOpenTuesday(  ),
                appointmentForm.getIsOpenWednesday(  ), appointmentForm.getIsOpenThursday(  ),
                appointmentForm.getIsOpenFriday(  ), appointmentForm.getIsOpenSaturday(  ),
                appointmentForm.getIsOpenSunday(  ),
            };
        boolean bHasModifications = false;

        for ( int i = 0; i < bArrayDayOpened.length; i++ )
        {
            if ( bArrayDayOpenedfromDb[i] != bArrayDayOpened[i] )
            {
                if ( bArrayDayOpened[i] )
                {
                    AppointmentDay day = AppointmentService.getService(  ).getAppointmentDayFromForm( appointmentForm );
                    day.setIsOpen( true );

                    List<AppointmentSlot> listSlots = AppointmentService.getService(  ).computeDaySlots( day, i + 1 );

                    for ( AppointmentSlot slot : listSlots )
                    {
                        AppointmentSlotHome.create( slot );
                    }
                }
                else
                {
                    AppointmentSlotHome.deleteByIdFormAndDayOfWeek( appointmentForm.getIdForm(  ), i + 1 );
                }

                bHasModifications = true;
            }
        }

        return bHasModifications;
    }
}
