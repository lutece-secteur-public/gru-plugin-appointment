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

package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.test.LuteceTestCase;

import java.sql.Date;


public class AppointmentBusinessTest extends LuteceTestCase
{
    private final static int IDAPPOINTMENT1 = 1;
    private final static int IDAPPOINTMENT2 = 2;
    private final static String FIRSTNAME1 = "FirstName1";
    private final static String FIRSTNAME2 = "FirstName2";
    private final static String LASTNAME1 = "LastName1";
    private final static String LASTNAME2 = "LastName2";
    private final static String EMAIL1 = "Email1";
    private final static String EMAIL2 = "Email2";
    private final static String IDUSER1 = "IdUser1";
    private final static String IDUSER2 = "IdUser2";
    private final static int TIMEAPPOINTMENT1 = 1;
    private final static int TIMEAPPOINTMENT2 = 2;
    private final static Date DATEAPPOINTMENT1 = new Date( System.currentTimeMillis( ) );
    private final static Date DATEAPPOINTMENT2 = new Date( System.currentTimeMillis( ) );

    public void testBusiness( )
    {
        // Initialize an object
        Appointment appointment = new Appointment( );
        appointment.setIdAppointment( IDAPPOINTMENT1 );
        appointment.setFirstName( FIRSTNAME1 );
        appointment.setLastName( LASTNAME1 );
        appointment.setEmail( EMAIL1 );
        appointment.setIdUser( IDUSER1 );
        appointment.setTimeAppointment( TIMEAPPOINTMENT1 );
        appointment.setDateAppointment( DATEAPPOINTMENT1 );

        // Create test
        AppointmentHome.create( appointment );
        Appointment appointmentStored = AppointmentHome.findByPrimaryKey( appointment.getIdAppointment( ) );
        assertEquals( appointmentStored.getIdAppointment( ), appointment.getIdAppointment( ) );
        assertEquals( appointmentStored.getFirstName( ), appointment.getFirstName( ) );
        assertEquals( appointmentStored.getLastName( ), appointment.getLastName( ) );
        assertEquals( appointmentStored.getEmail( ), appointment.getEmail( ) );
        assertEquals( appointmentStored.getIdUser( ), appointment.getIdUser( ) );
        assertEquals( appointmentStored.getTimeAppointment( ), appointment.getTimeAppointment( ) );
        assertEquals( appointmentStored.getDateAppointment( ), appointment.getDateAppointment( ) );

        // Update test
        appointment.setIdAppointment( IDAPPOINTMENT2 );
        appointment.setFirstName( FIRSTNAME2 );
        appointment.setLastName( LASTNAME2 );
        appointment.setEmail( EMAIL2 );
        appointment.setIdUser( IDUSER2 );
        appointment.setTimeAppointment( TIMEAPPOINTMENT2 );
        appointment.setDateAppointment( DATEAPPOINTMENT2 );
        AppointmentHome.update( appointment );
        appointmentStored = AppointmentHome.findByPrimaryKey( appointment.getIdAppointment( ) );
        assertEquals( appointmentStored.getIdAppointment( ), appointment.getIdAppointment( ) );
        assertEquals( appointmentStored.getFirstName( ), appointment.getFirstName( ) );
        assertEquals( appointmentStored.getLastName( ), appointment.getLastName( ) );
        assertEquals( appointmentStored.getEmail( ), appointment.getEmail( ) );
        assertEquals( appointmentStored.getIdUser( ), appointment.getIdUser( ) );
        assertEquals( appointmentStored.getTimeAppointment( ), appointment.getTimeAppointment( ) );
        assertEquals( appointmentStored.getDateAppointment( ), appointment.getDateAppointment( ) );

        // List test
        AppointmentHome.getAppointmentsList( );

        // Delete test
        AppointmentHome.remove( appointment.getIdAppointment( ) );
        appointmentStored = AppointmentHome.findByPrimaryKey( appointment.getIdAppointment( ) );
        assertNull( appointmentStored );

    }

}