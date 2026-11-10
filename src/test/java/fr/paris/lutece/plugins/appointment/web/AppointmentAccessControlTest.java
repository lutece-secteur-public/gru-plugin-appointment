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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import fr.paris.lutece.plugins.appointment.business.AppointmentTest;
import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.rbac.RBACHome;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The back office pages of a form check the permissions of that form
 */
public class AppointmentAccessControlTest extends LuteceTestCase
{
    private static final String ROLE = "appointment_access_test";
    private static final String ROLE_SUPER_ADMIN = "super_admin";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ID_APPOINTMENT = "id_appointment";
    private static final String MARK_APPOINTMENTFORM_LIST = "appointmentform_list";
    private static final String MARK_PERMISSIONS = "permissions";

    private final List<Integer> _listIdForm = new ArrayList<>( );
    private final List<Integer> _listIdAppointment = new ArrayList<>( );
    private final List<Integer> _listIdRBAC = new ArrayList<>( );

    @AfterEach
    public void cleanUp( )
    {
        for ( int nIdAppointment : _listIdAppointment )
        {
            if ( AppointmentService.findAppointmentById( nIdAppointment ) != null )
            {
                AppointmentService.deleteAppointment( nIdAppointment );
            }
        }
        _listIdForm.forEach( FormServiceTest::cleanForm );
        _listIdRBAC.forEach( RBACHome::remove );
    }

    /**
     * The details of an appointment need the permission on the form of the appointment, not on the form given in the request
     */
    @Test
    public void testViewAppointmentOfAnotherForm( ) throws Exception
    {
        int nIdFormAllowed = createForm( "Allowed form", null );
        int nIdFormOther = createForm( "Other form", null );
        int nIdAppointment = createAppointment( nIdFormOther );
        grant( nIdFormAllowed, AppointmentResourceIdService.PERMISSION_VIEW_APPOINTMENT );

        MockHttpServletRequest request = request( ROLE );
        request.addParameter( PARAMETER_ID_FORM, String.valueOf( nIdFormAllowed ) );
        request.addParameter( PARAMETER_ID_APPOINTMENT, String.valueOf( nIdAppointment ) );
        AppointmentJspBean instance = appointmentBean( request );

        assertRefused( ( ) -> instance.getViewAppointment( request ) );
    }

    /**
     * The removal of an appointment needs the permission on the form of the appointment
     */
    @Test
    public void testRemoveAppointmentOfAnotherForm( ) throws Exception
    {
        int nIdFormAllowed = createForm( "Allowed form", null );
        int nIdFormOther = createForm( "Other form", null );
        int nIdAppointment = createAppointment( nIdFormOther );
        grant( nIdFormAllowed, AppointmentResourceIdService.PERMISSION_DELETE_APPOINTMENT );

        MockHttpServletRequest request = request( ROLE );
        request.addParameter( PARAMETER_ID_FORM, String.valueOf( nIdFormAllowed ) );
        request.addParameter( PARAMETER_ID_APPOINTMENT, String.valueOf( nIdAppointment ) );
        AppointmentJspBean instance = appointmentBean( request );

        assertRefused( ( ) -> instance.doRemoveAppointment( request ) );
        assertNotNull( AppointmentService.findAppointmentById( nIdAppointment ), "the appointment must not be removed" );
    }

    /**
     * The calendar of a form is refused to a user outside the workgroup of the form, as the list of forms hides it
     */
    @Test
    public void testCalendarOfAnotherWorkgroup( ) throws Exception
    {
        int nIdForm = createForm( "Workgroup form", "appointment_access_test_wg" );

        MockHttpServletRequest request = request( ROLE_SUPER_ADMIN );
        request.addParameter( PARAMETER_ID_FORM, String.valueOf( nIdForm ) );
        AppointmentJspBean instance = appointmentBean( request );

        assertRefused( ( ) -> instance.getViewCalendarManageAppointments( request ) );
    }

    /**
     * The annual calendar needs the permission on the advanced settings of the form, like its actions
     */
    @Test
    public void testAnnualCalendarNeedsPermission( ) throws Exception
    {
        int nIdForm = createForm( "Annual calendar form", null );

        MockHttpServletRequest request = request( ROLE );
        request.addParameter( PARAMETER_ID_FORM, String.valueOf( nIdForm ) );
        AppointmentAnnualCalendarJspBean instance = CDI.current( ).select( AppointmentAnnualCalendarJspBean.class ).get( );
        instance.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );

        assertRefused( ( ) -> instance.getViewManageAnnualCalendar( request ) );
    }

    /**
     * The buttons of the list of forms are the ones of the form on the same line
     */
    @Test
    public void testFormListButtonsFollowTheirForm( ) throws Exception
    {
        // Sorted by title: the hidden form comes first
        createForm( "AAA access test hidden", null );
        int nIdFormShown = createForm( "ZZZ access test shown", null );
        grant( nIdFormShown, AppointmentResourceIdService.PERMISSION_VIEW_FORM );
        grant( nIdFormShown, AppointmentResourceIdService.PERMISSION_MODIFY_FORM );

        MockHttpServletRequest request = request( ROLE );
        AppointmentFormJspBean instance = CDI.current( ).select( AppointmentFormJspBean.class ).get( );
        instance.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        render( ( ) -> instance.getManageAppointmentForms( request ) );

        Models models = getModels( instance );
        List<?> listForms = new ArrayList<>( (java.util.Collection<?>) models.get( MARK_APPOINTMENTFORM_LIST ) );
        String [ ] [ ] permissions = (String [ ] [ ]) models.get( MARK_PERMISSIONS );
        assertEquals( 1, listForms.size( ) );
        assertEquals( nIdFormShown, ( (AppointmentFormDTO) listForms.get( 0 ) ).getIdForm( ) );
        assertEquals( "true", permissions [0] [2], "the modify button of the shown form" );
    }

    private int createForm( String strTitle, String strWorkgroup )
    {
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTitle( strTitle );
        appointmentForm.setWorkgroup( strWorkgroup );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        _listIdForm.add( nIdForm );
        return nIdForm;
    }

    private int createAppointment( int nIdForm ) throws Exception
    {
        LocalDateTime start = LocalDate.now( ).plusDays( 1 ).atTime( LocalTime.of( 10, 0 ) );
        Slot slot = SlotService.saveSlot( SlotTest.buildSlot( nIdForm, start, start.plusMinutes( 30 ), 1, 1, 0, 1, Boolean.TRUE, Boolean.TRUE ) );
        AppointmentDTO appointmentDTO = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "access@test.fr", "Jean", "Dupont", start.toLocalTime( ),
                start.toLocalTime( ).plusMinutes( 30 ), 1 );
        int nIdAppointment = AppointmentService.saveAppointment( appointmentDTO );
        _listIdAppointment.add( nIdAppointment );
        return nIdAppointment;
    }

    private void grant( int nIdForm, String strPermission )
    {
        RBAC rbac = new RBAC( );
        rbac.setRoleKey( ROLE );
        rbac.setResourceTypeKey( AppointmentFormDTO.RESOURCE_TYPE );
        rbac.setResourceId( String.valueOf( nIdForm ) );
        rbac.setPermissionKey( strPermission );
        _listIdRBAC.add( RBACHome.create( rbac ).getRBACId( ) );
    }

    private static MockHttpServletRequest request( String strRole ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = new AdminUser( );
        Map<String, RBACRole> roles = new HashMap<>( );
        roles.put( strRole, new RBACRole( strRole, strRole ) );
        user.setRoles( roles );
        AdminUserUtils.registerAdminUserWithRight( request, user, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        return request;
    }

    private static AppointmentJspBean appointmentBean( MockHttpServletRequest request ) throws AccessDeniedException
    {
        AppointmentJspBean instance = CDI.current( ).select( AppointmentJspBean.class ).get( );
        instance.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        return instance;
    }

    /**
     * The page is refused before anything is rendered: the admin macros are not loaded by the tests
     */
    private static void assertRefused( Executable page )
    {
        assertThrows( AccessDeniedException.class, page );
    }

    /**
     * Run a view: the page itself needs the admin macros, which the tests do not load, only its model matters
     */
    private static void render( Executable page )
    {
        try
        {
            page.execute( );
        }
        catch( Throwable e )
        {
            if ( !e.toString( ).toLowerCase( ).contains( "freemarker" ) )
            {
                throw new IllegalStateException( e );
            }
        }
    }

    private static Models getModels( AppointmentFormJspBean instance ) throws ReflectiveOperationException
    {
        // The bean is a client proxy of the session scope: read the field of its contextual instance
        Field field = AppointmentFormJspBean.class.getDeclaredField( "_models" );
        field.setAccessible( true );
        return (Models) field.get( ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( ) );
    }
}
