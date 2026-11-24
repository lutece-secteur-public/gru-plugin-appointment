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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.web.dto.CommentDTO;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;

/**
 * Script of the back office calendars, written by the macro fullCalendarScript of appointment_commons.html
 */
public class AppointmentCalendarScriptRenderTest extends LuteceTestCase
{
    private static String render( String strMacroCall )
    {
        return render( strMacroCall, new HashMap<>( ) );
    }

    private static String render( String strMacroCall, Map<String, Object> model )
    {
        model.put( "locale", "fr" );
        model.put( "permission_moderate_comment", "false" );
        model.put( "permission_access_code", "admin" );
        model.put( "mailing_list", new ReferenceList( ) );
        String strFtl = "<#include \"/admin/plugins/appointment/appointment_commons.html\" />" + strMacroCall;
        return AppTemplateService.getTemplateFromStringFtl( strFtl, Locale.FRENCH, model ).getHtml( );
    }

    /**
     * The day headers of the appointments calendar link to the list of the local day, not of the UTC one
     */
    @Test
    public void testDayLinkUsesTheLocalDate( )
    {
        String strHtml = render( "<@fullCalendarScript selectable=false />" );

        assertTrue( strHtml, strHtml.contains( "navLinkDayClick" ) );
        assertFalse( strHtml, strHtml.contains( "date.toISOString()" ) );
    }

    /**
     * The planning screens define no list of appointments to link to: no day link
     */
    @Test
    public void testNoDayLinkOnThePlanningScreens( )
    {
        assertFalse( render( "<@fullCalendarScript isSpecific=true />" ).contains( "navLinkDayClick" ) );
        assertFalse( render( "<@fullCalendarScript isTypical=true />" ).contains( "navLinkDayClick" ) );
    }

    /**
     * The typical week shows the current week only: it does not jump to the week of a comment
     */
    @Test
    public void testTypicalWeekDoesNotJumpToTheWeekOfAComment( )
    {
        assertFalse( render( "<@fullCalendarScript isTypical=true />" ).contains( "calendarInst.gotoDate(new Date(dtCurrent))" ) );
        assertTrue( render( "<@fullCalendarScript selectable=false />" ).contains( "calendarInst.gotoDate(new Date(dtCurrent))" ) );
    }

    /**
     * The week to come back to is stored when the comment form is submitted, not when its modal is opened
     */
    @Test
    public void testCommentContextStoredOnSubmission( )
    {
        String strHtml = render( "<@getCommentModal 'commentModal' 'title' 'comment' 'startingValidityDate' 'endingValidityDate' 'idStartingTime' 'idEndingTime' 'doAddComment' 3 mailing_list />" );

        assertFalse( strHtml, strHtml.contains( "localStorage.setItem('commentContext', firstDay)" ) );
        assertTrue( strHtml, strHtml.contains( "localStorage.setItem('commentContext', window.pendingCommentContext)" ) );
    }

    /**
     * The dates of a new comment are set to the displayed week when the modal opens, even if the fields already hold today's date
     */
    @Test
    public void testNewCommentDatesSetToTheDisplayedWeek( )
    {
        String strHtml = render( "<@getCommentModal 'commentModal' 'title' 'comment' 'startingValidityDate' 'endingValidityDate' 'idStartingTime' 'idEndingTime' 'doAddComment' 3 mailing_list />" );

        assertFalse( strHtml, strHtml.contains( "fpSet(startDateEl, firstDay)" ) );
        assertTrue( strHtml, strHtml.contains( "fpForce(startDateEl, firstDay)" ) );
        assertTrue( strHtml, strHtml.contains( "fpForce(endDateEl, firstDay)" ) );
    }

    /**
     * A comment without times is written with its dates alone: a date followed by an empty time is refused by the calendar
     */
    @Test
    public void testCommentWithoutTimes( )
    {
        CommentDTO comment = new CommentDTO( );
        comment.setId( 7 );
        comment.setComment( "bh" );
        comment.setStartingValidityDate( LocalDate.of( 2026, 12, 1 ) );
        comment.setEndingValidityDate( LocalDate.of( 2026, 12, 3 ) );
        comment.setCreationDate( LocalDate.of( 2026, 11, 24 ) );
        Map<String, Object> model = new HashMap<>( );
        model.put( "comments", List.of( comment ) );

        String strHtml = render( "<@getComments comments_list=comments />", model );

        assertTrue( strHtml, strHtml.contains( "\"start\" : \"2026-12-01\"" ) );
        assertFalse( strHtml, strHtml.contains( "2026-12-01T\"" ) );
        assertFalse( strHtml, strHtml.matches( "(?s).*\"end\" : \"[0-9-]+T\".*" ) );
    }
}
