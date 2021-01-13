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
package fr.paris.lutece.plugins.appointment.business.message;

import java.io.Serializable;

/**
 * Business class of the Form Message
 * 
 * @author Laurent Payen
 *
 */
public final class FormMessage implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 8770406931955371701L;

    /**
     * Form Message ID
     */
    private int _nIdFormMessage;

    /**
     * Title of the page of the calendar in FO
     */
    private String _strCalendarTitle;

    /**
     * Title of the first name field in the form page in FO
     */
    private String _strFieldFirstNameTitle;

    /**
     * Help message of the first name field in the form page in FO
     */
    private String _strFieldFirstNameHelp;

    /**
     * Title of the last name field in the form page in FO
     */
    private String _strFieldLastNameTitle;

    /**
     * Help message of the last name field in the form page in FO
     */
    private String _strFieldLastNameHelp;

    /**
     * Title of the email field in the form page in FO
     */
    private String _strFieldEmailTitle;

    /**
     * Help message of the email field in the form page in FO
     */
    private String _strFieldEmailHelp;

    /**
     * Field confirmation email
     */
    private String _strFieldConfirmationEmail;

    /**
     * Field confirmation email help
     */
    private String _strFieldConfirmationEmailHelp;

    /**
     * URL to redirect the user to after creation of an appointment
     */
    private String _strUrlRedirectAfterCreation;

    /**
     * Text to display to the user after the creation of an appointment
     */
    private String _strTextAppointmentCreated;

    /**
     * Text to display to the user after the canceling of an appointment
     */
    private String _strTextAppointmentCanceled;

    /**
     * Label of the button after the creation of an appointment
     */
    private String _strLabelButtonRedirection;

    /**
     * Message to display when there is no available slot for the associated form
     */
    private String _strNoAvailableSlot;

    /**
     * Calendar description
     */
    private String _strCalendarDescription;

    /**
     * Label that indicates that a slot is open for reservation
     */
    private String _strCalendarReserveLabel;

    /**
     * Label that indicates that a slot is full
     */
    private String _strCalendarFullLabel;

    /**
     * Form Id (foreign Key)
     */
    private int _nIdForm;

    /**
     * Get the Form Message Id
     * 
     * @return the FOrm Message Id
     */
    public int getIdFormMessage( )
    {
        return _nIdFormMessage;
    }

    /**
     * Set the Form Message Id
     * 
     * @param nIdFormMessage
     *            the Id to set
     */
    public void setIdFormMessage( int nIdFormMessage )
    {
        this._nIdFormMessage = nIdFormMessage;
    }

    /**
     * Get the title of the page of the calendar in FO
     * 
     * @return the title of the page of the calendar in FO
     */
    public String getCalendarTitle( )
    {
        return _strCalendarTitle;
    }

    /**
     * Set the title of the page of the calendar in FO
     * 
     * @param strCalendarTitle
     *            The title of the page of the calendar in FO
     */
    public void setCalendarTitle( String strCalendarTitle )
    {
        this._strCalendarTitle = strCalendarTitle;
    }

    /**
     * Get the title of the first name field in the form page in FO
     * 
     * @return The title of the first name field in the form page in FO
     */
    public String getFieldFirstNameTitle( )
    {
        return _strFieldFirstNameTitle;
    }

    /**
     * Set the title of the first name field in the form page in FO
     * 
     * @param strFieldFirstNameTitle
     *            The title of the first name field in the form page in FO
     */
    public void setFieldFirstNameTitle( String strFieldFirstNameTitle )
    {
        this._strFieldFirstNameTitle = strFieldFirstNameTitle;
    }

    /**
     * Get the help message of the first name field in the form page in FO
     * 
     * @return The help message of the first name field in the form page in FO
     */
    public String getFieldFirstNameHelp( )
    {
        return _strFieldFirstNameHelp;
    }

    /**
     * Set the help message of the first name field in the form page in FO
     * 
     * @param strFieldFirstNameHelp
     *            The help message of the first name field in the form page in FO
     */
    public void setFieldFirstNameHelp( String strFieldFirstNameHelp )
    {
        this._strFieldFirstNameHelp = strFieldFirstNameHelp;
    }

    /**
     * Get the title of the last name field in the form page in FO
     * 
     * @return The title of the last name field in the form page in FO
     */
    public String getFieldLastNameTitle( )
    {
        return _strFieldLastNameTitle;
    }

    /**
     * Set the title of the last name field in the form page in FO
     * 
     * @param strFieldLastNameTitle
     *            The title of the last name field in the form page in FO
     */
    public void setFieldLastNameTitle( String strFieldLastNameTitle )
    {
        this._strFieldLastNameTitle = strFieldLastNameTitle;
    }

    /**
     * Get the help message of the last name field in the form page in FO
     * 
     * @return The help message of the last name field in the form page in FO
     */
    public String getFieldLastNameHelp( )
    {
        return _strFieldLastNameHelp;
    }

    /**
     * Get the help message of the last name field in the form page in FO
     * 
     * @param strFieldLastNameHelp
     *            The help message of the last name field in the form page in FO
     */
    public void setFieldLastNameHelp( String strFieldLastNameHelp )
    {
        this._strFieldLastNameHelp = strFieldLastNameHelp;
    }

    /**
     * Get the title of the email field in the form page in FO
     * 
     * @return The title of the email field in the form page in FO
     */
    public String getFieldEmailTitle( )
    {
        return _strFieldEmailTitle;
    }

    /**
     * Set the title of the email field in the form page in FO
     * 
     * @param strFieldEmailTitle
     *            The title of the email field in the form page in FO
     */
    public void setFieldEmailTitle( String strFieldEmailTitle )
    {
        this._strFieldEmailTitle = strFieldEmailTitle;
    }

    /**
     * Get the help message of the email field in the form page in FO
     * 
     * @return The help message of the email field in the form page in FO
     */
    public String getFieldEmailHelp( )
    {
        return _strFieldEmailHelp;
    }

    /**
     * Set the help message of the email field in the form page in FO
     * 
     * @param strFieldEmailHelp
     *            The help message of the email field in the form page in FO
     */
    public void setFieldEmailHelp( String strFieldEmailHelp )
    {
        this._strFieldEmailHelp = strFieldEmailHelp;
    }

    /**
     * Get Field confirmation email
     * 
     * @return The field of confirmation email
     */
    public String getFieldConfirmationEmail( )
    {
        return _strFieldConfirmationEmail;
    }

    /**
     * Set field confirmation email
     * 
     * @param strFieldConfirmationEmail
     */
    public void setFieldConfirmationEmail( String strFieldConfirmationEmail )
    {
        this._strFieldConfirmationEmail = strFieldConfirmationEmail;
    }

    /**
     * Get field confirmation email help
     * 
     * @return The confirmation email help
     */
    public String getFieldConfirmationEmailHelp( )
    {
        return _strFieldConfirmationEmailHelp;
    }

    /**
     * Set the field email confirmation help
     * 
     * @param fieldConfirmationEmailHelp
     */
    public void setFieldConfirmationEmailHelp( String fieldConfirmationEmailHelp )
    {
        this._strFieldConfirmationEmailHelp = fieldConfirmationEmailHelp;
    }

    /**
     * Get the URL to redirect the user to after creation of an appointment
     * 
     * @return The URL to redirect the user to after creation of an appointment
     */
    public String getUrlRedirectAfterCreation( )
    {
        return _strUrlRedirectAfterCreation;
    }

    /**
     * Set the URL to redirect the user to after creation of an appointment
     * 
     * @param strUrlRedirectAfterCreation
     *            The URL to redirect the user to after creation of an appointment
     */
    public void setUrlRedirectAfterCreation( String strUrlRedirectAfterCreation )
    {
        this._strUrlRedirectAfterCreation = strUrlRedirectAfterCreation;
    }

    /**
     * Get the text to display to the user after the creation of an appointment and before he is redirected to a given URL
     * 
     * @return The text to display to the user after the creation of an appointment
     */
    public String getTextAppointmentCreated( )
    {
        return _strTextAppointmentCreated;
    }

    /**
     * Set the text to display to the user after the creation of an appointment
     * 
     * @param strTextAppointmentCreated
     *            The text to display to the user after the creation of an appointment
     */
    public void setTextAppointmentCreated( String strTextAppointmentCreated )
    {
        this._strTextAppointmentCreated = strTextAppointmentCreated;
    }

    /**
     * Get the text to display to the user after the canceling of an appointment
     * 
     * @return The text to display to the user after the canceling of an appointment
     */
    public String getTextAppointmentCanceled( )
    {
        return _strTextAppointmentCanceled;
    }

    /**
     * Set the text to display to the user after the canceling of an appointment
     * 
     * @param strTextAppointmentCanceled
     *            The text to display to the user after the canceling of an appointment
     */
    public void setTextAppointmentCanceled( String strTextAppointmentCanceled )
    {
        this._strTextAppointmentCanceled = strTextAppointmentCanceled;
    }

    /**
     * Get the label of the button after the creation of an appointment
     * 
     * @return The label of the button after the creation of an appointment
     */
    public String getLabelButtonRedirection( )
    {
        return _strLabelButtonRedirection;
    }

    /**
     * Set the label of the button after the creation of an appointment
     * 
     * @param strLabelButtonRedirection
     *            The label of the button after the creation of an appointment
     */
    public void setLabelButtonRedirection( String strLabelButtonRedirection )
    {
        this._strLabelButtonRedirection = strLabelButtonRedirection;
    }

    /**
     * Get the message to display when there is no available slot for the associated form
     * 
     * @return the message to display when there is no available slot
     */
    public String getNoAvailableSlot( )
    {
        return _strNoAvailableSlot;
    }

    /**
     * Set the message to display when there is no available slot for the associated form
     * 
     * @param strNoAvailableSlot
     *            The message to display when there is no available slot
     */
    public void setNoAvailableSlot( String strNoAvailableSlot )
    {
        this._strNoAvailableSlot = strNoAvailableSlot;
    }

    /**
     * Get the calendar description
     * 
     * @return The calendar description
     */
    public String getCalendarDescription( )
    {
        return _strCalendarDescription;
    }

    /**
     * Set the calendar description
     * 
     * @param strCalendarDescription
     *            The calendar description
     */
    public void setCalendarDescription( String strCalendarDescription )
    {
        this._strCalendarDescription = strCalendarDescription;
    }

    /**
     * Get the label that indicates that a slot is open for reservation
     * 
     * @return The label that indicates that a slot is open for reservation
     */
    public String getCalendarReserveLabel( )
    {
        return _strCalendarReserveLabel;
    }

    /**
     * Set the label that indicates that a slot is open for reservation
     * 
     * @param strCalendarReserveLabel
     *            The label that indicates that a slot is open for reservation
     */
    public void setCalendarReserveLabel( String strCalendarReserveLabel )
    {
        this._strCalendarReserveLabel = strCalendarReserveLabel;
    }

    /**
     * Get the label that indicates that a slot is full
     * 
     * @return The label that indicates that a slot is full
     */
    public String getCalendarFullLabel( )
    {
        return _strCalendarFullLabel;
    }

    /**
     * Set the label that indicates that a slot is full
     * 
     * @param strCalendarFullLabel
     *            The label that indicates that a slot is full
     */
    public void setCalendarFullLabel( String strCalendarFullLabel )
    {
        this._strCalendarFullLabel = strCalendarFullLabel;
    }

    /**
     * Return the id of the associated form
     * 
     * @return The id of the associated form
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the id of the associated form
     * 
     * @param nIdForm
     *            The id of the associated form
     */
    public void setIdForm( int nIdForm )
    {
        _nIdForm = nIdForm;
    }
}
