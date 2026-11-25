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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.FormTest;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationConfig.NotificationType;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationLog;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationLogHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentResourceIdService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.rbac.RBACHome;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * Notification zone of the comments: the notifications shown for forms in and out of the workgroups of the user, with
 * and without the RBAC permission on the form, read and unread
 */
public class CommentNotificationZoneEquivalenceTest extends LuteceTestCase
{
    private static final String ROLE_SUPER_ADMIN = "super_admin";
    private static final String ROLE_LIMITED = "bh_comment_zone_role";
    private static final String WG_1 = "bh_cz_wg1";
    private static final String WG_2 = "bh_cz_wg2";

    private final List<Form> _listForms = new ArrayList<>( );
    private final List<Integer> _listRbac = new ArrayList<>( );
    // forms: A all (no workgroup), B workgroup 1, C workgroup 2, D all
    private Form _formA;
    private Form _formB;
    private Form _formC;
    private Form _formD;
    private CommentNotificationLog _nA1;
    private CommentNotificationLog _nA2Read;
    private CommentNotificationLog _nB;
    private CommentNotificationLog _nC;
    private CommentNotificationLog _nD;

    @BeforeEach
    public void createData( )
    {
        _formA = form( null );
        _formB = form( WG_1 );
        _formC = form( WG_2 );
        _formD = form( "" );
        _nA1 = notification( _formA );
        _nA2Read = notification( _formA );
        _nB = notification( _formB );
        _nC = notification( _formC );
        _nD = notification( _formD );
        for ( int nUser : new int [ ] {
                9101, 9102, 9103
        } )
        {
            CommentNotificationLogHome.markRead( _nA2Read.getId( ), nUser );
        }
        // the limited role may view forms A, B and C only
        for ( Form form : List.of( _formA, _formB, _formC ) )
        {
            RBAC rbac = new RBAC( );
            rbac.setRoleKey( ROLE_LIMITED );
            rbac.setResourceTypeKey( AppointmentFormDTO.RESOURCE_TYPE );
            rbac.setResourceId( Integer.toString( form.getIdForm( ) ) );
            rbac.setPermissionKey( AppointmentResourceIdService.PERMISSION_VIEW_FORM );
            _listRbac.add( RBACHome.create( rbac ).getRBACId( ) );
        }
    }

    @AfterEach
    public void removeData( )
    {
        _listRbac.forEach( RBACHome::remove );
        for ( Form form : _listForms )
        {
            CommentNotificationLogHome.removeByIdForm( form.getIdForm( ) );
            FormHome.delete( form.getIdForm( ) );
        }
    }

    /**
     * Permission on every form, member of workgroup 1: A (unread), B and D; not the read one, not C (workgroup 2)
     */
    @Test
    public void testSuperAdminInWorkgroup1( )
    {
        String strHtml = zone( user( 9101, ROLE_SUPER_ADMIN, List.of( WG_1 ) ) );

        assertShown( strHtml, _nA1, _nB, _nD );
        assertHidden( strHtml, _nA2Read, _nC );
    }

    /**
     * Permission on A, B, C only, member of both workgroups: A, B and C; not D (no permission)
     */
    @Test
    public void testLimitedRoleInBothWorkgroups( )
    {
        String strHtml = zone( user( 9102, ROLE_LIMITED, List.of( WG_1, WG_2 ) ) );

        assertShown( strHtml, _nA1, _nB, _nC );
        assertHidden( strHtml, _nA2Read, _nD );
    }

    /**
     * Permission on A, B, C only, no workgroup: only the forms of every workgroup it may view, A
     */
    @Test
    public void testLimitedRoleWithoutWorkgroup( )
    {
        String strHtml = zone( user( 9103, ROLE_LIMITED, null ) );

        assertShown( strHtml, _nA1 );
        assertHidden( strHtml, _nA2Read, _nB, _nC, _nD );
    }

    /**
     * No permission at all: nothing
     */
    @Test
    public void testNoPermission( )
    {
        String strHtml = zone( user( 9104, "bh_no_role", List.of( WG_1, WG_2 ) ) );

        assertHidden( strHtml, _nA1, _nA2Read, _nB, _nC, _nD );
    }

    private static String zone( AdminUser user )
    {
        return new CommentNotificationDashboardComponent( ).getDashboardData( user, new MockHttpServletRequest( ) );
    }

    private static AdminUser user( int nId, String strRole, List<String> listWorkgroups )
    {
        AdminUser user = new AdminUser( );
        user.setUserId( nId );
        user.setRoles( Map.of( strRole, new RBACRole( strRole, strRole ) ) );
        user.setUserWorkgroups( listWorkgroups );
        user.setLocale( Locale.FRENCH );
        return user;
    }

    private Form form( String strWorkgroup )
    {
        Form form = FormTest.buildForm1( );
        form.setWorkgroup( strWorkgroup );
        FormHome.create( form );
        _listForms.add( form );
        return form;
    }

    private static CommentNotificationLog notification( Form form )
    {
        CommentNotificationLog notification = new CommentNotificationLog( );
        notification.setType( NotificationType.CREATE );
        notification.setIdForm( form.getIdForm( ) );
        notification.setIdComment( 1 );
        notification.setComment( "bh comment zone" );
        notification.setCreatorUserName( "bh_author" );
        notification.setStartingValidityDate( LocalDate.of( 2026, 12, 1 ) );
        notification.setEndingValidityDate( LocalDate.of( 2026, 12, 1 ) );
        notification.setDateNotification( Timestamp.valueOf( LocalDateTime.now( ) ) );
        return CommentNotificationLogHome.create( notification );
    }

    private static String marker( CommentNotificationLog notification )
    {
        return "name=\"appointment_comment_notification_" + notification.getId( ) + "\"";
    }

    private static void assertShown( String strHtml, CommentNotificationLog... notifications )
    {
        for ( CommentNotificationLog notification : notifications )
        {
            assertTrue( notification.getId( ) + "\n" + strHtml, strHtml.contains( marker( notification ) ) );
        }
    }

    private static void assertHidden( String strHtml, CommentNotificationLog... notifications )
    {
        for ( CommentNotificationLog notification : notifications )
        {
            assertFalse( notification.getId( ) + "\n" + strHtml, strHtml.contains( marker( notification ) ) );
        }
    }
}
