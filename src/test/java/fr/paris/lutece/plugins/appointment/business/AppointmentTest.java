package fr.paris.lutece.plugins.appointment.business;

import java.util.List;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the Appointment
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentTest extends LuteceTestCase
{

    /**
     * Test method for the Appointment (CRUD)
     */
    public void testAppointment( )
    {
        Form form = FormTest.buildForm( );
        FormHome.create( form );

        User user = UserTest.buildUser( );
        UserHome.create( user );

        Slot slot = SlotTest.buildSlot( );
        slot.setIdForm( form.getIdForm( ) );
        SlotHome.create( slot );

        // Initialize a Appointment
        Appointment appointment = new Appointment( );
        appointment.setIdUser( user.getIdUser( ) );
        appointment.setIdSlot( slot.getIdSlot( ) );
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

        // Clean
        FormHome.delete( form.getIdForm( ) );
        UserHome.delete( user.getIdUser( ) );
    }

    /**
     * Test the delete cascade
     */
    public void testDeleteCascade( )
    {
        Form form = FormTest.buildForm( );
        FormHome.create( form );

        User user = UserTest.buildUser( );
        UserHome.create( user );

        Slot slot = SlotTest.buildSlot( );
        slot.setIdForm( form.getIdForm( ) );
        SlotHome.create( slot );

        // Initialize a Appointment
        Appointment appointment = new Appointment( );
        appointment.setIdUser( user.getIdUser( ) );
        appointment.setIdSlot( slot.getIdSlot( ) );
        // Create the Appointment in database
        AppointmentHome.create( appointment );
        // Find the Appointment created in database
        Appointment appointmentStored = AppointmentHome.findByPrimaryKey( appointment.getIdAppointment( ) );
        assertNotNull( appointmentStored );
        // Delete the form and by cascade the appointment
        FormHome.delete( form.getIdForm( ) );
        appointmentStored = AppointmentHome.findByPrimaryKey( appointment.getIdAppointment( ) );
        // Check the Appointment has been removed from database
        assertNull( appointmentStored );

        // Clean
        FormHome.delete( form.getIdForm( ) );
        UserHome.delete( user.getIdUser( ) );
    }

    /**
     * Test findByIdUser method
     */
    public void testFindByIdUser( )
    {
        Form form = FormTest.buildForm( );
        FormHome.create( form );

        User user = UserTest.buildUser( );
        UserHome.create( user );

        Slot slot1 = SlotTest.buildSlot( );
        slot1.setIdForm( form.getIdForm( ) );
        SlotHome.create( slot1 );

        Slot slot2 = SlotTest.buildSlot2( );
        slot2.setIdForm( form.getIdForm( ) );
        SlotHome.create( slot2 );

        // Initialize a fist Appointment
        Appointment appointment1 = new Appointment( );
        appointment1.setIdUser( user.getIdUser( ) );
        appointment1.setIdSlot( slot1.getIdSlot( ) );
        // Create the Appointment in database
        AppointmentHome.create( appointment1 );

        // Initialize a 2nd Appointment
        Appointment appointment2 = new Appointment( );
        appointment2.setIdUser( user.getIdUser( ) );
        appointment2.setIdSlot( slot2.getIdSlot( ) );
        // Create the Appointment in database
        AppointmentHome.create( appointment2 );
        // Find the Appointments created in database
        List<Appointment> listAppointmentStored = AppointmentHome.findByIdUser( appointment1.getIdUser( ) );
        // Check that the list has two results
        assertEquals( listAppointmentStored.size( ), 2 );

        // Clean
        FormHome.delete( form.getIdForm( ) );
        UserHome.delete( user.getIdUser( ) );
    }

    /**
     * Test findByIdSlot method
     */
    public void testFindByIdSlot( )
    {
        Form form = FormTest.buildForm( );
        FormHome.create( form );

        User user1 = UserTest.buildUser( );
        UserHome.create( user1 );

        User user2 = UserTest.buildUser2( );
        UserHome.create( user2 );

        Slot slot = SlotTest.buildSlot( );
        slot.setIdForm( form.getIdForm( ) );
        SlotHome.create( slot );

        // Initialize a fist Appointment
        Appointment appointment1 = new Appointment( );
        appointment1.setIdUser( user1.getIdUser( ) );
        appointment1.setIdSlot( slot.getIdSlot( ) );
        // Create the Appointment in database
        AppointmentHome.create( appointment1 );

        // Initialize a 2nd Appointment
        Appointment appointment2 = new Appointment( );
        appointment2.setIdUser( user2.getIdUser( ) );
        appointment2.setIdSlot( slot.getIdSlot( ) );
        // Create the Appointment in database
        AppointmentHome.create( appointment2 );
        // Find the Appointments created in database
        List<Appointment> listAppointmentStored = AppointmentHome.findByIdSlot( appointment1.getIdSlot( ) );
        // Check that the list has two results
        assertEquals( listAppointmentStored.size( ), 2 );

        // Clean
        FormHome.delete( form.getIdForm( ) );
        UserHome.delete( user1.getIdUser( ) );
        UserHome.delete( user2.getIdUser( ) );
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
}
