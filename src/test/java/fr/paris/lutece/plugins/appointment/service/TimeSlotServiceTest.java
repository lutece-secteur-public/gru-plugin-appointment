/*
 * Copyright (c) 2002-2020, City of Paris
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

import java.time.LocalTime;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

public class TimeSlotServiceTest extends LuteceTestCase
{

    /**
     * Find the next time slots of a given time slot
     */
    public void testFindListTimeSlotAfterThisTimeSlot( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setTimeStart( "09:00" );
        appointmentForm.setTimeEnd( "18:00" );
        appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinitionRule( listWeekDefinition.get( 0 ).getIdWeekDefinition( ) );
        List<TimeSlot> listTimeSlot = TimeSlotService.findListTimeSlotByWorkingDay( listWorkingDay.get( 0 ).getIdWorkingDay( ) );

        TimeSlot timeSlot = listTimeSlot.stream( ).filter( t -> t.getStartingTime( ).equals( LocalTime.parse( "17:00" ) ) ).findFirst( ).get( );

        List<TimeSlot> listNextTimeSlots = TimeSlotService.findListTimeSlotAfterThisTimeSlot( timeSlot );

        assertEquals( 1, listNextTimeSlots.size( ) );
        assertEquals( LocalTime.parse( "17:30" ), listNextTimeSlots.get( 0 ).getStartingTime( ) );

        FormServiceTest.cleanForm( nIdForm );
    }

    /**
     * Return an ordered and filtered list of time slots after a given time
     */
    public void testGetNextTimeSlotsInAListOfTimeSlotAfterALocalTime( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setTimeStart( "09:00" );
        appointmentForm.setTimeEnd( "18:00" );
        appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinitionRule( listWeekDefinition.get( 0 ).getIdWeekDefinition( ) );
        List<TimeSlot> listTimeSlot = TimeSlotService.findListTimeSlotByWorkingDay( listWorkingDay.get( 0 ).getIdWorkingDay( ) );

        List<TimeSlot> listNextTimeSlots = TimeSlotService.getNextTimeSlotsInAListOfTimeSlotAfterALocalTime( listTimeSlot, LocalTime.parse( "17:10" ) );

        assertEquals( 1, listNextTimeSlots.size( ) );
        FormServiceTest.cleanForm( nIdForm );
    }

    /**
     * Returns the time slot in a list of time slot with the given starting time
     */
    public void testGetTimeSlotInListOfTimeSlotWithStartingTime( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setTimeStart( "09:00" );
        appointmentForm.setTimeEnd( "18:00" );
        appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinitionRule( listWeekDefinition.get( 0 ).getIdReservationRule( ) );
        List<TimeSlot> listTimeSlot = TimeSlotService.findListTimeSlotByWorkingDay( listWorkingDay.get( 0 ).getIdWorkingDay( ) );

        assertEquals( LocalTime.parse( "17:30" ),
                TimeSlotService.getTimeSlotInListOfTimeSlotWithStartingTime( listTimeSlot, LocalTime.parse( "17:00" ) ).getEndingTime( ) );
        FormServiceTest.cleanForm( nIdForm );
    }
}
