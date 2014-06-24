/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentFormService;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents business objects AppointmentPortlet
 */
public class AppointmentFormPortlet extends PortletHtmlContent
{
    private final AppointmentFormService _appointmentFormService = SpringContextService.getBean( AppointmentFormService.BEAN_NAME );
    private int _nIdAppointmentForm;

    /////////////////////////////////////////////////////////////////////////////////
    // Constants

    /**
     * Sets the identifier of the portlet type to value specified
     */
    public AppointmentFormPortlet(  )
    {
        setPortletTypeId( AppointmentFormPortletHome.getInstance(  ).getPortletTypeId(  ) );
    }

    /**
     * Returns the HTML code of the AppointmentPortlet portlet
     * @param request The HTTP servlet request
     * @return The HTML code of the AppointmentPortlet portlet
     */
    @Override
    public String getHtmlContent( HttpServletRequest request )
    {
        if ( ( request != null ) && ( _nIdAppointmentForm > 0 ) )
        {
            AppointmentForm form = AppointmentFormHome.findByPrimaryKey( _nIdAppointmentForm );

            if ( ( form != null ) && form.getIsActive(  ) )
            {
                return AppointmentApp.getHtmlFormFirstStep( request, form, _appointmentFormService,
                    new HashMap<String, Object>(  ), request.getLocale(  ) );
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * Updates the current instance of the AppointmentPortlet object
     */
    public void update(  )
    {
        AppointmentFormPortletHome.getInstance(  ).update( this );
    }

    /**
     * Removes the current instance of the AppointmentPortlet object
     */
    @Override
    public void remove(  )
    {
        AppointmentFormPortletHome.getInstance(  ).remove( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeCachedForConnectedUsers(  )
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeCachedForAnonymousUsers(  )
    {
        return false;
    }

    /**
     * Get the id of the appointment form to display
     * @return The id of the appointment form to display
     */
    public int getIdAppointmentForm(  )
    {
        return _nIdAppointmentForm;
    }

    /**
     * Set the id of the appointment form to display
     * @param nIdAppointmentForm The id of the appointment form to display
     */
    public void setIdAppointmentForm( int nIdAppointmentForm )
    {
        this._nIdAppointmentForm = nIdAppointmentForm;
    }
}
