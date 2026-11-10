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
import java.util.Map;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Second display of the back office booking form on the same slot, after a refused validation
 */
public class AppointmentJspBeanSecondAttemptTest extends LuteceTestCase
{
    private static final String ROLE_SUPER_ADMIN = "super_admin";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_STARTING_DATE_TIME = "starting_date_time";

    private int _nIdForm;

    @AfterEach
    public void removeForm( )
    {
        FormServiceTest.cleanForm( _nIdForm );
    }

    /**
     * The place held by the first display stays the place of the agent: the second display still offers it, and does not hold it twice
     *
     * @throws Exception
     *             if the form cannot be displayed
     */
    @Test
    public void testSecondDisplayKeepsThePlaceHeldByTheFirstOne( ) throws Exception
    {
        _nIdForm = FormService.createAppointmentForm( FormServiceTest.buildAppointmentForm( ) );
        LocalDateTime start = LocalDate.now( ).with( TemporalAdjusters.next( DayOfWeek.TUESDAY ) ).atTime( LocalTime.of( 10, 0 ) );
        Slot slot = SlotService.saveSlot( SlotTest.buildSlot( _nIdForm, start, start.plusMinutes( 30 ), 1, 1, 0, 1, Boolean.TRUE, Boolean.TRUE ) );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_SUPER_ADMIN, new RBACRole( ROLE_SUPER_ADMIN, ROLE_SUPER_ADMIN ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        request.addParameter( PARAMETER_ID_FORM, String.valueOf( _nIdForm ) );
        request.addParameter( PARAMETER_STARTING_DATE_TIME, start.toString( ) );

        AppointmentJspBean instance = CDI.current( ).select( AppointmentJspBean.class ).get( );
        instance.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );

        display( instance, request );
        assertEquals( "places offered by the first display", 1, getNotValidatedAppointment( instance ).getNbMaxPotentialBookedSeats( ) );
        assertEquals( 0, SlotService.findSlotById( slot.getIdSlot( ) ).getNbPotentialRemainingPlaces( ) );

        // The validation is refused: the controller shows the form again, same session, same slot
        display( instance, request );
        assertEquals( "places offered by the second display", 1, getNotValidatedAppointment( instance ).getNbMaxPotentialBookedSeats( ) );
        assertEquals( 0, SlotService.findSlotById( slot.getIdSlot( ) ).getNbPotentialRemainingPlaces( ) );
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

    private static AppointmentDTO getNotValidatedAppointment( AppointmentJspBean instance ) throws ReflectiveOperationException
    {
        // The bean is a client proxy of the session scope: read the field of its contextual instance
        Field field = AppointmentJspBean.class.getDeclaredField( "_notValidatedAppointment" );
        field.setAccessible( true );
        return (AppointmentDTO) field.get( ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( ) );
    }
}
