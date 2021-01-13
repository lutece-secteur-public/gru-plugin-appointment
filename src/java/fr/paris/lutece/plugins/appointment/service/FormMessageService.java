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

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.message.FormMessageHome;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Service class of the form message
 * 
 * @author Laurent Payen
 *
 */
public final class FormMessageService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private FormMessageService( )
    {
    }

    /**
     * Name of the bean of the service
     */
    public static final String BEAN_NAME = "appointment.formMessageService";

    private static final String PROPERTY_DEFAULT_CALENDAR_TITLE = "appointment.formMessages.defaultCalendarTitle";
    private static final String PROPERTY_DEFAULT_FIELD_FIRST_NAME_TITLE = "appointment.formMessages.defaultFieldFirstNameTitle";
    private static final String PROPERTY_DEFAULT_FIELD_FIRST_NAME_HELP = "appointment.formMessages.defaultFieldFirstNameHelp";
    private static final String PROPERTY_DEFAULT_FIELD_LAST_NAME_TITLE = "appointment.formMessages.defaultFieldLastNameTitle";
    private static final String PROPERTY_DEFAULT_FIELD_LAST_NAME_HELP = "appointment.formMessages.defaultFieldLastNameHelp";
    private static final String PROPERTY_DEFAULT_FIELD_EMAIL_TITLE = "appointment.formMessages.defaultFieldEmailTitle";
    private static final String PROPERTY_DEFAULT_FIELD_EMAIL_HELP = "appointment.formMessages.defaultFieldEmailHelp";
    private static final String PROPERTY_DEFAULT_FIELD_CONFIRMATION_EMAIL_TITLE = "appointment.formMessages.defaultFieldConfirmationEmailTitle";
    private static final String PROPERTY_DEFAULT_FIELD_CONFIRMATION_EMAIL_HELP = "appointment.formMessages.defaultFieldConfirmationEmailHelp";
    private static final String PROPERTY_DEFAULT_URL_REDIRECTION = "appointment.formMessages.defaultUrlRedirection";
    private static final String PROPERTY_DEFAULT_LABEL_BUTTON_REDIRECT = "appointment.formMessages.defaultLabelButtonRedirect";
    private static final String PROPERTY_DEFAULT_TEXT_APPOINTMENT_CREATED = "appointment.formMessages.defaultTextAppointmentCreated";
    private static final String PROPERTY_DEFAULT_TEXT_APPOINTMENT_CANCELED = "appointment.formMessages.defaultTextAppointmentCanceled";
    private static final String PROPERTY_DEFAULT_NO_AVAILABLE_SLOT = "appointment.formMessages.defaultNoAvailableSlot";
    private static final String PROPERTY_DEFAULT_CALENDAR_DESCRIPTION = "appointment.formMessages.defaultCalendarDescription";
    private static final String PROPERTY_DEFAULT_CALENDAR_RESERVE_LABEL = "appointment.formMessages.defaultCalendarReserveLabel";
    private static final String PROPERTY_DEFAULT_CALENDAR_FULL_LABEL = "appointment.formMessages.defaultCalendarFullLabel";

    /**
     * Create a default form message for a form
     * 
     * @param nIdForm
     *            the form Id
     */
    public static void createFormMessageWithDefaultValues( int nIdForm )
    {
        FormMessage formMessage = getDefaultAppointmentFormMessage( );
        formMessage.setIdForm( nIdForm );
        FormMessageHome.create( formMessage );
    }

    /**
     * Save a form message
     * 
     * @param formMessage
     *            the form message to save
     */
    public static void saveFormMessage( FormMessage formMessage )
    {
        FormMessageHome.create( formMessage );
    }

    /**
     * Get the default form message with values loaded from properties.
     * 
     * @return The default form message. The form message is not associated with any appointment form
     */
    public static FormMessage getDefaultAppointmentFormMessage( )
    {
        FormMessage formMessage = new FormMessage( );
        formMessage.setCalendarTitle( AppPropertiesService.getProperty( PROPERTY_DEFAULT_CALENDAR_TITLE, StringUtils.EMPTY ) );
        formMessage.setFieldFirstNameTitle( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_FIRST_NAME_TITLE, StringUtils.EMPTY ) );
        formMessage.setFieldFirstNameHelp( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_FIRST_NAME_HELP, StringUtils.EMPTY ) );
        formMessage.setFieldLastNameTitle( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_LAST_NAME_TITLE, StringUtils.EMPTY ) );
        formMessage.setFieldLastNameHelp( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_LAST_NAME_HELP, StringUtils.EMPTY ) );
        formMessage.setFieldEmailTitle( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_EMAIL_TITLE, StringUtils.EMPTY ) );
        formMessage.setFieldEmailHelp( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_EMAIL_HELP, StringUtils.EMPTY ) );
        formMessage.setFieldConfirmationEmail( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_CONFIRMATION_EMAIL_TITLE, StringUtils.EMPTY ) );
        formMessage.setFieldConfirmationEmailHelp( AppPropertiesService.getProperty( PROPERTY_DEFAULT_FIELD_CONFIRMATION_EMAIL_HELP, StringUtils.EMPTY ) );
        formMessage.setUrlRedirectAfterCreation( AppPropertiesService.getProperty( PROPERTY_DEFAULT_URL_REDIRECTION, StringUtils.EMPTY ) );
        formMessage.setLabelButtonRedirection( AppPropertiesService.getProperty( PROPERTY_DEFAULT_LABEL_BUTTON_REDIRECT, StringUtils.EMPTY ) );
        formMessage.setTextAppointmentCreated( AppPropertiesService.getProperty( PROPERTY_DEFAULT_TEXT_APPOINTMENT_CREATED, StringUtils.EMPTY ) );
        formMessage.setTextAppointmentCanceled( AppPropertiesService.getProperty( PROPERTY_DEFAULT_TEXT_APPOINTMENT_CANCELED, StringUtils.EMPTY ) );
        formMessage.setNoAvailableSlot( AppPropertiesService.getProperty( PROPERTY_DEFAULT_NO_AVAILABLE_SLOT, StringUtils.EMPTY ) );
        formMessage.setCalendarDescription( AppPropertiesService.getProperty( PROPERTY_DEFAULT_CALENDAR_DESCRIPTION, StringUtils.EMPTY ) );
        formMessage.setCalendarReserveLabel( AppPropertiesService.getProperty( PROPERTY_DEFAULT_CALENDAR_RESERVE_LABEL, StringUtils.EMPTY ) );
        formMessage.setCalendarFullLabel( AppPropertiesService.getProperty( PROPERTY_DEFAULT_CALENDAR_FULL_LABEL, StringUtils.EMPTY ) );

        return formMessage;
    }

    /**
     * Find the form messages of a form
     * 
     * @param nIdForm
     *            the form id
     * @return the form message object
     */
    public static FormMessage findFormMessageByIdForm( int nIdForm )
    {
        return FormMessageHome.findByIdForm( nIdForm );
    }

    /**
     * Update a form message
     * 
     * @param formMessage
     *            the formMessage updated
     */
    public static void updateFormMessage( FormMessage formMessage )
    {
        FormMessageHome.update( formMessage );
    }

}
