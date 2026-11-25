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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jboss.weld.proxy.WeldClientProxy;

import fr.paris.lutece.plugins.appointment.business.FormTest;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationConfig.NotificationType;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationLog;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationLogHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.service.CommentService;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Comment notifications of the admin header: the text of a rich text comment, and the workgroup of its form
 */
public class CommentNotificationDashboardTest extends LuteceTestCase
{
    private static final String ROLE_SUPER_ADMIN = "super_admin";
    private static final String RICH_COMMENT = "<p>Texte <strong>gras</strong> du commentaire</p>";

    private Form _form;
    private CommentNotificationLog _notification;

    @BeforeEach
    public void createNotification( )
    {
        _form = FormTest.buildForm1( );
        FormHome.create( _form );
        _notification = new CommentNotificationLog( );
        _notification.setType( NotificationType.CREATE );
        _notification.setIdForm( _form.getIdForm( ) );
        _notification.setIdComment( 1 );
        _notification.setComment( RICH_COMMENT );
        _notification.setCreatorUserName( "bh_author" );
        _notification.setStartingValidityDate( LocalDate.of( 2026, 12, 1 ) );
        _notification.setEndingValidityDate( LocalDate.of( 2026, 12, 1 ) );
        _notification.setDateNotification( Timestamp.valueOf( LocalDateTime.now( ) ) );
        CommentNotificationLogHome.create( _notification );
    }

    @AfterEach
    public void removeNotification( )
    {
        CommentNotificationLogHome.removeByIdForm( _form.getIdForm( ) );
        FormHome.delete( _form.getIdForm( ) );
    }

    private static AdminUser superAdmin( )
    {
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_SUPER_ADMIN, new RBACRole( ROLE_SUPER_ADMIN, ROLE_SUPER_ADMIN ) ) );
        user.setLocale( Locale.FRENCH );
        return user;
    }

    /**
     * The notification shows the text of the comment, not its HTML source
     */
    @Test
    public void testRichTextCommentShownAsText( )
    {
        String strHtml = new CommentNotificationDashboardComponent( ).getDashboardData( superAdmin( ), new MockHttpServletRequest( ) );

        assertTrue( strHtml, strHtml.contains( "Texte gras du commentaire" ) );
        assertFalse( strHtml, strHtml.contains( "&lt;p&gt;" ) );
        assertFalse( strHtml, strHtml.contains( "&lt;strong&gt;" ) );
    }

    /**
     * A notification of a form outside the workgroups of the user is not marked as read by him
     *
     * @throws Exception
     *             if the bean cannot be prepared
     */
    @Test
    public void testMarkReadChecksTheWorkgroup( ) throws Exception
    {
        _form.setWorkgroup( "bh_wg_other" );
        FormHome.update( _form );
        AdminUser user = superAdmin( );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUserUtils.registerAdminUserWithRight( request, user, "APPOINTMENT_FORM_MANAGEMENT" );
        request.addParameter( "id_notification", Integer.toString( _notification.getId( ) ) );
        CommentNotificationsJspBean instance = CDI.current( ).select( CommentNotificationsJspBean.class ).get( );
        instance.init( request, "APPOINTMENT_FORM_MANAGEMENT" );
        Field fieldResponse = MVCAdminJspBean.class.getDeclaredField( "_response" );
        fieldResponse.setAccessible( true );
        Object target = ( instance instanceof WeldClientProxy ) ? ( (WeldClientProxy) instance ).getMetadata( ).getContextualInstance( ) : instance;
        fieldResponse.set( target, new MockHttpServletResponse( ) );

        instance.doMarkRead( request );

        List<CommentNotificationLog> listUnread = CommentNotificationLogHome.findUnreadByIdFormsSince( List.of( _form.getIdForm( ) ),
                CommentService.getNotificationStartDate( ), user.getUserId( ) );
        assertEquals( 1, listUnread.size( ) );
    }
}
