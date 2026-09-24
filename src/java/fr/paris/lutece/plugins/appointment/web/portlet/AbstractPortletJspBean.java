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
package fr.paris.lutece.plugins.appointment.web.portlet;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;

/**
 * Create and modify screens shared by the appointment portlets.
 */
public abstract class AbstractPortletJspBean extends PortletJspBean
{

    private static final long serialVersionUID = 8507575062494354655L;
    private static final String MESSAGE_PORTLET_TYPE_NOT_FOUND = "appointment.message.portletTypeNotFound";
    private static final String MESSAGE_PORTLET_NOT_FOUND = "appointment.message.portletNotFound";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreate( HttpServletRequest request )
    {
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );

        if ( !StringUtils.isNumeric( strPageId ) || StringUtils.isEmpty( strPortletTypeId ) || PortletTypeHome.findByPrimaryKey( strPortletTypeId ) == null )
        {
            return I18nService.getLocalizedString( MESSAGE_PORTLET_TYPE_NOT_FOUND, getLocale( ) );
        }

        return getCreateTemplate( strPageId, strPortletTypeId, getPortletModel( ) ).getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModify( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        Portlet portlet = StringUtils.isNumeric( strPortletId ) ? PortletHome.findByPrimaryKey( Integer.parseInt( strPortletId ) ) : null;

        if ( portlet == null )
        {
            return I18nService.getLocalizedString( MESSAGE_PORTLET_NOT_FOUND, getLocale( ) );
        }

        return getModifyTemplate( portlet, getPortletModel( ) ).getHtml( );
    }

    /**
     * Returns the model the create and modify forms of the portlet need beyond the common portlet fields.
     *
     * @return the model, empty by default
     */
    protected Map<String, Object> getPortletModel( )
    {
        return new HashMap<>( );
    }
}
