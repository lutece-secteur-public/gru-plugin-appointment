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
package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.display.DisplayHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;

/**
 * Service class for the display
 * 
 * @author Laurent Payen
 *
 */
public final class DisplayService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private DisplayService( )
    {
    }

    /**
     * Fill a display object with the appointment form DTO
     * 
     * @param display
     *            the display object
     * @param appointmentForm
     *            the appointmentform DTO
     * @param nIdForm
     *            the form Id
     * @return the display overload
     */
    public static Display fillInDisplayWithAppointmentForm( Display display, AppointmentFormDTO appointmentForm, int nIdForm )
    {
        display.setDisplayTitleFo( appointmentForm.getDisplayTitleFo( ) );
        display.setIcon( appointmentForm.getIcon( ) );
        display.setNbWeeksToDisplay( appointmentForm.getNbWeeksToDisplay( ) );
        display.setIsDisplayedOnPortlet( appointmentForm.getIsDisplayedOnPortlet( ) );
        display.setIdCalendarTemplate( appointmentForm.getCalendarTemplateId( ) );
        display.setIdForm( nIdForm );
        return display;
    }

    /**
     * Create a display object from an appointment form DTO
     * 
     * @param appointmentForm
     *            the appointment form DTO
     * @param nIdForm
     *            the form Id
     * @return the display object created
     */
    public static Display createDisplay( AppointmentFormDTO appointmentForm, int nIdForm )
    {
        Display display = new Display( );
        fillInDisplayWithAppointmentForm( display, appointmentForm, nIdForm );
        DisplayHome.create( display );
        return display;
    }

    /**
     * Save a display of a form
     * 
     * @param display
     *            the display to save
     */
    public static void saveDisplay( Display display )
    {
        DisplayHome.create( display );
    }

    /**
     * Update a display object with the values of an appointment form DTO
     * 
     * @param appointmentForm
     *            the appointment form DTO
     * @param nIdForm
     *            the form Id
     * @return the display object updated
     */
    public static Display updateDisplay( AppointmentFormDTO appointmentForm, int nIdForm )
    {
        Display display = DisplayService.findDisplayWithFormId( nIdForm );
        fillInDisplayWithAppointmentForm( display, appointmentForm, nIdForm );
        DisplayHome.update( display );
        return display;
    }

    /**
     * Find the display of the form
     * 
     * @param nIdForm
     *            the form Id
     * @return the display of the form
     */
    public static Display findDisplayWithFormId( int nIdForm )
    {
        return DisplayHome.findByIdForm( nIdForm );
    }

}
