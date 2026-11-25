/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.appointment.web;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Back office booking form of a form displayed while the session still holds a booking started on another form
 */
public class AppointmentJspBeanOtherFormTest extends LuteceTestCase
{
    private static final String ROLE_SUPER_ADMIN = "super_admin";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_STARTING_DATE_TIME = "starting_date_time";
    private static final int ID_ENTRY_OF_OTHER_FORM = 987654;

    private final List<Integer> _listIdForm = new ArrayList<>( );

    @AfterEach
    public void removeForms( )
    {
        _listIdForm.forEach( FormServiceTest::cleanForm );
        _listIdForm.clear( );
    }

    /**
     * The answers of a booking left unfinished on another form (not validated: form displayed, validation refused) are not
     * carried to the booking of this form
     *
     * @throws Exception
     *             if the form cannot be displayed
     */
    @Test
    public void testUnvalidatedBookingOfAnotherFormIsNotReused( ) throws Exception
    {
        AppointmentJspBean instance = CDI.current( ).select( AppointmentJspBean.class ).get( );
        MockHttpServletRequest requestA = displayRequest( instance );
        display( instance, requestA );
        getAppointment( instance, "_notValidatedAppointment" ).setMapResponsesByIdEntry( staleAnswers( ) );

        MockHttpServletRequest requestB = displayRequest( instance );
        display( instance, requestB );
        AppointmentDTO appointment = getAppointment( instance, "_notValidatedAppointment" );
        assertEquals( _listIdForm.get( 1 ).intValue( ), appointment.getIdForm( ) );
        assertFalse( "answers of the other form", appointment.getMapResponsesByIdEntry( ).containsKey( ID_ENTRY_OF_OTHER_FORM ) );
    }

    /**
     * The answers of a booking validated on another form and left at its summary are not carried to the booking of this form
     *
     * @throws Exception
     *             if the form cannot be displayed
     */
    @Test
    public void testValidatedBookingOfAnotherFormIsNotReused( ) throws Exception
    {
        AppointmentJspBean instance = CDI.current( ).select( AppointmentJspBean.class ).get( );
        MockHttpServletRequest requestA = displayRequest( instance );
        display( instance, requestA );
        // the validation moves the booking to the summary
        AppointmentDTO validated = getAppointment( instance, "_notValidatedAppointment" );
        validated.setMapResponsesByIdEntry( staleAnswers( ) );
        setAppointment( instance, "_validatedAppointment", validated );
        setAppointment( instance, "_notValidatedAppointment", null );

        MockHttpServletRequest requestB = displayRequest( instance );
        display( instance, requestB );
        AppointmentDTO appointment = getAppointment( instance, "_notValidatedAppointment" );
        assertEquals( _listIdForm.get( 1 ).intValue( ), appointment.getIdForm( ) );
        assertFalse( "answers of the other form", appointment.getMapResponsesByIdEntry( ).containsKey( ID_ENTRY_OF_OTHER_FORM ) );
    }

    private static Map<Integer, List<Response>> staleAnswers( )
    {
        Map<Integer, List<Response>> map = new HashMap<>( );
        map.put( ID_ENTRY_OF_OTHER_FORM, new ArrayList<>( List.of( new Response( ) ) ) );
        return map;
    }

    /**
     * Request of the booking form of a new bookable form, on its slot of next Tuesday 10:00
     */
    private MockHttpServletRequest displayRequest( AppointmentJspBean instance ) throws Exception
    {
        int nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        _listIdForm.add( nIdForm );
        LocalDateTime start = LocalDate.now( ).with( TemporalAdjusters.next( DayOfWeek.TUESDAY ) ).atTime( LocalTime.of( 10, 0 ) );
        SlotService.saveSlot( SlotTest.buildSlot( nIdForm, start, start.plusMinutes( 30 ), 1, 1, 0, 1, Boolean.TRUE, Boolean.TRUE ) );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_SUPER_ADMIN, new RBACRole( ROLE_SUPER_ADMIN, ROLE_SUPER_ADMIN ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        request.addParameter( PARAMETER_ID_FORM, String.valueOf( nIdForm ) );
        request.addParameter( PARAMETER_STARTING_DATE_TIME, start.toString( ) );
        instance.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        return request;
    }

    /**
     * Display the form; the page itself needs the admin macros, which the tests do not load: only the state of the bean matters here
     */
    private static void display( AppointmentJspBean instance, MockHttpServletRequest request )
    {
        try
        {
            instance.getViewCreateAppointment( request );
        }
        catch( Exception e )
        {
            if ( !( e.toString( ).contains( "Freemarker" ) || e.toString( ).contains( "freemarker" ) ) )
            {
                throw new IllegalStateException( e );
            }
        }
    }

    private static Field field( String strName ) throws ReflectiveOperationException
    {
        Field field = AppointmentJspBean.class.getDeclaredField( strName );
        field.setAccessible( true );
        return field;
    }

    private static Object target( AppointmentJspBean instance )
    {
        // The bean is a client proxy of the session scope: use the fields of its contextual instance
        return ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( );
    }

    private static AppointmentDTO getAppointment( AppointmentJspBean instance, String strField ) throws ReflectiveOperationException
    {
        return (AppointmentDTO) field( strField ).get( target( instance ) );
    }

    private static void setAppointment( AppointmentJspBean instance, String strField, AppointmentDTO appointment ) throws ReflectiveOperationException
    {
        field( strField ).set( target( instance ), appointment );
    }
}
