/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * This class provides a simple implementation of an XPage
 */
@Controller( xpageName = "appointment", pageTitleProperty = "myplugin.pageTitle", pagePathProperty = "myplugin.pagePathLabel" )
public class AppointmentApp extends MVCApplication
{
    private static final String TEMPLATE_XPAGE = "/skin/plugins/appointment/appointment.html";
    private static final String TEMPLATE_APPOINTMENT_FORM_LIST = "/skin/pluigins/appointment/appointment_form_list.html";
    private static final String TEMPLATE_APPOINTMENT_FORM = "/skin/pluigins/appointment/appointment_form.html";

    private static final String VIEW_HOME = "home";
    private static final String VIEW_APPOINTMENT_FORM_LIST = "getViewFormList";
    private static final String VIEW_GET_FORM = "viewForm";

    private static final String PARAMETER_ID_FORM = "id_form";

    private static final String MARK_FORM_LIST = "form_list";

    /**
     * Returns the content of the page appointment.
     * @param request The HTTP request
     * @return The view
     */
    @View( VIEW_HOME )
    public XPage viewHome( HttpServletRequest request )
    {
        return getXPage( TEMPLATE_XPAGE, request.getLocale( ) );
    }

    /**
     * Get the list of appointment form list
     * @param request The request
     * @return The XPage to display
     */
    @View( value = VIEW_APPOINTMENT_FORM_LIST, defaultView = true )
    public XPage getFormList( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>( );

        Collection<AppointmentForm> listAppointmentForm = AppointmentFormHome.getAppointmentFormsList( );
        model.put( MARK_FORM_LIST, listAppointmentForm );
        return getXPage( TEMPLATE_APPOINTMENT_FORM_LIST, request.getLocale( ), model );
    }

    @View( VIEW_GET_FORM )
    public XPage getViewForm( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( strIdForm != null && StringUtils.isNumeric( PARAMETER_ID_FORM ) )
        {
            int nIdForm = Integer.parseInt( strIdForm );

            // TODO : implement me !

            Map<String, Object> model = new HashMap<String, Object>( );

            Collection<AppointmentForm> listAppointmentForm = AppointmentFormHome.getAppointmentFormsList( );
            model.put( MARK_FORM_LIST, listAppointmentForm );
            return getXPage( TEMPLATE_APPOINTMENT_FORM, request.getLocale( ), model );
        }
        return redirectView( request, VIEW_APPOINTMENT_FORM_LIST );
    }
}