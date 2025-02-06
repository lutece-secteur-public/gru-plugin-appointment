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

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;
import fr.paris.lutece.portal.web.l10n.LocaleService;

/**
 * This class represents business objects AppointmentPortlet
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentFormListPortlet extends PortletHtmlContent
{
    // MARKERS
    public static final String MARK_PORTLET_NAME = "portlet_name";

    /**
     * Sets the identifier of the portlet type to value specified
     */
    public AppointmentFormListPortlet( )
    {
        setPortletTypeId( AppointmentFormListPortletHome.getInstance( ).getPortletTypeId( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlContent( HttpServletRequest request )
    {
        Locale locale = LocaleService.getContextUserLocale( request );
        HashMap<String, Object> model = new HashMap<>( );

        // Get the portlet's custom title if it has to be displayed
        if ( this.getDisplayPortletTitle( ) == 0 )
        {
            model.put( MARK_PORTLET_NAME, this.getName( ) );
        }
        // If the portlet's title should not be displayed, then set an empty value for it
        else
        {
            model.put( MARK_PORTLET_NAME, StringUtils.EMPTY );
        }

        return AppointmentApp.getFormListHtml( locale, model );
    }

    /**
     * Updates the current instance of the AppointmentFormListPortlet object
     */
    public void update( )
    {
        AppointmentFormListPortletHome.getInstance( ).update( this );
    }

    /**
     * Removes the current instance of the AppointmentFormListPortlet object
     */
    @Override
    public void remove( )
    {
        AppointmentFormListPortletHome.getInstance( ).remove( this );
    }
}
