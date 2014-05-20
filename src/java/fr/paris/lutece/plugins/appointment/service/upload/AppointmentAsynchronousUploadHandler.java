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
package fr.paris.lutece.plugins.appointment.service.upload;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.plugins.genericattributes.service.upload.AbstractAsynchronousUploadHandler;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import javax.servlet.http.HttpServletRequest;


/**
 * AppointmentAsynchronousUploadHandler.
 * @see #getFileItems(String, String)
 * @see #removeFileItem(String, String, int)
 */
public class AppointmentAsynchronousUploadHandler extends AbstractAsynchronousUploadHandler
{
    private static final String UPLOAD_SUBMIT_PREFIX = "_appointment_upload_submit_attribute";
    private static final String UPLOAD_DELETE_PREFIX = "_appointment_upload_delete_attribute";
    private static final String UPLOAD_CHECKBOX_PREFIX = "_appointment_upload_checkbox_attribute";
    private static final String PARAMETER_PAGE = "page";
    private static final String BEAN_APPOINTMENT_ASYNCHRONOUS_UPLOAD_HANDLER = "appointment.appointmentAsynchronousUploadHandler";

    /**
     * Get the handler
     * @return the handler
     */
    public static AppointmentAsynchronousUploadHandler getHandler( )
    {
        return SpringContextService.getBean( BEAN_APPOINTMENT_ASYNCHRONOUS_UPLOAD_HANDLER );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvoked( HttpServletRequest request )
    {
        return AppointmentPlugin.PLUGIN_NAME.equals( request.getParameter( PARAMETER_PAGE ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUploadSubmitPrefix( )
    {
        return UPLOAD_SUBMIT_PREFIX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUploadDeletePrefix( )
    {
        return UPLOAD_DELETE_PREFIX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUploadCheckboxPrefix( )
    {
        return UPLOAD_CHECKBOX_PREFIX;
    }
}
