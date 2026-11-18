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

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.comment.Comment;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Check of the validity period of a calendar comment, whose times are optional
 */
public class CommentJspBeanValidityTest extends LuteceTestCase
{
    private static final LocalDate DAY = LocalDate.of( 2026, 12, 1 );

    private static Comment comment( LocalTime start, LocalTime end )
    {
        Comment comment = new Comment( );
        comment.setStartingValidityDate( DAY );
        comment.setEndingValidityDate( DAY );
        comment.setStartingValidityTime( start );
        comment.setEndingValidityTime( end );
        return comment;
    }

    /**
     * One day with a start time and no end time is valid
     */
    @Test
    public void testOneDayWithoutEndTime( )
    {
        CommentJspBean bean = new CommentJspBean( );
        assertTrue( bean.validateDateStartEndValidity( comment( LocalTime.of( 10, 0 ), null ) ) );
        assertTrue( bean.validateDateStartEndValidity( comment( null, LocalTime.of( 10, 0 ) ) ) );
        assertTrue( bean.validateDateStartEndValidity( comment( null, null ) ) );
    }

    /**
     * One day whose end time is before its start time is refused
     */
    @Test
    public void testOneDayWithEndTimeBeforeStartTime( )
    {
        CommentJspBean bean = new CommentJspBean( );
        assertFalse( bean.validateDateStartEndValidity( comment( LocalTime.of( 10, 0 ), LocalTime.of( 9, 0 ) ) ) );
        assertTrue( bean.validateDateStartEndValidity( comment( LocalTime.of( 9, 0 ), LocalTime.of( 10, 0 ) ) ) );
    }
}
