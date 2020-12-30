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

import java.time.LocalDate;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

public class ReservationRuleServiceTest extends LuteceTestCase
{

    public void testFindReservationRuleByIdFormAndClosestToDateOfApply( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setName("appointment_form");
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setDurationAppointments( 20 );
        LocalDate dateOfModification = LocalDate.parse( "2028-06-20" );
        FormService.updateGlobalParameters( appointmentForm2 );

        AppointmentFormDTO appointmentForm3 = FormServiceTest.buildAppointmentForm( );
        appointmentForm3.setName("appointment_form");
        appointmentForm3.setIdForm( nIdForm );
        appointmentForm3.setDurationAppointments( 10 );
        LocalDate dateOfModification2 = LocalDate.parse( "2028-06-22" );
        FormService.updateGlobalParameters( appointmentForm3 );

        assertEquals( dateOfModification,
                ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, LocalDate.parse( "2028-06-21" ) ) );

        FormServiceTest.cleanForm( nIdForm );
    }

}
