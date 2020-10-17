package fr.paris.lutece.plugins.appointment.service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.paris.lutece.plugins.appointment.business.AppointmentTest;
import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.TimeSlotTest;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.service.WorkingDayService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.test.LuteceTestCase;

public class AppointmentUtilitiesTest extends LuteceTestCase
{

    /**
     * Try to get another appointment which does not match the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );

        appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-04T10:00" ), LocalDateTime.parse( "2018-06-04T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-05T10:00" ), LocalDateTime.parse( "2018-06-05T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );

        assertFalse( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO2, "Jean", "Dupont", "jean.dupont@mdp.fr", appointmentForm ) );

        FormService.removeForm( nIdForm );

    }

    /**
     * Try to get another appointment which matches the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments2( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );

        appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-04T10:00" ), LocalDateTime.parse( "2018-06-04T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-15T10:00" ), LocalDateTime.parse( "2018-06-15T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );

        assertTrue( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO2, "Jean", "Dupont", "jean.dupont@mdp.fr", appointmentForm ) );

        FormService.removeForm( nIdForm );

    }

    /**
     * Try to get a third appointment which does not match the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments3( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );

        appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-04T10:00" ), LocalDateTime.parse( "2018-06-04T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-15T10:00" ), LocalDateTime.parse( "2018-06-15T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-16T10:00" ), LocalDateTime.parse( "2018-06-16T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( slot3, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );

        assertFalse( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO3, "Jean", "Dupont", "jean.dupont@mdp.fr", appointmentForm ) );

        FormService.removeForm( nIdForm );

    }

    /**
     * Try to get a third appointment which matches the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments4( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );

        appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-04T10:00" ), LocalDateTime.parse( "2018-06-04T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-15T10:00" ), LocalDateTime.parse( "2018-06-15T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-07T10:00" ), LocalDateTime.parse( "2018-06-07T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( slot3, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );

        assertTrue( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO3, "Jean", "Dupont", "jean.dupont@mdp.fr", appointmentForm ) );

        FormService.removeForm( nIdForm );

    }

    /**
     * Try to get a third appointment which doas not match the rule of the number of days between two appointments for the same user
     */
    public void testNbDaysBetweenTwoAppointments5( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );

        appointmentForm.setNbDaysBeforeNewAppointment( 2 );

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-04T10:00" ), LocalDateTime.parse( "2018-06-04T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-15T10:00" ), LocalDateTime.parse( "2018-06-15T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-14T10:00" ), LocalDateTime.parse( "2018-06-14T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( slot3, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );

        assertFalse( AppointmentUtilities.checkNbDaysBetweenTwoAppointments( appointmentDTO3, "Jean", "Dupont", "jean.dupont@mdp.fr", appointmentForm ) );

        FormService.removeForm( nIdForm );

    }

    /**
     * Check that the user can not take more than 2 appointments on 7 days
     */
    public void testCheckNbMaxAppointmentsOnAGivenPeriod( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );
        appointmentForm.setNbMaxAppointmentsPerUser( 2 );
        appointmentForm.setNbDaysForMaxAppointmentsPerUser( 7 );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-04T10:00" ), LocalDateTime.parse( "2018-06-04T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-05T10:00" ), LocalDateTime.parse( "2018-06-05T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-07T10:00" ), LocalDateTime.parse( "2018-06-07T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( slot3, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );

        assertFalse( AppointmentUtilities.checkNbMaxAppointmentsOnAGivenPeriod( appointmentDTO3, "jean.dupont@mdp.fr", appointmentForm ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Check that the user can take another appointment
     */
    public void testCheckNbMaxAppointmentsOnAGivenPeriod2( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );
        appointmentForm.setNbMaxAppointmentsPerUser( 3 );
        appointmentForm.setNbDaysForMaxAppointmentsPerUser( 7 );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-04T10:00" ), LocalDateTime.parse( "2018-06-04T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-05T10:00" ), LocalDateTime.parse( "2018-06-05T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot3 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-07T10:00" ), LocalDateTime.parse( "2018-06-07T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot3 = SlotService.saveSlot( slot3 );

        AppointmentDTO appointmentDTO3 = AppointmentTest.buildAppointmentDTO( slot3, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );

        assertTrue( AppointmentUtilities.checkNbMaxAppointmentsOnAGivenPeriod( appointmentDTO3, "jean.dupont@mdp.fr", appointmentForm ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Check and validate all the rules for the number of booked seats asked
     */
    public void testCheckAndReturnNbBookedSeats( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );
        appointmentForm.setMaxPeoplePerAppointment( 2 );
        appointmentForm.setMaxCapacityPerSlot( 3 );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-04T10:00" ), LocalDateTime.parse( "2018-06-04T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot1, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        appointmentDTO2.setNbMaxPotentialBookedSeats( 1 );
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        assertEquals( 1, AppointmentUtilities.checkAndReturnNbBookedSeats( "1", appointmentForm, appointmentDTO2, Locale.FRANCE, listFormErrors ) );
        assertEquals( 0, listFormErrors.size( ) );

        FormService.removeForm( nIdForm );

    }

    /**
     * Try to get an appointment with 2 places on a slot that have only 1 remaining place
     */
    public void testCheckAndReturnNbBookedSeats2( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );
        appointmentForm.setMaxPeoplePerAppointment( 2 );
        appointmentForm.setMaxCapacityPerSlot( 3 );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-04T10:00" ), LocalDateTime.parse( "2018-06-04T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }
        slot1 = SlotService.findSlotById( slot1.getIdSlot( ) );
        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot1, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );

        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        assertEquals( 2, AppointmentUtilities.checkAndReturnNbBookedSeats( "2", appointmentForm, appointmentDTO2, Locale.FRANCE, listFormErrors ) );
        assertEquals( 1, listFormErrors.size( ) );

        FormService.removeForm( nIdForm );

    }

    /**
     * Return the min starting time to display
     */
    public void testGetMinTimeToDisplay( )
    {
        assertEquals( LocalTime.parse( "09:00" ), AppointmentUtilities.getMinTimeToDisplay( LocalTime.parse( "09:22" ) ) );
        assertEquals( LocalTime.parse( "10:30" ), AppointmentUtilities.getMinTimeToDisplay( LocalTime.parse( "10:47" ) ) );
    }

    /**
     * Return the max ending time to display
     */
    public void testGetMaxTimeToDisplay( )
    {
        assertEquals( LocalTime.parse( "09:30" ), AppointmentUtilities.getMaxTimeToDisplay( LocalTime.parse( "09:01" ) ) );
        assertEquals( LocalTime.parse( "11:00" ), AppointmentUtilities.getMaxTimeToDisplay( LocalTime.parse( "10:42" ) ) );
    }

    /**
     * Check if there are appointments impacted by the new week definition
     */
    public void testCheckNoAppointmentsImpacted( )
    {
        AppointmentFormDTO appointmentForm1 = FormServiceTest.buildAppointmentForm( );
        appointmentForm1.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm1.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm1.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-25T10:00" ), LocalDateTime.parse( "2018-06-25T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );

        try 
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-26T17:00" ), LocalDateTime.parse( "2018-06-26T17:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "17:00" ),
                LocalTime.parse( "17:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm2.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );
        appointmentForm2.setIsOpenMonday( Boolean.FALSE );
        appointmentForm2.setIsOpenTuesday( Boolean.TRUE );
        appointmentForm2.setIsOpenWednesday( Boolean.TRUE );
        appointmentForm2.setIsOpenThursday( Boolean.TRUE );
        appointmentForm2.setIsOpenFriday( Boolean.TRUE );
        appointmentForm2.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm2.setIsOpenSunday( Boolean.FALSE );

        LocalDate dateOfModification = LocalDate.parse( "2018-06-20" );
        LocalDateTime endingDateTimeOfSearch = LocalDateTime.of( LocalDate.of( 9999, 12, 31 ), LocalTime.of( 23, 59 ) );
        List<Slot> listSlotsImpacted = SlotService.findSlotsByIdFormAndDateRange( nIdForm, dateOfModification.atStartOfDay( ), endingDateTimeOfSearch );
        List<Appointment> listAppointmentsImpacted = AppointmentService.findListAppointmentByListSlot( listSlotsImpacted );

        assertFalse( AppointmentUtilities.checkNoAppointmentsImpacted( listAppointmentsImpacted, nIdForm, dateOfModification, appointmentForm2 ) );

        FormService.removeForm( nIdForm );
    }

    public void testCheckNoAppointmentsImpacted2( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );
        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-24T10:00" ), LocalDateTime.parse( "2018-06-24T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        Slot slot2 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-25T10:00" ), LocalDateTime.parse( "2018-06-25T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot2 = SlotService.saveSlot( slot2 );

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot2, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm2.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );
        appointmentForm2.setIsOpenMonday( Boolean.FALSE );
        appointmentForm2.setIsOpenTuesday( Boolean.TRUE );
        appointmentForm2.setIsOpenWednesday( Boolean.TRUE );
        appointmentForm2.setIsOpenThursday( Boolean.TRUE );
        appointmentForm2.setIsOpenFriday( Boolean.TRUE );
        appointmentForm2.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm2.setIsOpenSunday( Boolean.FALSE );

        LocalDate dateOfModification = LocalDate.parse( "2018-06-26" );
        LocalDateTime endingDateTimeOfSearch = LocalDateTime.of( LocalDate.of( 9999, 12, 31 ), LocalTime.of( 23, 59 ) );
        List<Slot> listSlotsImpacted = SlotService.findSlotsByIdFormAndDateRange( nIdForm, dateOfModification.atStartOfDay( ), endingDateTimeOfSearch );
        List<Appointment> listAppointmentsImpacted = AppointmentService.findListAppointmentByListSlot( listSlotsImpacted );

        assertTrue( AppointmentUtilities.checkNoAppointmentsImpacted( listAppointmentsImpacted, nIdForm, dateOfModification, appointmentForm ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Check that there is no validated appointments on a slot
     */
    public void testCheckNoValidatedAppointmentsOnThisSlot( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-25T10:00" ), LocalDateTime.parse( "2018-06-25T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        try 
        {
            AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        assertFalse( AppointmentUtilities.checkNoValidatedAppointmentsOnThisSlot( slot1 ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Check that there is no validated appointments on a slot (there is an appointment on the slot but it has been cancelled)
     */
    public void testCheckNoValidatedAppointmentsOnThisSlot2( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2018-06-30" ) ) );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );

        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2018-06-25T10:00" ), LocalDateTime.parse( "2018-06-25T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot1, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        int nIdAppointment1 = -1;
        try 
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }
        Appointment appointment1 = AppointmentService.findAppointmentById( nIdAppointment1 );
        appointment1.setIsCancelled( true );
        AppointmentService.updateAppointment( appointment1 );

        assertTrue( AppointmentUtilities.checkNoValidatedAppointmentsOnThisSlot( slot1 ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Return the slots impacted by the modification of this time slot
     */
    public void testFindSlotsImpactedByThisTimeSlot( )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2018-06-04" ) ) );
        appointmentForm.setDateEndValidity( Date.valueOf( LocalDate.parse( "2025-06-30" ) ) );
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        appointmentForm.setIdForm( nIdForm );

        List<WeekDefinition> allWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );

        WeekDefinition weekDefinition = allWeekDefinition.get( 0 );

        Slot slot1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-06-27T10:00" ), LocalDateTime.parse( "2022-06-27T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot1 = SlotService.saveSlot( slot1 );

        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinition( weekDefinition.getIdWeekDefinition( ) );

        WorkingDay mondayWorkingDay = listWorkingDay.stream( ).filter( w -> w.getDayOfWeek( ) == DayOfWeek.MONDAY.getValue( ) ).findFirst( ).get( );

        TimeSlot timeSlot = TimeSlotTest.buildTimeSlot( LocalTime.parse( "10:00" ), LocalTime.parse( "10:30" ), false, 3, mondayWorkingDay.getIdWorkingDay( ) );

        assertEquals( 1, AppointmentUtilities.findSlotsImpactedByThisTimeSlot( timeSlot, nIdForm, weekDefinition.getIdWeekDefinition( ), false ).size( ) );

        FormService.removeForm( nIdForm );

    }
}
