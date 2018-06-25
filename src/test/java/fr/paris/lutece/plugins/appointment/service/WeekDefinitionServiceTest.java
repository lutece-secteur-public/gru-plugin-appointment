package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

public class WeekDefinitionServiceTest extends LuteceTestCase
{

    /**
     * Find a week definition of a form and a date of apply
     */
    public void testFindWeekDefinitionByIdFormAndClosestToDateOfApply( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeEnd( "18:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeEnd( "20:00" );
        LocalDate dateOfModification = LocalDate.parse( "2028-06-20" );
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );
        LocalDate dateOfApply = LocalDate.parse( "2028-06-22" );
        WeekDefinition foundWeekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, dateOfApply );
        assertEquals( dateOfModification, foundWeekDefinition.getDateOfApply( ) );

        AppointmentFormDTO appointmentForm3 = FormServiceTest.buildAppointmentForm( );
        appointmentForm3.setIdForm( nIdForm );
        appointmentForm3.setTimeEnd( "19:00" );
        LocalDate dateOfModification2 = LocalDate.parse( "2028-06-21" );
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification2 );

        foundWeekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, dateOfApply );
        assertEquals( dateOfModification2, foundWeekDefinition.getDateOfApply( ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Return, if it exists, the next week definition after a given date
     */
    public void testFindNextWeekDefinition( )
    {

        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeEnd( "18:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeEnd( "20:00" );
        LocalDate dateOfModification = LocalDate.parse( "2028-06-20" );
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );
        LocalDate givenDate = LocalDate.parse( "2028-06-19" );

        WeekDefinition foundWeekDefinition = WeekDefinitionService.findNextWeekDefinition( nIdForm, givenDate );

        assertEquals( dateOfModification, foundWeekDefinition.getDateOfApply( ) );

        LocalDate givenDate2 = LocalDate.parse( "2028-06-21" );
        foundWeekDefinition = WeekDefinitionService.findNextWeekDefinition( nIdForm, givenDate2 );
        assertNull( foundWeekDefinition );

        FormService.removeForm( nIdForm );
    }

    /**
     * Return the min starting time of a list of week definitions
     */
    public void testGetMinStartingTimeOfAListOfWeekDefinition( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeStart( "09:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeStart( "10:00" );
        LocalDate dateOfModification = LocalDate.parse( "2028-06-20" );
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );

        AppointmentFormDTO appointmentForm3 = FormServiceTest.buildAppointmentForm( );
        appointmentForm3.setIdForm( nIdForm );
        appointmentForm3.setTimeStart( "09:30" );
        LocalDate dateOfModification2 = LocalDate.parse( "2028-06-21" );
        FormService.updateAdvancedParameters( appointmentForm3, dateOfModification2 );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        assertEquals( LocalTime.parse( "09:00" ), WeekDefinitionService.getMinStartingTimeOfAListOfWeekDefinition( listWeekDefinition ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Return the min starting time of a week definition
     */
    public void testGetMinStartingTimeOfAWeekDefinition( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeStart( "09:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        WeekDefinition weekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm ).get( 0 );
        assertEquals( LocalTime.parse( "09:00" ), WeekDefinitionService.getMinStartingTimeOfAWeekDefinition( weekDefinition ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Return the max ending time of a list of week definitions
     */
    public void testGetMaxEndingTimeOfAListOfWeekDefinition( )
    {

        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeEnd( "18:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeEnd( "19:00" );
        LocalDate dateOfModification = LocalDate.parse( "2028-06-20" );
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );

        AppointmentFormDTO appointmentForm3 = FormServiceTest.buildAppointmentForm( );
        appointmentForm3.setIdForm( nIdForm );
        appointmentForm3.setTimeEnd( "19:30" );
        LocalDate dateOfModification2 = LocalDate.parse( "2028-06-21" );
        FormService.updateAdvancedParameters( appointmentForm3, dateOfModification2 );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        assertEquals( LocalTime.parse( "19:30" ), WeekDefinitionService.getMaxEndingTimeOfAListOfWeekDefinition( listWeekDefinition ) );

        FormService.removeForm( nIdForm );

    }

    /**
     * Get the max ending time of a week definition
     */
    public void testGetMaxEndingTimeOfAWeekDefinition( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeEnd( "19:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        WeekDefinition weekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm ).get( 0 );
        assertEquals( LocalTime.parse( "19:00" ), WeekDefinitionService.getMaxEndingTimeOfAWeekDefinition( weekDefinition ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Get the min duration of a time slot of a week definition
     */
    public void testGetMinDurationTimeSlotOfAListOfWeekDefinition( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setDurationAppointments( 20 );
        LocalDate dateOfModification = LocalDate.parse( "2028-06-20" );
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );

        AppointmentFormDTO appointmentForm3 = FormServiceTest.buildAppointmentForm( );
        appointmentForm3.setIdForm( nIdForm );
        appointmentForm3.setDurationAppointments( 10 );
        LocalDate dateOfModification2 = LocalDate.parse( "2028-06-21" );
        FormService.updateAdvancedParameters( appointmentForm3, dateOfModification2 );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );

        assertEquals( 10, WeekDefinitionService.getMinDurationTimeSlotOfAListOfWeekDefinition( listWeekDefinition ) );

        FormService.removeForm( nIdForm );
    }

    /**
     * Get the set of the open days of all the week definitons
     */
    public void testGetOpenDaysOfWeek( )
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

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );

        assertEquals( 5, WeekDefinitionService.getOpenDaysOfWeek( listWeekDefinition ).size( ) );

        FormService.removeForm( nIdForm );
    }
}
