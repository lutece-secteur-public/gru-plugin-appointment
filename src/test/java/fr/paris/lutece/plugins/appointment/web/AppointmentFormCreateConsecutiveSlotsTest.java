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
import java.util.Map;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The creation screen of a form displayed again after a refused creation keeps the number of consecutive slots typed
 */
public class AppointmentFormCreateConsecutiveSlotsTest extends LuteceTestCase
{
    private static final String ROLE_SUPER_ADMIN = "super_admin";

    @Test
    public void testNumberOfConsecutiveSlotsKept( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_SUPER_ADMIN, new RBACRole( ROLE_SUPER_ADMIN, ROLE_SUPER_ADMIN ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        AppointmentFormJspBean instance = CDI.current( ).select( AppointmentFormJspBean.class ).get( );
        instance.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        Object bean = ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( );
        Field fieldResponse = MVCAdminJspBean.class.getDeclaredField( "_response" );
        fieldResponse.setAccessible( true );
        fieldResponse.set( bean, new MockHttpServletResponse( ) );
        AppointmentFormDTO form = FormServiceTest.buildAppointmentForm( );
        form.setIsMultislotAppointment( true );
        form.setNbConsecutiveSlots( 3 );
        Field fieldForm = AppointmentFormJspBean.class.getDeclaredField( "_appointmentFormDTO" );
        fieldForm.setAccessible( true );
        fieldForm.set( bean, form );

        String strHtml = instance.getCreateAppointmentForm( request );

        int nIndex = strHtml.indexOf( "id=\"nb_consecutive_slots\"" );
        assertTrue( nIndex > 0, strHtml );
        String strInput = strHtml.substring( strHtml.lastIndexOf( "<input", nIndex ), strHtml.indexOf( '>', nIndex ) );
        assertTrue( strInput.contains( "value=\"3\"" ), strInput );
        assertTrue( strHtml.contains( "toggleConsecutiveSlots(true);" ) );
    }
}
