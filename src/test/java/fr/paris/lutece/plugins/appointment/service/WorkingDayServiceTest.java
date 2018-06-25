package fr.paris.lutece.plugins.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

public class WorkingDayServiceTest extends LuteceTestCase
{

    /**
     * Get the max ending time of a list of working days
     */
    public void testGetMaxEndingTimeOfAListOfWorkingDay( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeEnd( "18:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeEnd( "20:00" );
        LocalDate dateOfModification = LocalDate.parse( "2018-06-20" );
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = new ArrayList<>( );
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            listWorkingDay.addAll( weekDefinition.getListWorkingDay( ) );
        }

        assertEquals( LocalTime.parse( "20:00" ), WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( listWorkingDay ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Get the max ending time of a working day
     */
    public void testGetMaxEndingTimeOfAWorkingDay( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeEnd( "18:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinition( listWeekDefinition.get( 0 ).getIdWeekDefinition( ) );

        WorkingDay workingDayMonday = listWorkingDay.stream( ).filter( w -> w.getDayOfWeek( ) == DayOfWeek.MONDAY.getValue( ) ).findFirst( ).get( );

        assertEquals( LocalTime.parse( "18:00" ), WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDayMonday ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Get the min duration slot of a list of working days
     */
    public void testGetMinDurationTimeSlotOfAListOfWorkingDay( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setDurationAppointments( 10 );
        LocalDate dateOfModification = LocalDate.parse( "2028-06-20" );
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = new ArrayList<>( );
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            listWorkingDay.addAll( weekDefinition.getListWorkingDay( ) );
        }

        assertEquals( 10, WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay( listWorkingDay ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Get the min duration slot of a working day
     */
    public void testGetMinDurationTimeSlotOfAWorkingDay( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinition( listWeekDefinition.get( 0 ).getIdWeekDefinition( ) );

        WorkingDay workingDayMonday = listWorkingDay.stream( ).filter( w -> w.getDayOfWeek( ) == DayOfWeek.MONDAY.getValue( ) ).findFirst( ).get( );

        assertEquals( 30, WorkingDayService.getMinDurationTimeSlotOfAWorkingDay( workingDayMonday ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Get the min starting time of a list of working days
     */
    public void testGetMinStartingTimeOfAListOfWorkingDay( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeStart( "09:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeStart( "10:00" );
        LocalDate dateOfModification = LocalDate.parse( "2018-06-20" );
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = new ArrayList<>( );
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
            listWorkingDay.addAll( weekDefinition.getListWorkingDay( ) );
        }

        assertEquals( LocalTime.parse( "09:00" ), WorkingDayService.getMinStartingTimeOfAListOfWorkingDay( listWorkingDay ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Get the min starting time of a working day
     */
    public void testGetMinStartingTimeOfAWorkingDay( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeStart( "09:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinition( listWeekDefinition.get( 0 ).getIdWeekDefinition( ) );

        WorkingDay workingDayMonday = listWorkingDay.stream( ).filter( w -> w.getDayOfWeek( ) == DayOfWeek.MONDAY.getValue( ) ).findFirst( ).get( );

        assertEquals( LocalTime.parse( "09:00" ), WorkingDayService.getMinStartingTimeOfAWorkingDay( workingDayMonday ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Get the open days of an appointmentForm DTO
     */
    public void testGetOpenDays( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setIsOpenMonday( Boolean.TRUE );
        appointmentForm.setIsOpenTuesday( Boolean.TRUE );
        appointmentForm.setIsOpenWednesday( Boolean.TRUE );
        appointmentForm.setIsOpenThursday( Boolean.TRUE );
        appointmentForm.setIsOpenFriday( Boolean.TRUE );
        appointmentForm.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm.setIsOpenSunday( Boolean.FALSE );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        assertEquals( 5, WorkingDayService.getOpenDays( appointmentForm ).size( ) );

        FormService.removeForm( nIdForm );
    }
}
