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
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The front office workflow action of an appointment (doProcessWorkflowAction) is protected by a security token (LUT-35242)
 */
public class AppointmentAppWorkflowActionTokenTest extends LuteceTestCase
{
    private static final String ACTION_DO_PROCESS_WORKFLOW_ACTION = "doProcessWorkflowAction";
    private static final String PARAMETER_REF_APPOINTMENT = "refAppointment";
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String UNKNOWN_REFERENCE = "AppointmentAppWorkflowActionTokenTestUnknown";
    private static final String UNKNOWN_ACTION = "987654";
    private static final String TEMPLATE_TASKS_FORM = "skin/plugins/appointment/tasks_form_workflow.html";

    @AfterEach
    public void clearLocalVariables( )
    {
        LocalVariables.setLocal( null, null, null );
    }

    private static ISecurityTokenService tokenService( )
    {
        return CDI.current( ).select( ISecurityTokenService.class ).get( );
    }

    private static AppointmentApp app( )
    {
        return CDI.current( ).select( AppointmentApp.class ).get( );
    }

    private static MockHttpServletRequest actionRequest( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setMethod( "POST" );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );
        request.addParameter( PARAMETER_ID_ACTION, UNKNOWN_ACTION );
        request.addParameter( PARAMETER_REF_APPOINTMENT, UNKNOWN_REFERENCE );

        return request;
    }

    /**
     * Without token, with a wrong token or with the token of another action, the action is refused before anything is read or changed
     */
    @Test
    public void testActionWithoutValidTokenIsRefused( )
    {
        MockHttpServletRequest request = actionRequest( );
        assertThrows( AccessDeniedException.class, ( ) -> app( ).doProcessWorkflowAction( request ) );

        MockHttpServletRequest requestWrongToken = actionRequest( );
        requestWrongToken.addParameter( SecurityTokenService.PARAMETER_TOKEN, "wrong" );
        assertThrows( AccessDeniedException.class, ( ) -> app( ).doProcessWorkflowAction( requestWrongToken ) );

        MockHttpServletRequest requestOtherToken = actionRequest( );
        requestOtherToken.addParameter( SecurityTokenService.PARAMETER_TOKEN, tokenService( ).getToken( requestOtherToken, "doCancelAppointment" ) );
        assertThrows( AccessDeniedException.class, ( ) -> app( ).doProcessWorkflowAction( requestOtherToken ) );
    }

    /**
     * A valid token lets the action run (here up to the unknown appointment), once: the token cannot be replayed
     *
     * @throws Exception
     *             if the action fails
     */
    @Test
    public void testActionWithValidTokenRunsOnce( ) throws Exception
    {
        MockHttpServletRequest request = actionRequest( );
        String strToken = tokenService( ).getToken( request, ACTION_DO_PROCESS_WORKFLOW_ACTION );
        request.addParameter( SecurityTokenService.PARAMETER_TOKEN, strToken );

        XPage page = app( ).doProcessWorkflowAction( request );
        assertNotNull( page );

        assertThrows( AccessDeniedException.class, ( ) -> app( ).doProcessWorkflowAction( request ) );
    }

    /**
     * An action without task form is confirmed by a site message whose form posts a valid token of the action
     *
     * @throws Exception
     *             if the action fails
     */
    @Test
    public void testConfirmationOfAnActionWithoutTaskFormCarriesAToken( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );
        request.addParameter( PARAMETER_ID_ACTION, UNKNOWN_ACTION );
        request.addParameter( PARAMETER_REF_APPOINTMENT, UNKNOWN_REFERENCE );

        assertThrows( SiteMessageException.class, ( ) -> app( ).getWorkflowActionForm( request ) );
        SiteMessage message = SiteMessageService.getMessage( request );
        assertNotNull( message );
        assertTrue( message.getUrl( ).contains( "action=" + ACTION_DO_PROCESS_WORKFLOW_ACTION ), message.getUrl( ) );
        String strToken = message.getRequestParameters( ).get( SecurityTokenService.PARAMETER_TOKEN );
        assertNotNull( strToken );

        // the confirmation posts this token: the action accepts it
        request.setMethod( "POST" );
        request.addParameter( SecurityTokenService.PARAMETER_TOKEN, strToken );
        assertNotNull( app( ).doProcessWorkflowAction( request ) );
    }

    /**
     * The task form page, displayed at first or again after a failed validation, posts the token of its model
     */
    @Test
    public void testTaskFormPagePostsTheToken( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "tasks_form", HtmlMarkup.of( "<textarea name=\"comment_value_1\"></textarea>" ) );
        model.put( "id_action", 3 );
        model.put( "refAppointment", "ref" );
        model.put( SecurityTokenService.MARK_TOKEN, "tok123" );

        String strHtml = AppTemplateService.getTemplate( TEMPLATE_TASKS_FORM, Locale.ENGLISH, model ).getHtml( );

        assertTrue( strHtml.contains( "name=\"token\"" ) && strHtml.contains( "value=\"tok123\"" ), strHtml );
    }
}
