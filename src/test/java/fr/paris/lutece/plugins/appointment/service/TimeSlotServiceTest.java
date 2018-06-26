package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalTime;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

public class TimeSlotServiceTest extends LuteceTestCase
{

    /**
     * Find the next time slots of a given time slot
     */
    public void testFindListTimeSlotAfterThisTimeSlot( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeStart( "09:00" );
        appointmentForm.setTimeEnd( "18:00" );
        appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinition( listWeekDefinition.get( 0 ).getIdWeekDefinition( ) );
        List<TimeSlot> listTimeSlot = TimeSlotService.findListTimeSlotByWorkingDay( listWorkingDay.get( 0 ).getIdWorkingDay( ) );

        TimeSlot timeSlot = listTimeSlot.stream( ).filter( t -> t.getStartingTime( ).equals( LocalTime.parse( "17:00" ) ) ).findFirst( ).get( );

        List<TimeSlot> listNextTimeSlots = TimeSlotService.findListTimeSlotAfterThisTimeSlot( timeSlot );

        assertEquals( 1, listNextTimeSlots.size( ) );
        assertEquals( LocalTime.parse( "17:30" ), listNextTimeSlots.get( 0 ).getStartingTime( ) );

        FormService.removeForm( nIdForm );

    }

    /**
     * Return an ordered and filtered list of time slots after a given time
     */
    public void testGetNextTimeSlotsInAListOfTimeSlotAfterALocalTime( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeStart( "09:00" );
        appointmentForm.setTimeEnd( "18:00" );
        appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinition( listWeekDefinition.get( 0 ).getIdWeekDefinition( ) );
        List<TimeSlot> listTimeSlot = TimeSlotService.findListTimeSlotByWorkingDay( listWorkingDay.get( 0 ).getIdWorkingDay( ) );

        List<TimeSlot> listNextTimeSlots = TimeSlotService.getNextTimeSlotsInAListOfTimeSlotAfterALocalTime( listTimeSlot, LocalTime.parse( "17:10" ) );

        assertEquals( 1, listNextTimeSlots.size( ) );

        FormService.removeForm( nIdForm );

    }

    /**
     * Returns the time slot in a list of time slot with the given starting time
     */
    public void testGetTimeSlotInListOfTimeSlotWithStartingTime( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeStart( "09:00" );
        appointmentForm.setTimeEnd( "18:00" );
        appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinition( listWeekDefinition.get( 0 ).getIdWeekDefinition( ) );
        List<TimeSlot> listTimeSlot = TimeSlotService.findListTimeSlotByWorkingDay( listWorkingDay.get( 0 ).getIdWorkingDay( ) );

        assertEquals( LocalTime.parse( "17:30" ), TimeSlotService.getTimeSlotInListOfTimeSlotWithStartingTime( listTimeSlot, LocalTime.parse( "17:00" ) )
                .getEndingTime( ) );

        FormService.removeForm( nIdForm );

    }
}
