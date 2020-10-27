package fr.paris.lutece.plugins.appointment.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.WeekDefinitionService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

public class SlotServiceTest extends LuteceTestCase
{

    // Check that there are 180 open slots from the 3/12/2022 to the 14/12/2022
    // With open days from Monday to Friday
    public void testOpenSlots( )
    {

        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2022-12-01" ) ) );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2022-12-31" ) ) );

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
        List<Slot> listSlots = SlotService.buildListSlot( nIdForm, mapWeekDefinition, LocalDate.parse( "2022-12-03" ), LocalDate.parse( "2022-12-14" ) );

        assertEquals( 180, listSlots.stream( ).filter( s -> s.getIsOpen( ) ).collect( Collectors.toList( ) ).size( ) );

        FormService.removeForm( nIdForm );
    }

    public void testOpenSlotsWithSpecificSlotsClosed( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2022-12-01" ) ) );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2022-12-31" ) ) );

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

        Slot slotSpecificClosed1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-03T09:00" ), LocalDateTime.parse( "2022-12-03T09:30" ), 1, 1, 0,
                1, Boolean.FALSE, Boolean.TRUE );
        slotSpecificClosed1 = SlotService.saveSlot( slotSpecificClosed1 );

        Slot slotSpecificClosed2 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-04T09:00" ), LocalDateTime.parse( "2022-12-04T09:30" ), 1, 1, 0,
                1, Boolean.FALSE, Boolean.TRUE );
        slotSpecificClosed2 = SlotService.saveSlot( slotSpecificClosed2 );

        Slot slotSpecificClosed3 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-05T10:00" ), LocalDateTime.parse( "2022-12-04T10:30" ), 1, 1, 0,
                1, Boolean.FALSE, Boolean.TRUE );
        slotSpecificClosed3 = SlotService.saveSlot( slotSpecificClosed3 );

        List<Slot> listSlots = SlotService.buildListSlot( nIdForm, mapWeekDefinition, LocalDate.parse( "2022-12-03" ), LocalDate.parse( "2022-12-14" ) );

        assertEquals( 177, listSlots.stream( ).filter( s -> s.getIsOpen( ) ).collect( Collectors.toList( ) ).size( ) );

        FormService.removeForm( nIdForm );
    }

    public void testOpenSlotsWithSpecificLargeSlots( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2022-12-01" ) ) );
        appointmentForm.setDateStartValidity( Date.valueOf( LocalDate.parse( "2022-12-31" ) ) );

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

        Slot slotSpecific1 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-03T09:00" ), LocalDateTime.parse( "2022-12-03T10:00" ), 1, 1, 0, 1,
                Boolean.TRUE, Boolean.TRUE );
        slotSpecific1 = SlotService.saveSlot( slotSpecific1 );

        Slot slotSpecific2 = SlotTest.buildSlot( nIdForm, LocalDateTime.parse( "2022-12-04T10:00" ), LocalDateTime.parse( "2022-12-04T11:30" ), 1, 1, 0, 1,
                Boolean.TRUE, Boolean.TRUE );
        slotSpecific2 = SlotService.saveSlot( slotSpecific2 );

        List<Slot> listSlots = SlotService.buildListSlot( nIdForm, mapWeekDefinition, LocalDate.parse( "2022-12-03" ), LocalDate.parse( "2022-12-14" ) );

        assertEquals( 177, listSlots.stream( ).filter( s -> s.getIsOpen( ) ).collect( Collectors.toList( ) ).size( ) );

        FormService.removeForm( nIdForm );
    }

}
