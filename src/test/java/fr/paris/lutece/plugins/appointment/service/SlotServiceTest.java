/*
 * Copyright (c) 2002-2021, City of Paris
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

public class SlotServiceTest extends LuteceTestCase
{
    private AppointmentFormDTO appointmentForm;
    private int nIdForm;

    // Check that there are 180 open slots from the 3/12/2022 to the 14/12/2022
    // With open days from Monday to Friday
    public void testOpenSlots( )
    {
        // Get all the week definitions
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( this.nIdForm );
        List<Slot> listSlots = SlotService.buildListSlot( nIdForm, mapWeekDefinition, Constants.DATE_14, Constants.DATE_15 );

        assertEquals( 180, listSlots.stream( ).filter( s -> s.getIsOpen( ) ).collect( Collectors.toList( ) ).size( ) );

    }

    public void testOpenSlotsWithSpecificSlotsClosed( )
    {
        // Get all the week definitions
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( this.nIdForm );

        Slot slotSpecificClosed1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_12, Constants.ENDING_DATE_12, Constants.NB_REMAINING_PLACES_1, Constants.NB_REMAINING_PLACES_1,
                0, Constants.NB_REMAINING_PLACES_1, Boolean.FALSE, Boolean.TRUE );
        slotSpecificClosed1 = SlotService.saveSlot( slotSpecificClosed1 );

        Slot slotSpecificClosed2 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_13, Constants.ENDING_DATE_13, Constants.NB_REMAINING_PLACES_1, Constants.NB_REMAINING_PLACES_1,
                0, Constants.NB_REMAINING_PLACES_1, Boolean.FALSE, Boolean.TRUE );
        slotSpecificClosed2 = SlotService.saveSlot( slotSpecificClosed2 );

        Slot slotSpecificClosed3 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_14, Constants.ENDING_DATE_14, Constants.NB_REMAINING_PLACES_1, Constants.NB_REMAINING_PLACES_1,
                0, Constants.NB_REMAINING_PLACES_1, Boolean.FALSE, Boolean.TRUE );
        slotSpecificClosed3 = SlotService.saveSlot( slotSpecificClosed3 );

        List<Slot> listSlots = SlotService.buildListSlot( this.nIdForm, mapWeekDefinition,  Constants.DATE_14, Constants.DATE_15 );

        assertEquals( 177, listSlots.stream( ).filter( s -> s.getIsOpen( ) ).collect( Collectors.toList( ) ).size( ) );

    }

    public void testOpenSlotsWithSpecificLargeSlots( )
    {
        // Get all the week definitions
        HashMap<LocalDate, WeekDefinition> mapWeekDefinition = WeekDefinitionService.findAllWeekDefinition( this.nIdForm );

        Slot slotSpecific1 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_12, Constants.STARTING_DATE_1, Constants.NB_REMAINING_PLACES_1, Constants.NB_REMAINING_PLACES_1,
                0, Constants.NB_REMAINING_PLACES_1, Boolean.TRUE, Boolean.TRUE );
        slotSpecific1 = SlotService.saveSlot( slotSpecific1 );

        Slot slotSpecific2 = SlotTest.buildSlot( this.nIdForm, Constants.STARTING_DATE_15, Constants.ENDING_DATE_15, Constants.NB_REMAINING_PLACES_1, Constants.NB_REMAINING_PLACES_1,
                0, Constants.NB_REMAINING_PLACES_1,Boolean.TRUE, Boolean.TRUE );
        slotSpecific2 = SlotService.saveSlot( slotSpecific2 );

        List<Slot> listSlots = SlotService.buildListSlot( this.nIdForm, mapWeekDefinition, Constants.DATE_14, Constants.DATE_15 );

        assertEquals( 177, listSlots.stream( ).filter( s -> s.getIsOpen( ) ).collect( Collectors.toList( ) ).size( ) );

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.appointmentForm = FormServiceTest.buildAppointmentForm( );
        this.appointmentForm.setDateStartValidity( Date.valueOf( Constants.DATE_16 ) );
        this.appointmentForm.setDateStartValidity( Date.valueOf( Constants.DATE_17) );
        this.appointmentForm.setIsOpenMonday( Boolean.TRUE );
        this.appointmentForm.setIsOpenTuesday( Boolean.TRUE );
        this.appointmentForm.setIsOpenWednesday( Boolean.TRUE );
        this.appointmentForm.setIsOpenThursday( Boolean.TRUE );
        this.appointmentForm.setIsOpenFriday( Boolean.TRUE );
        this.appointmentForm.setIsOpenSaturday( Boolean.FALSE );
        this.appointmentForm.setIsOpenSunday( Boolean.FALSE );

        this.nIdForm = FormService.createAppointmentForm( this.appointmentForm );
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //delete all the forms left over from tests
        for (Form f : FormService.findAllForms()) {
            FormService.removeForm(f.getIdForm());
        }
        this.nIdForm = 0;
        this.appointmentForm = null;
    }


}