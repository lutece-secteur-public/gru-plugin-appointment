/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.service.listeners.AdminUserAppointmentRemovalListener;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * The deletion of a back office user is refused while upcoming appointments not cancelled are assigned to the user
 */
public class AdminUserAppointmentRemovalListenerTest extends LuteceTestCase
{
    private static final int ID_ADMIN_USER = 876543;

    @Test
    public void testUpcomingAppointmentRefusesTheDeletion( )
    {
        AdminUserAppointmentRemovalListener listener = new AdminUserAppointmentRemovalListener( );
        String strIdAdminUser = Integer.toString( ID_ADMIN_USER );

        Form form = FormTest.buildForm1( );
        FormHome.create( form );
        User user = UserTest.buildUser( Constants.GUID_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.EMAIL_1, Constants.PHONE_NUMBER_1 );
        UserHome.create( user );
        LocalDateTime future = LocalDateTime.now( ).plusDays( 10 ).withNano( 0 );
        Slot pastSlot = SlotTest.buildSlot( form.getIdForm( ), Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, 2, 2, 0, 2, true, true );
        SlotHome.create( pastSlot );
        Slot futureSlot = SlotTest.buildSlot( form.getIdForm( ), future, future.plusMinutes( 30 ), 2, 2, 0, 2, true, true );
        SlotHome.create( futureSlot );

        Appointment past = createAppointment( user, pastSlot );
        Appointment upcoming = null;
        try
        {
            assertTrue( listener.canBeRemoved( strIdAdminUser ), "a past appointment does not refuse the deletion" );

            upcoming = createAppointment( user, futureSlot );
            assertFalse( listener.canBeRemoved( strIdAdminUser ) );
            String strMessage = listener.getRemovalRefusedMessage( strIdAdminUser, Locale.ENGLISH );
            assertTrue( strMessage.startsWith( "1 upcoming appointment" ), strMessage );

            upcoming.setIsCancelled( true );
            AppointmentHome.update( upcoming );
            assertTrue( listener.canBeRemoved( strIdAdminUser ), "a cancelled appointment does not refuse the deletion" );
            assertTrue( listener.canBeRemoved( "abc" ) );
        }
        finally
        {
            AppointmentHome.delete( past.getIdAppointment( ) );
            if ( upcoming != null )
            {
                AppointmentHome.delete( upcoming.getIdAppointment( ) );
            }
            SlotHome.delete( pastSlot.getIdSlot( ) );
            SlotHome.delete( futureSlot.getIdSlot( ) );
            FormHome.delete( form.getIdForm( ) );
            UserHome.delete( user.getIdUser( ) );
        }
    }

    private Appointment createAppointment( User user, Slot slot )
    {
        AppointmentSlot appointmentSlot = new AppointmentSlot( );
        appointmentSlot.setIdSlot( slot.getIdSlot( ) );
        appointmentSlot.setNbPlaces( 1 );

        Appointment appointment = new Appointment( );
        appointment.setIdUser( user.getIdUser( ) );
        appointment.setIdAdminUser( ID_ADMIN_USER );
        appointment.addSlot( slot );
        appointment.setListAppointmentSlot( new ArrayList<>( ) );
        appointment.getListAppointmentSlot( ).add( appointmentSlot );
        AppointmentHome.create( appointment );
        return appointment;
    }
}
