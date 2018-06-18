/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.appointment.service;

import java.text.DateFormat;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.dozer.converters.DateConverter;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Appointment plugin
 * 
 * @author Laurent Payen
 * 
 */
public final class AppointmentPlugin extends Plugin
{
    /**
     * Name of the appointment plugin
     */
    public static final String PLUGIN_NAME = "appointment";

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( )
    {
        BeanUtilsBean.getInstance( ).getConvertUtils( )
                .register( new DateConverter( DateFormat.getDateInstance( DateFormat.SHORT, getPluginLocale( Locale.FRANCE ) ) ), java.sql.Date.class );
    }

    /**
     * Get the locale used by this plugin
     * 
     * @param locale
     *            The locale preferred by the user
     * @return The locale used by this plugin
     */
    public static Locale getPluginLocale( Locale locale )
    {
        return Locale.FRANCE;
    }
}
