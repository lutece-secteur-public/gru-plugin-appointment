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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * BO summary of an appointment before saving: download of the files of a question that holds several files
 */
public class AppointmentJspBeanRecapFileTest extends LuteceTestCase
{
    private static final String ROLE_SUPER_ADMIN = "super_admin";

    @Test
    public void testSecondFileOfAQuestion( ) throws Exception
    {
        Entry entry = new Entry( );
        entry.setIdEntry( 735 );
        List<Response> listResponses = new ArrayList<>( );
        for ( String strName : new String [ ] {
                "a9.txt", "b9.txt"
        } )
        {
            PhysicalFile physicalFile = new PhysicalFile( );
            physicalFile.setValue( strName.getBytes( ) );
            File file = new File( );
            file.setTitle( strName );
            file.setMimeType( "text/plain" );
            file.setPhysicalFile( physicalFile );
            Response response = new Response( );
            response.setEntry( entry );
            response.setFile( file );
            listResponses.add( response );
        }
        AppointmentDTO appointment = new AppointmentDTO( );
        appointment.setListResponse( listResponses );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_SUPER_ADMIN, new RBACRole( ROLE_SUPER_ADMIN, ROLE_SUPER_ADMIN ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        request.addParameter( "idResponse", "735" );
        request.addParameter( "file_index", "1" );
        AppointmentJspBean instance = CDI.current( ).select( AppointmentJspBean.class ).get( );
        instance.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        Object bean = ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( );
        Field field = AppointmentJspBean.class.getDeclaredField( "_validatedAppointment" );
        field.setAccessible( true );
        field.set( bean, appointment );
        HeaderResponse response = new HeaderResponse( );
        Field fieldResponse = MVCAdminJspBean.class.getDeclaredField( "_response" );
        fieldResponse.setAccessible( true );
        fieldResponse.set( bean, response );

        instance.getDownloadFileFromSession( request );

        String strDisposition = response._mapHeaders.get( "Content-Disposition" );
        assertNotNull( strDisposition );
        assertTrue( strDisposition, strDisposition.contains( "b9.txt" ) );
    }

    /**
     * Response that keeps its headers
     */
    private static class HeaderResponse extends MockHttpServletResponse
    {
        private final Map<String, String> _mapHeaders = new java.util.HashMap<>( );

        @Override
        public void setHeader( String strName, String strValue )
        {
            _mapHeaders.put( strName, strValue );
        }
    }
}
