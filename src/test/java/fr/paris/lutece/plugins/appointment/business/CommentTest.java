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
package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDate;
import java.time.LocalTime;

import fr.paris.lutece.plugins.appointment.business.comment.Comment;
import fr.paris.lutece.plugins.appointment.business.comment.CommentHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.test.LuteceTestCase;

import org.junit.jupiter.api.Test;

/**
 * Test class for the Comment, whose validity times are optional.
 */
public final class CommentTest extends LuteceTestCase
{
    private static final LocalDate STARTING_DATE = LocalDate.parse( "2026-09-24" );
    private static final LocalDate ENDING_DATE = LocalDate.parse( "2026-10-08" );

    /**
     * Creates a comment without validity times, updates its text, then gives it times, reading it back each time.
     */
    @Test
    public void testCommentWithoutTimes( )
    {
        Form form = FormTest.buildForm1( );
        FormHome.create( form );

        Comment comment = new Comment( );
        comment.setIdForm( form.getIdForm( ) );
        comment.setStartingValidityDate( STARTING_DATE );
        comment.setEndingValidityDate( ENDING_DATE );
        comment.setComment( "comment" );
        comment.setCreationDate( STARTING_DATE );
        comment.setCreatorUserName( "admin" );
        CommentHome.create( comment );

        comment.setComment( "modified comment" );
        CommentHome.update( comment );
        Comment stored = CommentHome.findByPrimaryKey( comment.getId( ) );
        assertEquals( "modified comment", stored.getComment( ) );
        assertNull( stored.getStartingValidityTime( ) );
        assertNull( stored.getEndingValidityTime( ) );

        comment.setStartingValidityTime( LocalTime.of( 9, 0 ) );
        comment.setEndingValidityTime( LocalTime.of( 12, 30 ) );
        CommentHome.update( comment );
        stored = CommentHome.findByPrimaryKey( comment.getId( ) );
        assertEquals( LocalTime.of( 9, 0 ), stored.getStartingValidityTime( ) );
        assertEquals( LocalTime.of( 12, 30 ), stored.getEndingValidityTime( ) );

        CommentHome.remove( comment.getId( ) );
        FormHome.delete( form.getIdForm( ) );
    }
}
