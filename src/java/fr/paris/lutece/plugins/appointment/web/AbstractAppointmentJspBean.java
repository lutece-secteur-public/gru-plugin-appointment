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

import java.util.Map;

import fr.paris.lutece.portal.service.security.SecurityTokenHandler;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Common class of the back office controllers of the plugin: their security token is enabled, and every view and action uses the same token
 * key, so that a page of the plugin can post any action of the plugin (forms, modals of the calendars, confirmations of removal)
 */
public abstract class AbstractAppointmentJspBean extends MVCAdminJspBean
{
    /**
     * The key of the security token shared by the views and the actions of the back office controllers of the plugin
     */
    public static final String SECURITY_TOKEN_ACTION = "appointment.admin";

    private static final long serialVersionUID = -2410845203569818716L;

    /**
     * {@inheritDoc} The token of the page is put in the model, so that the core adds it to every form of the page
     */
    @Override
    protected String getPage( String strPageTitleProperty, String strTemplate, Map<String, Object> model )
    {
        String strToken = getPageSecurityToken( );
        if ( strToken != null && model != null && !model.containsKey( SecurityTokenHandler.MARK_CSRF_TOKEN ) )
        {
            model.put( SecurityTokenHandler.MARK_CSRF_TOKEN, strToken );
        }
        return super.getPage( strPageTitleProperty, strTemplate, model );
    }

    /**
     * {@inheritDoc} The token of the page is put in the model, so that the core adds it to every form of the page
     */
    @Override
    protected String getPage( String strPageTitleProperty, String strTemplate, Models model )
    {
        String strToken = getPageSecurityToken( );
        if ( strToken != null && model != null && model.get( SecurityTokenHandler.MARK_CSRF_TOKEN ) == null )
        {
            model.put( SecurityTokenHandler.MARK_CSRF_TOKEN, strToken );
        }
        return super.getPage( strPageTitleProperty, strTemplate, model );
    }

    /**
     * Get the security token generated for the current view or action
     *
     * @return the token, or null when the request has none
     */
    private static String getPageSecurityToken( )
    {
        HttpServletRequest request = LocalVariables.getRequest( );
        if ( request == null )
        {
            return null;
        }
        return CDI.current( ).select( SecurityTokenHandler.class ).get( ).resolveTokenValue( request );
    }
}
