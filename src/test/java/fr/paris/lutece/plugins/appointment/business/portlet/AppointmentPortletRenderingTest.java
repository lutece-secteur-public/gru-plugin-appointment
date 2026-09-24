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
package fr.paris.lutece.plugins.appointment.business.portlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.FormServiceTest;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTemplate;
import fr.paris.lutece.portal.business.portlet.PortletTemplateHome;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;

/**
 * Renders the appointment portlets with the templates registered in the core for their portlet types
 */
public class AppointmentPortletRenderingTest extends LuteceTestCase
{
    private static final String PORTLET_NAME = "AppointmentPortletRenderingTest heading";
    private static final String FORM_TITLE = "AppointmentPortletRenderingTest form";
    private static final String MARKER_FORM_PORTLET = "portlet-appointment-form ";
    private static final String MARKER_FORM_LIST_PORTLET = "portlet-appointment-form-list";
    private static final int UNKNOWN_TEMPLATE_ID = 99999;

    private int _nIdForm;
    private AppointmentFormPortlet _formPortlet;
    private AppointmentFormListPortlet _formListPortlet;
    private MockHttpServletRequest _request;

    /**
     * Creates an active form, a form portlet and a form list portlet
     *
     * @throws Exception
     *             if the test context cannot be initialized
     */
    @BeforeEach
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        FormServiceTest.cleanFormByTitle( FORM_TITLE );
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTitle( FORM_TITLE );
        _nIdForm = FormService.createAppointmentForm( appointmentForm );

        _formPortlet = new AppointmentFormPortlet( );
        _formPortlet.setIdAppointmentForm( _nIdForm );
        initPortlet( _formPortlet );
        AppointmentFormPortletHome.getInstance( ).create( _formPortlet );

        _formListPortlet = new AppointmentFormListPortlet( );
        initPortlet( _formListPortlet );
        AppointmentFormListPortletHome.getInstance( ).create( _formListPortlet );

        _request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, _request, new MockHttpServletResponse( ) );
    }

    /**
     * Removes the portlets and the form
     *
     * @throws Exception
     *             if the test context cannot be released
     */
    @AfterEach
    @Override
    protected void tearDown( ) throws Exception
    {
        if ( _formPortlet != null )
        {
            AppointmentFormPortletHome.getInstance( ).remove( _formPortlet );
        }
        if ( _formListPortlet != null )
        {
            AppointmentFormListPortletHome.getInstance( ).remove( _formListPortlet );
        }
        FormServiceTest.cleanForm( _nIdForm );

        LocalVariables.remove( );
        super.tearDown( );
    }

    /**
     * Sets the common attributes of a test portlet
     *
     * @param portlet
     *            the portlet
     */
    private static void initPortlet( Portlet portlet )
    {
        portlet.setPageId( PortalService.getRootPageId( ) );
        portlet.setStyleId( 0 );
        portlet.setColumn( 1 );
        portlet.setOrder( 1 );
        portlet.setName( PORTLET_NAME );
        portlet.setStatus( Portlet.STATUS_PUBLISHED );
        portlet.setDisplayPortletTitle( 0 );
        portlet.setDeviceDisplayFlags( Portlet.FLAG_DISPLAY_ON_NORMAL_DEVICE | Portlet.FLAG_DISPLAY_ON_LARGE_DEVICE | Portlet.FLAG_DISPLAY_ON_XLARGE_DEVICE );
    }

    /**
     * The shipped templates are registered in the core for each appointment portlet type
     */
    @Test
    public void testShippedTemplatesRegistered( )
    {
        assertEquals( 1, PortletTemplateHome.findByPortletType( AppointmentPortletHome.getInstance( ).getPortletTypeId( ) ).size( ) );
        assertEquals( 1, PortletTemplateHome.findByPortletType( AppointmentFormPortletHome.getInstance( ).getPortletTypeId( ) ).size( ) );
        assertEquals( 1, PortletTemplateHome.findByPortletType( AppointmentFormListPortletHome.getInstance( ).getPortletTypeId( ) ).size( ) );
    }

    /**
     * The template chosen for a portlet is stored by the core
     */
    @Test
    public void testTemplateStoredWithThePortlet( )
    {
        PortletTemplate template = PortletTemplateHome.findByPortletType( _formPortlet.getPortletTypeId( ) ).get( 0 );
        _formPortlet.setIdTemplate( template.getId( ) );
        _formPortlet.update( );

        Portlet stored = PortletHome.findByPrimaryKey( _formPortlet.getId( ) );

        assertEquals( template.getId( ), stored.getIdTemplate( ), "the chosen template should be stored by the core with the portlet" );
        assertTrue( PortletTemplateHome.isTemplateUsed( template.getId( ) ), "a template chosen by a portlet is used" );
    }

    /**
     * The form portlet is rendered with every template of its type
     */
    @Test
    public void testRenderFormPortlet( )
    {
        List<PortletTemplate> listTemplates = PortletTemplateHome.findByPortletType( _formPortlet.getPortletTypeId( ) );

        for ( PortletTemplate template : listTemplates )
        {
            _formPortlet.setIdTemplate( template.getId( ) );
            String strContent = _formPortlet.getHtmlContent( _request );

            assertTrue( strContent.contains( MARKER_FORM_PORTLET ), template.getTemplatePath( ) + " should render the portlet wrapper" );
            assertTrue( strContent.contains( PORTLET_NAME ), template.getTemplatePath( ) + " should render the portlet title" );
            assertTrue( strContent.contains( FORM_TITLE ), template.getTemplatePath( ) + " should render the form title" );
            assertTrue( strContent.contains( "id_form=" + _nIdForm ), template.getTemplatePath( ) + " should link to the form calendar" );
            assertTrue( strContent.contains( "d-none" ), template.getTemplatePath( ) + " should render the device display classes" );
        }
    }

    /**
     * The form list portlet is rendered with every template of its type
     */
    @Test
    public void testRenderFormListPortlet( )
    {
        for ( PortletTemplate template : PortletTemplateHome.findByPortletType( _formListPortlet.getPortletTypeId( ) ) )
        {
            _formListPortlet.setIdTemplate( template.getId( ) );
            String strContent = _formListPortlet.getHtmlContent( _request );

            assertTrue( strContent.contains( MARKER_FORM_LIST_PORTLET ), template.getTemplatePath( ) + " should render the portlet wrapper" );
            assertTrue( strContent.contains( PORTLET_NAME ), template.getTemplatePath( ) + " should render the portlet title" );
            assertTrue( strContent.contains( FORM_TITLE ), template.getTemplatePath( ) + " should render the active forms" );
        }
    }

    /**
     * An unknown template falls back to the default template, and a hidden title is not rendered
     */
    @Test
    public void testFallbackToDefaultTemplate( )
    {
        _formPortlet.setIdTemplate( UNKNOWN_TEMPLATE_ID );
        _formPortlet.setDisplayPortletTitle( 1 );
        String strContent = _formPortlet.getHtmlContent( _request );

        assertTrue( strContent.contains( MARKER_FORM_PORTLET ), "the default template should render the portlet wrapper" );
        assertTrue( strContent.contains( FORM_TITLE ), "the default template should render the form title" );
        assertFalse( strContent.contains( PORTLET_NAME ), "a hidden portlet title should not be rendered" );
    }

    /**
     * The "My appointments" portlet renders nothing without a signed in user
     */
    @Test
    public void testMyAppointmentsPortletWithoutUser( )
    {
        AppointmentPortlet portlet = new AppointmentPortlet( );
        initPortlet( portlet );

        assertEquals( "", portlet.getHtmlContent( _request ), "nothing should be rendered without a signed in user" );
    }
}
