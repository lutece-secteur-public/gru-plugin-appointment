package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

public class ReservationRuleServiceTest extends LuteceTestCase
{

    public void testFindReservationRuleByIdFormAndClosestToDateOfApply( )
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
        LocalDate dateOfModification2 = LocalDate.parse( "2028-06-22" );
        FormService.updateAdvancedParameters( appointmentForm3, dateOfModification2 );

        assertEquals( dateOfModification, ReservationRuleService.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, LocalDate.parse( "2028-06-21" ) )
                .getDateOfApply( ) );

        FormService.removeForm( nIdForm );
    }

}
