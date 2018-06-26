package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.test.LuteceTestCase;

public class ClosingDayServiceTest extends LuteceTestCase
{

    /**
     * Find all the closing dates of the form on a given period
     */
    public void testFindListDateOfClosingDayByIdFormAndDateRange( )
    {
        // Build the form
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        List<LocalDate> listClosingDays = new ArrayList<>( );
        listClosingDays.add( LocalDate.parse( "2018-05-01" ) );
        listClosingDays.add( LocalDate.parse( "2018-05-08" ) );
        listClosingDays.add( LocalDate.parse( "2018-07-14" ) );
        listClosingDays.add( LocalDate.parse( "2018-08-15" ) );
        ClosingDayService.saveListClosingDay( nIdForm, listClosingDays );

        List<LocalDate> listClosingDaysFound = ClosingDayService.findListDateOfClosingDayByIdFormAndDateRange( nIdForm, LocalDate.parse( "2018-06-01" ),
                LocalDate.parse( "2018-09-01" ) );
        assertEquals( 2, listClosingDaysFound.size( ) );

        FormService.removeForm( nIdForm );
    }

}
