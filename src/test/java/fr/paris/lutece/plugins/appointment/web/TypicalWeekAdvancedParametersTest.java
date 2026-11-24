/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.appointment.web;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.service.ReservationRuleService;
import fr.paris.lutece.plugins.appointment.service.WorkingDayService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Saving the advanced parameters of a typical week keeps the time slots customised one by one when the grid of time slots does not change
 */
public class TypicalWeekAdvancedParametersTest extends LuteceTestCase
{
    private int _nIdForm;
    private int _nIdRule;
    private TimeSlot _timeSlotClosed;
    private TimeSlot _timeSlotCustomCapacity;
    private TimeSlot _timeSlotDefault;

    @BeforeEach
    public void createForm( )
    {
        AppointmentFormDTO form = FormServiceTest.buildAppointmentForm( );
        _nIdForm = FormService.createAppointmentForm( form );
        _nIdRule = ReservationRuleService.findListReservationRule( _nIdForm ).get( 0 ).getIdReservationRule( );
        List<TimeSlot> listTimeSlot = WorkingDayService.findListWorkingDayByWeekDefinitionRule( _nIdRule ).stream( ).map( WorkingDay::getListTimeSlot )
                .filter( list -> list.size( ) >= 3 ).findFirst( ).orElseThrow( );
        _timeSlotClosed = listTimeSlot.get( 0 );
        _timeSlotClosed.setIsOpen( false );
        TimeSlotHome.update( _timeSlotClosed );
        _timeSlotCustomCapacity = listTimeSlot.get( 1 );
        _timeSlotCustomCapacity.setMaxCapacity( 7 );
        TimeSlotHome.update( _timeSlotCustomCapacity );
        _timeSlotDefault = listTimeSlot.get( 2 );
    }

    @AfterEach
    public void cleanUp( )
    {
        FormServiceTest.cleanForm( _nIdForm );
    }

    /**
     * Only the capacity changes: the closed time slot stays closed, the customised capacity is kept, the other time slots get the new capacity
     */
    @Test
    public void testCapacityOnlyKeepsTheCustomisedTimeSlots( )
    {
        submitAdvancedParameters( "09:00", "18:00", 4 );

        TimeSlot closed = TimeSlotHome.findByPrimaryKey( _timeSlotClosed.getIdTimeSlot( ) );
        assertNotNull( closed );
        assertFalse( closed.getIsOpen( ) );
        assertEquals( 7, TimeSlotHome.findByPrimaryKey( _timeSlotCustomCapacity.getIdTimeSlot( ) ).getMaxCapacity( ) );
        assertEquals( 4, TimeSlotHome.findByPrimaryKey( _timeSlotDefault.getIdTimeSlot( ) ).getMaxCapacity( ) );
    }

    /**
     * The opening hours change: the time slots are generated again, all open with the capacity of the typical week
     */
    @Test
    public void testNewOpeningHoursGenerateTheTimeSlotsAgain( )
    {
        submitAdvancedParameters( "09:00", "17:00", 4 );

        assertNull( TimeSlotHome.findByPrimaryKey( _timeSlotClosed.getIdTimeSlot( ) ) );
        assertTrue( WorkingDayService.findListWorkingDayByWeekDefinitionRule( _nIdRule ).stream( ).flatMap( day -> day.getListTimeSlot( ).stream( ) )
                .allMatch( timeSlot -> timeSlot.getIsOpen( ) && timeSlot.getMaxCapacity( ) == 4 ) );
    }

    private void submitAdvancedParameters( String strTimeStart, String strTimeEnd, int nMaxCapacity )
    {
        // the action validates the bean (no Bean Validation provider in the unit tests), then calls this service
        AppointmentFormDTO appointmentForm = FormService.buildAppointmentForm( _nIdForm, _nIdRule );
        appointmentForm.setTimeStart( strTimeStart );
        appointmentForm.setTimeEnd( strTimeEnd );
        appointmentForm.setMaxCapacityPerSlot( nMaxCapacity );
        ReservationRuleService.updateAdvancedParameters( appointmentForm );
    }
}
