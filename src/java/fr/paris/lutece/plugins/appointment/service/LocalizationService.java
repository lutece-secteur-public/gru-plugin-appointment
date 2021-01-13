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

import fr.paris.lutece.plugins.appointment.business.localization.Localization;
import fr.paris.lutece.plugins.appointment.business.localization.LocalizationHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;

/**
 * Service class for the localization
 * 
 * @author Laurent Payen
 *
 */
public final class LocalizationService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private LocalizationService( )
    {
    }

    /**
     * Fill a localization object with the appointment form DTO
     * 
     * @param localization
     *            the localization object
     * @param appointmentForm
     *            the appointmentform DTO
     * @param nIdForm
     *            the form Id
     * @return the localization overload
     */
    public static Localization fillInLocalizationWithAppointmentForm( Localization localization, AppointmentFormDTO appointmentForm, int nIdForm )
    {
        localization.setLongitude( appointmentForm.getLongitude( ) );
        localization.setLatitude( appointmentForm.getLatitude( ) );
        localization.setAddress( appointmentForm.getAddress( ) );
        localization.setIdForm( nIdForm );
        return localization;
    }

    /**
     * Create a localization object from an appointment form DTO
     * 
     * @param appointmentForm
     *            the appointment form DTO
     * @param nIdForm
     *            the form Id
     * @return the display object created
     */
    public static Localization createLocalization( AppointmentFormDTO appointmentForm, int nIdForm )
    {
        Localization localization = new Localization( );
        fillInLocalizationWithAppointmentForm( localization, appointmentForm, nIdForm );
        LocalizationHome.create( localization );
        return localization;
    }

    /**
     * Save a localization of a form
     * 
     * @param localization
     *            the localization to save
     */
    public static void saveLocalization( Localization localization )
    {
        LocalizationHome.create( localization );
    }

    /**
     * Update a localization object with the values of an appointment form DTO
     * 
     * @param appointmentForm
     *            the appointment form DTO
     * @param nIdForm
     *            the form Id
     * @return the localization object updated
     */
    public static Localization updateLocalization( AppointmentFormDTO appointmentForm, int nIdForm )
    {
        Localization localization = LocalizationService.findLocalizationWithFormId( nIdForm );
        fillInLocalizationWithAppointmentForm( localization, appointmentForm, nIdForm );
        LocalizationHome.update( localization );
        return localization;
    }

    /**
     * Find the Localization of the form
     * 
     * @param nIdForm
     *            the form Id
     * @return the Localization of the form
     */
    public static Localization findLocalizationWithFormId( int nIdForm )
    {
        return LocalizationHome.findByIdForm( nIdForm );
    }

}
