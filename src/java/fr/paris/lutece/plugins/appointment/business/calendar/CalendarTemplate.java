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
package fr.paris.lutece.plugins.appointment.business.calendar;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * The business class of the Calendar Template
 * 
 * @author Laurent Payen
 *
 */
public final class CalendarTemplate implements Serializable
{

    public static final String CALENDAR = "Calendrier";

    public static final String FREE_SLOTS_GROUPED = "Liste des creneaux disponible regroupés";
    public static final String CALENDAR_OPEN_DAYS = "Calendrier jours ouverts";
    public static final String FREE_SLOTS = "Liste des creneaux disponibles";
    public static final String FREE_SLOTS_ON_OPEN_DAYS = "Liste des creneaux disponibles jours ouverts";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 8029294463873867355L;

    /**
     * Calendar Template Id
     */
    private int _nIdCalendarTemplate;

    /**
     * Calendar Title
     */
    @NotBlank( message = "#i18n{appointment.calendarTemplate.labelTitleBlank}" )
    @Size( max = 255, message = "#i18n{appointment.labelTemplatePathSize}" )
    private String _strTitle;

    /**
     * Calendar Description
     */
    @NotBlank( message = "#i18n{appointment.calendarTemplate.labelDescriptionBlank}" )
    @Size( max = 255, message = "#i18n{appointment.calendarTemplate.labelDescriptionSize}" )
    private String _strDescription;

    /**
     * Path for the template
     */
    @NotBlank( message = "#i18n{appointment.calendarTemplate.labelTemplatePathBlank}" )
    @Size( max = 255, message = "#i18n{appointment.labelTemplatePathSize}" )
    private String _strTemplatePath;

    /**
     * Get the id of the template
     * 
     * @return The id of the template
     */
    public int getIdCalendarTemplate( )
    {
        return _nIdCalendarTemplate;
    }

    /**
     * Set the id of the template
     * 
     * @param nId
     *            The id of the template
     */
    public void setIdCalendarTemplate( int nIdCalendarTemplate )
    {
        this._nIdCalendarTemplate = nIdCalendarTemplate;
    }

    /**
     * Get the title of the template
     * 
     * @return The title of the template
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Set the title of the template
     * 
     * @param strTitle
     *            The title of the template
     */
    public void setTitle( String strTitle )
    {
        this._strTitle = strTitle;
    }

    /**
     * Get the description of the template
     * 
     * @return The description of the template
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Set the description of the template
     * 
     * @param strDescription
     *            The description of the template
     */
    public void setDescription( String strDescription )
    {
        this._strDescription = strDescription;
    }

    /**
     * Get the path of the file of the template
     * 
     * @return The path of the file of the template
     */
    public String getTemplatePath( )
    {
        return _strTemplatePath;
    }

    /**
     * Set the path of the file of the template
     * 
     * @param strTemplatePath
     *            The path of the file of the template
     */
    public void setTemplatePath( String strTemplatePath )
    {
        this._strTemplatePath = strTemplatePath;
    }
}
