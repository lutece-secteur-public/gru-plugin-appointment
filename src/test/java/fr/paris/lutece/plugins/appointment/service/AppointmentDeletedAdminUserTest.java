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
package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.AppointmentTest;
import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * An appointment assigned to a back office user who no longer exists
 */
public class AppointmentDeletedAdminUserTest extends LuteceTestCase
{
    private static final int ID_DELETED_ADMIN_USER = 987654;

    /**
     * The DTO of the appointment gives an empty admin user name, not null: the readers (multiview indexer, lists) cannot handle null
     */
    @Test
    public void testAdminUserNoLongerExists( ) throws Exception
    {
        AppointmentFormDTO form = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( form );
        LocalDate day = LocalDate.now( ).plusDays( 1 );
        Slot slot = SlotService.saveSlot( SlotTest.buildSlot( nIdForm, day.atTime( 10, 0 ), day.atTime( 10, 30 ), 3, 3, 0, 3, Boolean.TRUE, Boolean.TRUE ) );
        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "bh@mdp.fr", "Jean", "Dupont", LocalTime.of( 10, 0 ),
                LocalTime.of( 10, 30 ), 1 );
        int nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        try
        {
            Appointment appointment = AppointmentHome.findByPrimaryKey( nIdAppointment );
            appointment.setIdAdminUser( ID_DELETED_ADMIN_USER );
            AppointmentHome.update( appointment );

            AppointmentDTO dto = AppointmentService.buildAppointmentDTOFromIdAppointment( nIdAppointment );

            assertNotNull( dto.getAdminUser( ) );
            assertEquals( "", dto.getAdminUser( ) );
        }
        finally
        {
            AppointmentHome.delete( nIdAppointment );
            User user = UserHome.findByFirstNameLastNameAndEmail( "Jean", "Dupont", "bh@mdp.fr" );
            if ( user != null )
            {
                UserHome.delete( user.getIdUser( ) );
            }
            FormServiceTest.cleanForm( nIdForm );
        }
    }
}
