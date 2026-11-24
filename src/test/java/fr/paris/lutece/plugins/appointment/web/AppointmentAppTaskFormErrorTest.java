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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * A task form of a front office workflow action whose validation fails is displayed again with the message of the task, not with the URL of the
 * back-office message page the task component answers.
 */
public class AppointmentAppTaskFormErrorTest extends LuteceTestCase
{
    private static final String KEY_MESSAGE_WITH_ARGUMENT = "appointment.message.error.openSlots";
    private static final String TEMPLATE = "skin/plugins/appointment/tasks_form_workflow.html";

    /**
     * The message the task stored in the session is read
     */
    @Test
    public void testMessageOfTheTaskIsReturned( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        String strError = AdminMessageService.getMessageUrl( request, KEY_MESSAGE_WITH_ARGUMENT, new Object [ ] {
                "Motif"
        }, AdminMessage.TYPE_STOP );

        String strMessage = AppointmentApp.getTaskFormErrorMessage( request, strError, Locale.ENGLISH );

        assertTrue( strMessage.contains( "Motif" ), strMessage );
        assertFalse( strMessage.contains( "AdminMessage.jsp" ), strMessage );
    }

    /**
     * An error that is not the URL of the message page gives the generic error message
     */
    @Test
    public void testOtherErrorGivesTheGenericMessage( )
    {
        String strMessage = AppointmentApp.getTaskFormErrorMessage( new MockHttpServletRequest( ), "error", Locale.ENGLISH );

        assertEquals( "An error occurred while processing your action.", strMessage );
    }

    /**
     * The task form page displays the error, escaped, above the task form
     */
    @Test
    public void testTaskFormPageDisplaysTheError( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "errors", List.of( new MVCMessage( "The field \"Motif <b>\" is mandatory." ) ) );
        model.put( "tasks_form", HtmlMarkup.of( "<textarea name=\"comment_value_1\">typed</textarea>" ) );
        model.put( "id_action", 3 );
        model.put( "refAppointment", "ref" );

        String strHtml = AppTemplateService.getTemplate( TEMPLATE, Locale.ENGLISH, model ).getHtml( );

        assertTrue( strHtml.contains( "Motif &lt;b&gt;" ), strHtml );
        assertTrue( strHtml.contains( "<textarea name=\"comment_value_1\">typed</textarea>" ), strHtml );
    }
}
