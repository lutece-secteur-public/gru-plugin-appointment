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
package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the Appointment
 *
 * @author Laurent Payen
 *
 */
public final class AppointmentTest extends LuteceTestCase
{
    private List<Integer> userIds = new ArrayList<>();
    private List<Integer> slotIds = new ArrayList<>();
    private Form form;
    private User user;
    private Slot slotOne;

    /**
     * Test method for the Appointment (CRUD)
     */
    public void testAppointment( )
    {
        // Initialize a Appointment
        Appointment appointment = new Appointment( );
        appointment.setIdUser( this.user.getIdUser( ) );
        appointment.setIdSlot( this.slotOne.getIdSlot( ) );
        // Create the Appointment in database
        AppointmentHome.create( appointment );
        // Find the Appointment created in database
        Appointment appointmentStored = AppointmentHome.findByPrimaryKey( appointment.getIdAppointment( ) );
        // Check Asserts
        checkAsserts( appointmentStored, appointment );

        // No possible update
        // An appointment is linked to a User and a Slot
        // It will be a nonsense to update the foreign keys (User or Slot).

        // Delete the appointment
        AppointmentHome.delete( appointment.getIdAppointment( ) );
        appointmentStored = AppointmentHome.findByPrimaryKey( appointment.getIdAppointment( ) );
        // Check the Appointment has been removed from database
        assertNull( appointmentStored );

    }

    /**
     * Test the delete cascade
     */
    public void testDeleteCascade( )
    {
        // Initialize a Appointment
        Appointment appointment = new Appointment( );
        appointment.setIdUser( this.user.getIdUser( ) );
        appointment.setIdSlot( this.slotOne.getIdSlot( ) );
        // Create the Appointment in database
        AppointmentHome.create( appointment );
        // Find the Appointment created in database
        Appointment appointmentStored = AppointmentHome.findByPrimaryKey( appointment.getIdAppointment( ) );
        assertNotNull( appointmentStored );
        // Delete the form and by cascade the appointment
        FormHome.delete( this.form.getIdForm( ) );
        appointmentStored = AppointmentHome.findByPrimaryKey( appointment.getIdAppointment( ) );
        // Check the Appointment has been removed from database
        assertNull( appointmentStored );

    }

    /**
     * Test findByIdUser method
     */
    public void testFindByIdUser( )
    {
        Slot slot2 = SlotTest.buildSlot( form.getIdForm( ), Constants.STARTING_DATE_2, Constants.ENDING_DATE_2, Constants.NB_REMAINING_PLACES_2,
                Constants.NB_REMAINING_PLACES_2, 0, Constants.NB_REMAINING_PLACES_2, Boolean.TRUE, Boolean.TRUE );
        SlotHome.create( slot2 );

        this.slotIds.add(slot2.getIdSlot());

        // Initialize a fist Appointment
        Appointment appointment1 = new Appointment( );
        appointment1.setIdUser( this.user.getIdUser( ) );
        appointment1.setIdSlot( this.slotOne.getIdSlot( ) );
        // Create the Appointment in database
        AppointmentHome.create( appointment1 );

        // Initialize a 2nd Appointment
        Appointment appointment2 = new Appointment( );
        appointment2.setIdUser( this.user.getIdUser( ) );
        appointment2.setIdSlot( slot2.getIdSlot( ) );
        // Create the Appointment in database
        AppointmentHome.create( appointment2 );
        // Find the Appointments created in database
        List<Appointment> listAppointmentStored = AppointmentHome.findByIdUser( appointment1.getIdUser( ) );
        // Check that the list has two results
        assertEquals( listAppointmentStored.size( ), 2 );

    }

    /**
     * Test findByIdSlot method
     */
    public void testFindByIdSlot( )
    {
        User user2 = UserTest.buildUser( Constants.GUID_2, Constants.FIRST_NAME_2, Constants.LAST_NAME_2, Constants.EMAIL_2, Constants.PHONE_NUMBER_2 );
        UserHome.create( user2 );

        this.userIds.add(user2.getIdUser());

        // Initialize a fist Appointment
        Appointment appointment1 = new Appointment( );
        appointment1.setIdUser( this.user.getIdUser( ) );
        appointment1.setIdSlot( this.slotOne.getIdSlot( ) );
        // Create the Appointment in database
        AppointmentHome.create( appointment1 );

        // Initialize a 2nd Appointment
        Appointment appointment2 = new Appointment( );
        appointment2.setIdUser( user2.getIdUser( ) );
        appointment2.setIdSlot( this.slotOne.getIdSlot( ) );
        // Create the Appointment in database
        AppointmentHome.create( appointment2 );
        // Find the Appointments created in database
        List<Appointment> listAppointmentStored = AppointmentHome.findByIdSlot( appointment1.getIdSlot( ) );
        // Check that the list has two results
        assertEquals( listAppointmentStored.size( ), 2 );

    }

    /**
     * Check that all the asserts are true
     *
     * @param appointmentStored
     *            the Appointment stored
     * @param appointment
     *            the Appointment created
     */
    public void checkAsserts( Appointment appointmentStored, Appointment appointment )
    {
        assertEquals( appointmentStored.getIdSlot( ), appointment.getIdSlot( ) );
        assertEquals( appointmentStored.getIdUser( ), appointment.getIdUser( ) );
    }

    public static AppointmentDTO buildAppointmentDTO( Slot slot, String strEmail, String strFirstName, String strLastName, LocalTime startingTime,
                                                      LocalTime endingTime, int nbBookedSeats )
    {
        AppointmentDTO appointmentDTO = new AppointmentDTO( );
        appointmentDTO.setSlot( slot );
        appointmentDTO.setEmail( strEmail );
        appointmentDTO.setFirstName( strFirstName );
        appointmentDTO.setLastName( strLastName );
        appointmentDTO.setStartingTime( startingTime );
        appointmentDTO.setEndingTime( endingTime );
        appointmentDTO.setNbBookedSeats( nbBookedSeats );
        return appointmentDTO;
    }

    @Override
    protected  void setUp() throws Exception {
        super.setUp();
        this.form = FormTest.buildForm1( );
        FormHome.create( this.form );

        this.user = UserTest.buildUser( Constants.GUID_1, Constants.FIRST_NAME_1, Constants.LAST_NAME_1, Constants.EMAIL_1, Constants.PHONE_NUMBER_1 );
        UserHome.create( user );
        // Add user id to private list of users that will be deleted int he tear down
        this.userIds.add(this.user.getIdUser());

        this.slotOne = SlotTest.buildSlot( form.getIdForm( ), Constants.STARTING_DATE_1, Constants.ENDING_DATE_1, Constants.NB_REMAINING_PLACES_1,
                Constants.NB_REMAINING_PLACES_1, 0, Constants.NB_REMAINING_PLACES_1, Boolean.TRUE, Boolean.TRUE );
        SlotHome.create( slotOne);
        this.slotIds.add(this.slotOne.getIdSlot());

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //delete all the forms left over from tests
        for (Form f : FormHome.findAllForms()) {
            FormHome.delete(f.getIdForm());
            assertNull(FormHome.findByPrimaryKey(f.getIdForm()));
        }
        for (int id : this.userIds) {
            UserHome.delete(id);
            assertNull(UserHome.findByPrimaryKey(id));
        }

        for (int id : this.slotIds) {
            SlotHome.delete(id);
            assertNull(SlotHome.findByPrimaryKey(id));
        }

        this.userIds = null;
        this.slotIds = null;
        this.form = null;
        this.user = null;
        this.slotOne = null;

    }
}
