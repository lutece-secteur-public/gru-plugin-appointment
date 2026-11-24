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
package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.FormTest;
import fr.paris.lutece.plugins.appointment.business.comment.Comment;
import fr.paris.lutece.plugins.appointment.business.comment.CommentHome;
import fr.paris.lutece.plugins.appointment.business.comment.CommentNotificationConfig;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Body of the e-mail sent to a mailing list when a comment of a calendar is created, modified or deleted
 */
public class CommentServiceNotificationTest extends LuteceTestCase
{
    private static final LocalDate DATE = LocalDate.parse( "2026-12-01" );

    private Form _form;
    private Comment _comment;

    @BeforeEach
    public void createComment( )
    {
        _form = FormTest.buildForm1( );
        FormHome.create( _form );
        _comment = new Comment( );
        _comment.setIdForm( _form.getIdForm( ) );
        _comment.setStartingValidityDate( DATE );
        _comment.setEndingValidityDate( DATE );
        _comment.setComment( "<p>bh text <strong>bold</strong></p>" );
        _comment.setCreationDate( DATE );
        _comment.setCreatorUserName( "admin" );
        CommentHome.create( _comment );
    }

    @AfterEach
    public void removeComment( )
    {
        CommentHome.remove( _comment.getId( ) );
        FormHome.delete( _form.getIdForm( ) );
    }

    /**
     * The message typed in the rich text editor and the rich text of the comment are written as HTML, not as their escaped source
     */
    @Test
    public void testRichTextIsNotEscaped( )
    {
        String strContent = CommentService.buildNotificationContent( _comment, config( "<p>New comment: ${comment!}</p>" ), Locale.ENGLISH );

        assertTrue( strContent.contains( "<p>New comment: <p>bh text <strong>bold</strong></p></p>" ), strContent );
        assertFalse( strContent.contains( "&lt;" ), strContent );
    }

    /**
     * The values of the model that are text stay escaped
     */
    @Test
    public void testTextValuesAreEscaped( )
    {
        String strContent = CommentService.buildNotificationContent( _comment, config( "<p>${creator_user_name!} ${form_title!}</p>" ), Locale.ENGLISH );

        assertTrue( strContent.startsWith( "<p>" ), strContent );
        assertFalse( strContent.contains( "${" ), strContent );
    }

    private static CommentNotificationConfig config( String strMessage )
    {
        CommentNotificationConfig config = new CommentNotificationConfig( );
        config.setMessage( strMessage );
        config.setSubject( "bh subject" );
        config.setSenderName( "bh" );
        return config;
    }
}
