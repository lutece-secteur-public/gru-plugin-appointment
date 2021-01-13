/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.appointment.web.portlet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.portlet.AppointmentFormPortlet;
import fr.paris.lutece.plugins.appointment.business.portlet.AppointmentFormPortletHome;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class provides the user interface to manage AppointmentPortlet features
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentFormPortletJspBean extends AbstractPortletJspBean
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 5342937491389478335L;

    // Marks
    private static final String MARK_LIST_APPOINTMENT_FORM = "refListAppointmentForm";

    // Parameters
    private static final String PARAMETER_FORM = "id_form";

    // Messages
    private static final String MESSAGE_ERROR_NO_APPOINTMENT_FORM_SELECTED = "appointment.message.error.noAppointmentFormSelected";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreate( HttpServletRequest request )
    {
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );

        Collection<Form> listIsActiveAndIsDisplayedOnPortletAppointmentForm = FormService.findAllActiveAndDisplayedOnPortletForms( );

        ReferenceList refListAppointmentForm = new ReferenceList( );

        for ( Form form : listIsActiveAndIsDisplayedOnPortletAppointmentForm )
        {
            refListAppointmentForm.addItem( form.getIdForm( ), form.getTitle( ) );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_LIST_APPOINTMENT_FORM, refListAppointmentForm );

        HtmlTemplate template = getCreateTemplate( strPageId, strPortletTypeId, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModify( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        AppointmentFormPortlet portlet = (AppointmentFormPortlet) PortletHome.findByPrimaryKey( nPortletId );

        Collection<Form> listIsActiveAndIsDisplayedOnPortletAppointmentForm = FormService.findAllActiveAndDisplayedOnPortletForms( );

        ReferenceList refListAppointmentForm = new ReferenceList( );

        for ( Form form : listIsActiveAndIsDisplayedOnPortletAppointmentForm )
        {
            refListAppointmentForm.addItem( form.getIdForm( ), form.getTitle( ) );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_LIST_APPOINTMENT_FORM, refListAppointmentForm );

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doCreate( HttpServletRequest request )
    {
        AppointmentFormPortlet portlet = new AppointmentFormPortlet( );

        // recovers portlet specific attributes
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        int nPageId = Integer.parseInt( strPageId );

        String strFormId = request.getParameter( PARAMETER_FORM );

        if ( StringUtils.isNotEmpty( strFormId ) && StringUtils.isNumeric( strFormId ) )
        {
            int nIdForm = Integer.parseInt( strFormId );
            portlet.setIdAppointmentForm( nIdForm );
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NO_APPOINTMENT_FORM_SELECTED, AdminMessage.TYPE_STOP );
        }

        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.setPageId( nPageId );

        // Creates the portlet
        AppointmentFormPortletHome.getInstance( ).create( portlet );

        // Displays the page with the new Portlet
        return getPageUrl( nPageId );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doModify( HttpServletRequest request )
    {
        // fetches portlet attributes
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        AppointmentFormPortlet portlet = (AppointmentFormPortlet) PortletHome.findByPrimaryKey( nPortletId );

        String strFormId = request.getParameter( PARAMETER_FORM );

        if ( StringUtils.isNotEmpty( strFormId ) && StringUtils.isNumeric( strFormId ) )
        {
            int nIdForm = Integer.parseInt( strFormId );
            portlet.setIdAppointmentForm( nIdForm );
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NO_APPOINTMENT_FORM_SELECTED, AdminMessage.TYPE_STOP );
        }

        // retrieve portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        // updates the portlet
        portlet.update( );

        // displays the page with the updated portlet
        return getPageUrl( portlet.getPageId( ) );
    }
}
