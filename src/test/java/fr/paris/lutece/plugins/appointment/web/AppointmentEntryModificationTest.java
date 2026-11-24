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

import java.util.ArrayList;
import java.util.Map;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Modification of the questions of an appointment form and of their answers
 */
public class AppointmentEntryModificationTest extends LuteceTestCase
{
    private static final String ROLE_SUPER_ADMIN = "super_admin";
    private static final int ID_RESOURCE = 987660;
    private static final int ID_TYPE_TEXT = 128;

    private final java.util.List<Integer> _listIdEntry = new ArrayList<>( );

    @AfterEach
    public void removeQuestions( )
    {
        for ( int nIdEntry : _listIdEntry )
        {
            EntryHome.remove( nIdEntry );
        }
        _listIdEntry.clear( );
    }

    /**
     * A disabled question stays disabled when it is modified: the modification screen does not post its state
     */
    @Test
    public void testModifyKeepsDisabled( ) throws Exception
    {
        int nIdEntry = createEntry( ID_TYPE_TEXT, "bh Q1" );
        Field disabled = new Field( );
        disabled.setParentEntry( EntryHome.findByPrimaryKey( nIdEntry ) );
        disabled.setCode( IEntryTypeService.FIELD_DISABLED );
        disabled.setValue( "true" );
        FieldHome.create( disabled );
        assertEquals( "true", EntryHome.findByPrimaryKey( nIdEntry ).getFieldByCode( IEntryTypeService.FIELD_DISABLED ).getValue( ) );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        AppointmentFormEntryJspBean instance = bean( request, AppointmentFormEntryJspBean.class );
        request.addParameter( "id_entry", String.valueOf( nIdEntry ) );
        request.addParameter( "title", "bh Q1" );
        request.addParameter( "help_message", "help" );
        request.addParameter( "width", "50" );
        request.addParameter( "save", "save" );

        instance.doModifyEntry( request );

        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );
        assertEquals( "help", entry.getHelpMessage( ) );
        assertEquals( "true", entry.getFieldByCode( IEntryTypeService.FIELD_DISABLED ).getValue( ) );

        assertEquals( "true", EntryHome.findByPrimaryKey( nIdEntry ).getFieldByCode( IEntryTypeService.FIELD_DISABLED ).getValue( ) );
    }

    int createEntry( int nIdType, String strTitle )
    {
        Entry entry = new Entry( );
        entry.setIdResource( ID_RESOURCE );
        entry.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        entry.setTitle( strTitle );
        entry.setEntryType( EntryTypeHome.findByPrimaryKey( nIdType ) );
        int nIdEntry = EntryHome.create( entry );
        _listIdEntry.add( nIdEntry );
        return nIdEntry;
    }

    static <T extends MVCAdminJspBean> T bean( MockHttpServletRequest request, Class<T> beanClass ) throws Exception
    {
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_SUPER_ADMIN, new RBACRole( ROLE_SUPER_ADMIN, ROLE_SUPER_ADMIN ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );

        T instance = CDI.current( ).select( beanClass ).get( );
        instance.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        java.lang.reflect.Field fieldResponse = MVCAdminJspBean.class.getDeclaredField( "_response" );
        fieldResponse.setAccessible( true );
        fieldResponse.set( ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( ), new MockHttpServletResponse( ) );
        return instance;
    }
}
