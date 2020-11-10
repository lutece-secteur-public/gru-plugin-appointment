package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import fr.paris.lutece.plugins.appointment.business.AppointmentTest;
import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the Appointment Service
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentServiceTest extends LuteceTestCase
{

    public void testAppointmentAndNbRemainingPlaces( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 1, 1, 0, 1,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment = -1;
        try 
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbRemainingPlaces2( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 2, 2, 0, 2,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        int nIdAppointment = -1;
        try 
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbRemainingPlaces3( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 2, 2, 0, 2,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment = -1;
        try 
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 1, 1, 0, 1,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment = -1;
        try 
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces2( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 2, 2, 0, 2,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        
        int nIdAppointment = -1;
        try 
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPotentialRemainingPlaces3( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 2, 2, 0, 2,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment = -1;
        try 
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 1, 1, 0, 1,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment = -1;
        try 
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbPlacesTaken( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken2( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 2, 2, 0, 2,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment = -1;
        try 
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 1, slot.getNbPlacesTaken( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testAppointmentAndNbPlacesTaken3( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 2, 2, 0, 2,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( slot, "mdp@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        int nIdAppointment = -1;
        try 
        {
            nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 2, slot.getNbPlacesTaken( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment ) );
    }

    public void testMultipleAppointmentsOnSameSlot( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 2, 2, 0, 2,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }
        

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 2, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
    }

    public void testMultipleAppointmentsOnSameSlot2( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 =  AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 2, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );
    }

    public void testMultipleAppointmentsOnSameSlot3( )
    {

        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment1 = -1;
        try
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 2 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );

        assertEquals( 3, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 1, 1, 0, 1,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment1 = -1;
        try 
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        AppointmentService.deleteAppointment( nIdAppointment1 );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 0, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces2( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 2, 2, 0, 2,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment1 = -1;
        try 
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );

        AppointmentService.deleteAppointment( nIdAppointment1 );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 0, slot.getNbPlacesTaken( ) );
        assertEquals( 2, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 2, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces3( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 2, 2, 0, 2,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment1 = -1 ;
        try 
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment2 = -1;
        try
        {
           nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 2, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        AppointmentService.deleteAppointment( nIdAppointment1 );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );
    }

    public void testRemoveAppointmentAndCheckNbRemainingPlaces4( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
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

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 3, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        AppointmentService.deleteAppointment( nIdAppointment1 );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 2, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 2, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );
    }

    public void testCancelAppointment( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 2, 2, 0, 2,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment1 = -1;
        try 
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );

        Appointment appointmentToCancel = AppointmentService.findAppointmentById( nIdAppointment1 );
        appointmentToCancel.setIsCancelled( true );
        AppointmentService.updateAppointment( appointmentToCancel );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 0, slot.getNbPlacesTaken( ) );
        assertEquals( 2, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 2, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );

    }

    public void testCancelAppointment2( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 2, 2, 0, 2,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment1 = -1;
        try 
        {
            nIdAppointment1 = AppointmentService.saveAppointment( appointmentDTO1 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 = AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 2, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        Appointment appointmentToCancel = AppointmentService.findAppointmentById( nIdAppointment1 );
        appointmentToCancel.setIsCancelled( true );
        AppointmentService.updateAppointment( appointmentToCancel );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 1, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 1, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );

    }

    public void testCancelAppointment3( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );

        Slot slot = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-05T10:30" ), 3, 3, 0, 3,
                Boolean.TRUE, Boolean.TRUE );
        slot = SlotService.saveSlot( slot );

        AppointmentDTO appointmentDTO1 = AppointmentTest.buildAppointmentDTO( slot, "jean.dupont@mdp.fr", "Jean", "Dupont", LocalTime.parse( "10:00" ),
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

        AppointmentDTO appointmentDTO2 = AppointmentTest.buildAppointmentDTO( slot, "gerard.durand@mdp.fr", "Gérard", "Durand", LocalTime.parse( "10:00" ),
                LocalTime.parse( "10:30" ), 1 );
        int nIdAppointment2 = -1;
        try
        {
            nIdAppointment2 =  AppointmentService.saveAppointment( appointmentDTO2 );
        }
        catch (Exception e) 
        {
            fail( e.getLocalizedMessage( ) );
        }

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 3, slot.getNbPlacesTaken( ) );
        assertEquals( 0, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 0, slot.getNbRemainingPlaces( ) );

        Appointment appointmentToCancel = AppointmentService.findAppointmentById( nIdAppointment1 );
        appointmentToCancel.setIsCancelled( true );
        AppointmentService.updateAppointment( appointmentToCancel );

        slot = SlotService.findSlotById( slot.getIdSlot( ) );
        assertEquals( 1, slot.getNbPlacesTaken( ) );
        assertEquals( 2, slot.getNbPotentialRemainingPlaces( ) );
        assertEquals( 2, slot.getNbRemainingPlaces( ) );

        FormService.removeForm( nIdForm );

        assertNull( AppointmentService.findAppointmentById( nIdAppointment1 ) );
        assertNull( AppointmentService.findAppointmentById( nIdAppointment2 ) );

    }

}
