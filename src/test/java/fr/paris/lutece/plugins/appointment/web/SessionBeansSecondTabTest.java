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

import fr.paris.lutece.plugins.appointment.business.calendar.CalendarTemplate;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationConfig;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationHome;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Session scoped beans of the back office used from two tabs: each form says which record it edits
 */
public class SessionBeansSecondTabTest extends LuteceTestCase
{
    private static final String ROLE_SUPER_ADMIN = "super_admin";

    /**
     * Tab 1 edits the configuration CREATE, tab 2 then opens the configuration DELETE: the form of tab 1 updates CREATE
     */
    @Test
    public void testNotificationConfigOfTheForm( ) throws Exception
    {
        CommentNotificationConfig configCreate = CommentNotificationHome.loadCommentNotificationConfigByType( "CREATE" );
        CommentNotificationConfig configDelete = CommentNotificationHome.loadCommentNotificationConfigByType( "DELETE" );
        String strSubjectCreate = configCreate.getSubject( );
        String strSubjectDelete = configDelete.getSubject( );
        try
        {
            MockHttpServletRequest request = new MockHttpServletRequest( );
            CommentNotificationJspBean instance = bean( request, CommentNotificationJspBean.class, CommentJspBean.RIGHT_MANAGECOMMENTTFORM );
            setField( instance, CommentNotificationJspBean.class, "_commentNotificationConfig", configDelete );
            request.addParameter( "notify_type", "CREATE" );
            request.addParameter( "sender_name", configCreate.getSenderName( ) );
            request.addParameter( "subject", "bh subject CREATE" );
            request.addParameter( "message", "bh message" );

            instance.doUpdateNotificationCommentConfig( request );

            assertEquals( "bh subject CREATE", CommentNotificationHome.loadCommentNotificationConfigByType( "CREATE" ).getSubject( ) );
            assertEquals( strSubjectDelete, CommentNotificationHome.loadCommentNotificationConfigByType( "DELETE" ).getSubject( ) );
        }
        finally
        {
            CommentNotificationHome.update( configCreate );
            configDelete.setSubject( strSubjectDelete );
            CommentNotificationHome.update( configDelete );
            configCreate.setSubject( strSubjectCreate );
            CommentNotificationHome.update( configCreate );
        }
    }

    /**
     * Tab 1 modifies the template T, tab 2 then opens "Add a template": a new template, not T
     */
    @Test
    public void testAddTemplateWhileAnotherIsModified( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        CalendarTemplateJspBean instance = bean( request, CalendarTemplateJspBean.class, CalendarTemplateJspBean.RIGHT_MANAGE_CALENDAR_TEMPLATES );
        CalendarTemplate template = new CalendarTemplate( );
        template.setIdCalendarTemplate( 1 );
        template.setTitle( "bh modified template" );
        setField( instance, CalendarTemplateJspBean.class, "_template", template );

        instance.getCreateModifyTemplate( request );

        CalendarTemplate templateOfTheForm = (CalendarTemplate) getField( instance, CalendarTemplateJspBean.class, "_template" );
        assertEquals( 0, templateOfTheForm.getIdCalendarTemplate( ) );
    }

    private static <T extends MVCAdminJspBean> T bean( MockHttpServletRequest request, Class<T> beanClass, String strRight ) throws Exception
    {
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_SUPER_ADMIN, new RBACRole( ROLE_SUPER_ADMIN, ROLE_SUPER_ADMIN ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, strRight );
        T instance = CDI.current( ).select( beanClass ).get( );
        instance.init( request, strRight );
        // processController gives the bean its response for the redirects: set it as processController does
        setField( instance, MVCAdminJspBean.class, "_response", new MockHttpServletResponse( ) );
        return instance;
    }

    private static void setField( Object instance, Class<?> declaringClass, String strName, Object value ) throws Exception
    {
        Field field = declaringClass.getDeclaredField( strName );
        field.setAccessible( true );
        field.set( ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( ), value );
    }

    private static Object getField( Object instance, Class<?> declaringClass, String strName ) throws Exception
    {
        Field field = declaringClass.getDeclaredField( strName );
        field.setAccessible( true );
        return field.get( ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( ) );
    }
}
