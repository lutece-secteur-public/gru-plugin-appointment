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
package fr.paris.lutece.plugins.appointment.business.display;

import java.io.Serializable;

import javax.validation.constraints.Min;

import fr.paris.lutece.portal.service.image.ImageResource;

/**
 * Business class of the Form Display
 * 
 * @author Laurent Payen
 *
 */
public final class Display implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -4827729906365306894L;

    /**
     * Display Id
     */
    private int _nIdDisplay;

    /**
     * Indicate whether the title is displayed in the front office or not
     */
    private boolean _bIsDisplayTitleFo;

    /**
     * Indicate whether the form is displayed on the front office portlet or not
     */
    private boolean _bIsDisplayedOnPortlet;

    /**
     * Form Icon
     */
    private ImageResource _icon;

    /**
     * Number of weeks during which the form is displayed to the user
     */
    @Min( value = 0, message = "#i18n{appointment.validation.appointmentform.fromTimeSeizure.notEmpty}" )
    private int _nbWeeksToDisplay;

    /**
     * Calendar Template Id of the Display Form (foreign key)
     */
    private int _nIdCalendarTemplate;

    /**
     * Form id (foreign key)
     */
    private int _nIdForm;

    /**
     * Get the Display Id
     * 
     * @return the Display Id
     */
    public int getIdDisplay( )
    {
        return _nIdDisplay;
    }

    /**
     * Set the Display Id
     * 
     * @param _nIdDisplay
     *            the Id to set
     */
    public void setIdDisplay( int nIdDisplay )
    {
        this._nIdDisplay = nIdDisplay;
    }

    /**
     * Get the display title value for the front office form
     * 
     * @return true if the title has to be displayed
     */
    public boolean isDisplayTitleFo( )
    {
        return _bIsDisplayTitleFo;
    }

    /**
     * Get the boolean value for displayed or not the form on the portlet
     * 
     * @return true if the form has to be displayed on the portlet
     */
    public boolean isDisplayedOnPortlet( )
    {
        return _bIsDisplayedOnPortlet;
    }

    /**
     * Set the boolean value to display or not the form on the portlet
     * 
     * @param bIsDisplayedOnPortlet
     */
    public void setIsDisplayedOnPortlet( boolean bIsDisplayedOnPortlet )
    {
        this._bIsDisplayedOnPortlet = bIsDisplayedOnPortlet;
    }

    /**
     * Set the display title boolean value
     * 
     * @param displayTitleFo
     *            the boolean display title value to set
     */
    public void setDisplayTitleFo( boolean bIsDisplayTitleFo )
    {
        this._bIsDisplayTitleFo = bIsDisplayTitleFo;
    }

    /**
     * Get the form icon
     * 
     * @return the form icon
     */
    public ImageResource getIcon( )
    {
        return _icon;
    }

    /**
     * Set the form icon
     * 
     * @param icon
     *            the icon to set
     */
    public void setIcon( ImageResource icon )
    {
        this._icon = icon;
    }

    /**
     * Get the number of weeks during which the form is displayed to the user
     * 
     * @return the number of weeks
     */
    public int getNbWeeksToDisplay( )
    {
        return _nbWeeksToDisplay;
    }

    /**
     * Set the number of weeks during which the form is displayed to the user
     * 
     * @param nbWeeksToDisplay
     *            the number of weeks to set
     */
    public void setNbWeeksToDisplay( int nbWeeksToDisplay )
    {
        this._nbWeeksToDisplay = nbWeeksToDisplay;
    }

    /**
     * Get the Calendar Template Id
     * 
     * @return the Calendar Template Id
     */
    public int getIdCalendarTemplate( )
    {
        return _nIdCalendarTemplate;
    }

    /**
     * Set the Calendar Template Id
     * 
     * @param nIdCalendarTemplate
     *            the Calendar Template Id to set
     */
    public void setIdCalendarTemplate( int nIdCalendarTemplate )
    {
        this._nIdCalendarTemplate = nIdCalendarTemplate;
    }

    /**
     * Get the Form Id
     * 
     * @return the Form Id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the FOrm Id
     * 
     * @param nIdForm
     *            the Form Id to set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

}
