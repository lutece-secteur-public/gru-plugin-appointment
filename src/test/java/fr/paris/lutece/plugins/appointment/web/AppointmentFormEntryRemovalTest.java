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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The removal of a question or of an answer is refused when it would also remove responses of appointments: responses of the questions of a
 * group, and of the conditional questions of an answer
 */
public class AppointmentFormEntryRemovalTest extends LuteceTestCase
{
    private static final String ROLE_SUPER_ADMIN = "super_admin";
    private static final int ID_RESOURCE = 987650;
    private static final int ID_ENTRY_TYPE = 124;

    private int _nIdGroup;
    private int _nIdChild;
    private int _nIdChoice;
    private int _nIdField;
    private int _nIdConditional;

    @BeforeEach
    public void createQuestions( )
    {
        _nIdGroup = createEntry( "bh group", null, null );
        _nIdChild = createEntry( "bh child", _nIdGroup, null );
        createResponse( _nIdChild );

        _nIdChoice = createEntry( "bh choice", null, null );
        fr.paris.lutece.plugins.genericattributes.business.Field field = new fr.paris.lutece.plugins.genericattributes.business.Field( );
        field.setParentEntry( EntryHome.findByPrimaryKey( _nIdChoice ) );
        field.setTitle( "A" );
        field.setValue( "a" );
        field.setCode( "answer_choice" );
        _nIdField = FieldHome.create( field );
        _nIdConditional = createEntry( "bh conditional", null, _nIdField );
        createResponse( _nIdConditional );
    }

    @AfterEach
    public void removeQuestions( )
    {
        EntryHome.remove( _nIdGroup );
        EntryHome.remove( _nIdChoice );
    }

    @Test
    public void testConfirmRemoveGroupWithAnsweredChild( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AppointmentFormEntryJspBean instance = bean( request, AppointmentFormEntryJspBean.class );
        request.addParameter( "id_entry", String.valueOf( _nIdGroup ) );

        instance.getConfirmRemoveEntry( request );

        assertEquals( AdminMessage.TYPE_STOP, AdminMessageService.getMessage( request ).getType( ) );
    }

    @Test
    public void testDoRemoveGroupWithAnsweredChild( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AppointmentFormEntryJspBean instance = bean( request, AppointmentFormEntryJspBean.class );
        request.addParameter( "id_entry", String.valueOf( _nIdGroup ) );

        instance.doRemoveEntry( request );

        assertNotNull( EntryHome.findByPrimaryKey( _nIdGroup ) );
        assertNotNull( EntryHome.findByPrimaryKey( _nIdChild ) );
    }

    @Test
    public void testConfirmRemoveChoiceWithAnsweredConditionalQuestion( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AppointmentFormEntryJspBean instance = bean( request, AppointmentFormEntryJspBean.class );
        request.addParameter( "id_entry", String.valueOf( _nIdChoice ) );

        instance.getConfirmRemoveEntry( request );

        assertEquals( AdminMessage.TYPE_STOP, AdminMessageService.getMessage( request ).getType( ) );
    }

    @Test
    public void testConfirmRemoveAnswerWithAnsweredConditionalQuestion( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AppointmentFormFieldJspBean instance = bean( request, AppointmentFormFieldJspBean.class );
        request.addParameter( "id_field", String.valueOf( _nIdField ) );

        instance.getConfirmRemoveField( request );

        assertEquals( AdminMessage.TYPE_STOP, AdminMessageService.getMessage( request ).getType( ) );
    }

    @Test
    public void testDoRemoveAnswerWithAnsweredConditionalQuestion( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AppointmentFormFieldJspBean instance = bean( request, AppointmentFormFieldJspBean.class );
        request.addParameter( "id_field", String.valueOf( _nIdField ) );

        instance.doRemoveField( request );

        assertNotNull( FieldHome.findByPrimaryKey( _nIdField ) );
        assertNotNull( EntryHome.findByPrimaryKey( _nIdConditional ) );
    }

    @Test
    public void testConfirmRemoveQuestionWithoutResponse( ) throws Exception
    {
        int nIdEntry = createEntry( "bh free", null, null );
        try
        {
            MockHttpServletRequest request = new MockHttpServletRequest( );
            AppointmentFormEntryJspBean instance = bean( request, AppointmentFormEntryJspBean.class );
            request.addParameter( "id_entry", String.valueOf( nIdEntry ) );

            instance.getConfirmRemoveEntry( request );

            assertEquals( AdminMessage.TYPE_CONFIRMATION, AdminMessageService.getMessage( request ).getType( ) );
        }
        finally
        {
            EntryHome.remove( nIdEntry );
        }
    }

    private static int createEntry( String strTitle, Integer nIdParent, Integer nIdFieldDepend )
    {
        EntryType entryType = new EntryType( );
        entryType.setIdType( ID_ENTRY_TYPE );
        Entry entry = new Entry( );
        entry.setIdResource( ID_RESOURCE );
        entry.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        entry.setTitle( strTitle );
        entry.setEntryType( entryType );
        if ( nIdParent != null )
        {
            entry.setParent( EntryHome.findByPrimaryKey( nIdParent ) );
        }
        if ( nIdFieldDepend != null )
        {
            entry.setFieldDepend( FieldHome.findByPrimaryKey( nIdFieldDepend ) );
        }
        return EntryHome.create( entry );
    }

    private static void createResponse( int nIdEntry )
    {
        Response response = new Response( );
        response.setEntry( EntryHome.findByPrimaryKey( nIdEntry ) );
        response.setResponseValue( "abc" );
        ResponseHome.create( response );
    }

    private static <T extends MVCAdminJspBean> T bean( MockHttpServletRequest request, Class<T> beanClass ) throws Exception
    {
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_SUPER_ADMIN, new RBACRole( ROLE_SUPER_ADMIN, ROLE_SUPER_ADMIN ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );

        T instance = CDI.current( ).select( beanClass ).get( );
        instance.init( request, AppointmentFormJspBean.RIGHT_MANAGEAPPOINTMENTFORM );
        // processController gives the bean its response for the redirects: set it as processController does
        Field fieldResponse = MVCAdminJspBean.class.getDeclaredField( "_response" );
        fieldResponse.setAccessible( true );
        fieldResponse.set( ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( ), new MockHttpServletResponse( ) );
        return instance;
    }
}
