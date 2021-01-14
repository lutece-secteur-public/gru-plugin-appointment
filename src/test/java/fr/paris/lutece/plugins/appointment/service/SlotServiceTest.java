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

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

public class SlotServiceTest extends LuteceTestCase
{
    private LocalDate _nextMonday = LocalDate.now( ).with( TemporalAdjusters.next( DayOfWeek.MONDAY ) );
    private LocalDate _sundayTwoWeeks = _nextMonday.plusDays( 13 );
    private LocalTime _startSlot = LocalTime.of( 10, 0 );
    private LocalTime _endSlot = LocalTime.of( 10, 30 );

    // Check that there are 180 open slots from the 3/12/2018 to the 14/12/2018
    // With open days from Monday to Friday
    public void testOpenSlots( )
    {

        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setDateStartValidity( Date.valueOf( _nextMonday ) );
        appointmentForm.setDateStartValidity( Date.valueOf( _sundayTwoWeeks ) );

        appointmentForm.setIsOpenMonday( Boolean.TRUE );
        appointmentForm.setIsOpenTuesday( Boolean.TRUE );
        appointmentForm.setIsOpenWednesday( Boolean.TRUE );
        appointmentForm.setIsOpenThursday( Boolean.TRUE );
        appointmentForm.setIsOpenFriday( Boolean.TRUE );
        appointmentForm.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm.setIsOpenSunday( Boolean.FALSE );

        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        // Get all the week definitions
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( nIdForm );

        List<Slot> listSlots = SlotService.buildListSlot( nIdForm, mapWeekDefinition, _nextMonday, _sundayTwoWeeks );

        assertEquals( 180, listSlots.stream( ).filter( s -> s.getIsOpen( ) ).collect( Collectors.toList( ) ).size( ) );
        FormServiceTest.cleanForm( nIdForm );
    }

    public void testOpenSlotsWithSpecificSlotsClosed( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setDateStartValidity( Date.valueOf( _nextMonday ) );
        appointmentForm.setDateStartValidity( Date.valueOf( _sundayTwoWeeks ) );

        appointmentForm.setIsOpenMonday( Boolean.TRUE );
        appointmentForm.setIsOpenTuesday( Boolean.TRUE );
        appointmentForm.setIsOpenWednesday( Boolean.TRUE );
        appointmentForm.setIsOpenThursday( Boolean.TRUE );
        appointmentForm.setIsOpenFriday( Boolean.TRUE );
        appointmentForm.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm.setIsOpenSunday( Boolean.FALSE );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        // Get all the week definitions
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( nIdForm );

        Slot slotSpecificClosed1 = SlotTest.buildSlot( nIdForm, _nextMonday.atTime( _startSlot ), _nextMonday.atTime( _endSlot ), 1, 1, 0, 1, Boolean.FALSE,
                Boolean.TRUE );
        slotSpecificClosed1 = SlotService.saveSlot( slotSpecificClosed1 );

        Slot slotSpecificClosed2 = SlotTest.buildSlot( nIdForm, _nextMonday.plusDays( 1 ).atTime( _startSlot ), _nextMonday.plusDays( 1 ).atTime( _endSlot ), 1,
                1, 0, 1, Boolean.FALSE, Boolean.TRUE );
        slotSpecificClosed2 = SlotService.saveSlot( slotSpecificClosed2 );

        Slot slotSpecificClosed3 = SlotTest.buildSlot( nIdForm, _nextMonday.plusDays( 2 ).atTime( _startSlot ), _nextMonday.plusDays( 2 ).atTime( _endSlot ), 1,
                1, 0, 1, Boolean.FALSE, Boolean.TRUE );
        slotSpecificClosed3 = SlotService.saveSlot( slotSpecificClosed3 );

        List<Slot> listSlots = SlotService.buildListSlot( nIdForm, mapWeekDefinition, _nextMonday, _sundayTwoWeeks );

        assertEquals( 177, listSlots.stream( ).filter( s -> s.getIsOpen( ) ).collect( Collectors.toList( ) ).size( ) );
        FormServiceTest.cleanForm( nIdForm );
    }

    public void testOpenSlotsWithSpecificLargeSlots( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setDateStartValidity( Date.valueOf( _nextMonday ) );
        appointmentForm.setDateStartValidity( Date.valueOf( _sundayTwoWeeks ) );

        appointmentForm.setIsOpenMonday( Boolean.TRUE );
        appointmentForm.setIsOpenTuesday( Boolean.TRUE );
        appointmentForm.setIsOpenWednesday( Boolean.TRUE );
        appointmentForm.setIsOpenThursday( Boolean.TRUE );
        appointmentForm.setIsOpenFriday( Boolean.TRUE );
        appointmentForm.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm.setIsOpenSunday( Boolean.FALSE );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        // Get all the week definitions
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( nIdForm );

        Slot slotSpecific1 = SlotTest.buildSlot( nIdForm, _nextMonday.atTime( _startSlot ), _nextMonday.atTime( 11, 0 ), 1, 1, 0, 1, Boolean.TRUE,
                Boolean.TRUE );
        slotSpecific1 = SlotService.saveSlot( slotSpecific1 );

        Slot slotSpecific2 = SlotTest.buildSlot( nIdForm, _nextMonday.plusDays( 1 ).atTime( _startSlot ), _nextMonday.plusDays( 1 ).atTime( 11, 30 ), 1, 1, 0,
                1, Boolean.TRUE, Boolean.TRUE );
        slotSpecific2 = SlotService.saveSlot( slotSpecific2 );

        List<Slot> listSlots = SlotService.buildListSlot( nIdForm, mapWeekDefinition, _nextMonday, _sundayTwoWeeks );

        assertEquals( 177, listSlots.stream( ).filter( s -> s.getIsOpen( ) ).collect( Collectors.toList( ) ).size( ) );
        FormServiceTest.cleanForm( nIdForm );
    }
}
