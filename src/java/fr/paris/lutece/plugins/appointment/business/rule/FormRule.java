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
package fr.paris.lutece.plugins.appointment.business.rule;

import java.io.Serializable;

/**
 * Business Class of the rules of the form
 * 
 * @author Laurent Payen
 *
 */
public final class FormRule implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -737984459576501946L;

    /**
     * Id of the form rule.
     */
    private int _nIdFormRule;

    /**
     * Indicate whether the captcha is enabled or not
     */
    private boolean _bIsCaptchaEnabled;

    /**
     * Indicate whether the email is mandatory or not
     */
    private boolean _bIsMandatoryEmailEnabled;

    /**
     * True if the authentication is required
     */
    private boolean _bIsActiveAuthentication;

    /**
     * Nb Days before the user can take another appointment
     */
    private int _nNbDaysBeforeNewAppointment;

    /**
     * Minimum time from now before the user can take an appointment
     */
    private int _nMinTimeBeforeAppointment;

    /**
     * Maximum number of appointments for a same user on a given period
     */
    private int _nNbMaxAppointmentsPerUser;

    /**
     * The period for the maximum number of appointments per user
     */
    private int _nNbDaysForMaxAppointmentsPerUser;
    /**
     * Authorize overbooking
     */
    private boolean _bBoOverbooking;

    /**
     * Form id (foreign key)
     */
    private int _nIdForm;

    /**
     * Get the id of the form rule
     * 
     * @return the id of the form rule
     */
    public int getIdFormRule( )
    {
        return _nIdFormRule;
    }

    /**
     * Set the id of the form rule
     * 
     * @param nIdFormRule
     *            the id to set
     */
    public void setIdFormRule( int nIdFormRule )
    {
        this._nIdFormRule = nIdFormRule;
    }

    /**
     * Indicate if the captcha is enabled or not
     * 
     * @return true if the captcha is enabled
     */
    public boolean getIsCaptchaEnabled( )
    {
        return _bIsCaptchaEnabled;
    }

    /**
     * Set the boolean captcha value
     * 
     * @param bIsCaptchaEnabled
     *            the boolean captcha value to set
     */
    public void setIsCaptchaEnabled( boolean bIsCaptchaEnabled )
    {
        this._bIsCaptchaEnabled = bIsCaptchaEnabled;
    }

    /**
     * Indicate whether the email is mandatory or not
     * 
     * @return true if the email is mandatory
     */
    public boolean getIsMandatoryEmailEnabled( )
    {
        return _bIsMandatoryEmailEnabled;
    }

    /**
     * Set the boolean value for the mandatory email
     * 
     * @param bIsMandatoryEmailEnabled
     *            the boolean value for the mandatory email
     */
    public void setIsMandatoryEmailEnabled( boolean bIsMandatoryEmailEnabled )
    {
        this._bIsMandatoryEmailEnabled = bIsMandatoryEmailEnabled;
    }

    /**
     * Indicate whether the authentication is required or not
     * 
     * @return true if the authentication is required
     */
    public boolean getIsActiveAuthentication( )
    {
        return _bIsActiveAuthentication;
    }

    /**
     * Set the boolean value for the authentication
     * 
     * @param bIsActiveAuthentication
     *            the boolean value for the authentication
     */
    public void setIsActiveAuthentication( boolean bIsActiveAuthentication )
    {
        this._bIsActiveAuthentication = bIsActiveAuthentication;
    }

    /**
     * Get the number of days the user has to wait before he can take another appointment
     * 
     * @return the number of days
     */
    public int getNbDaysBeforeNewAppointment( )
    {
        return _nNbDaysBeforeNewAppointment;
    }

    /**
     * Set the number of days the user have to wait before he can take another appointment
     * 
     * @param _nNbDaysBeforeNewAppointment
     *            the number of days
     */
    public void setNbDaysBeforeNewAppointment( int nNbDaysBeforeNewAppointment )
    {
        this._nNbDaysBeforeNewAppointment = nNbDaysBeforeNewAppointment;
    }

    /**
     * Get the minimal time from now before the user can take an appointment
     * 
     * @return The minimal time in hours
     */
    public int getMinTimeBeforeAppointment( )
    {
        return _nMinTimeBeforeAppointment;
    }

    /**
     * Set the minimal time from now before the user can take an appointment
     * 
     * @param nMinTimeBeforeAppointment
     *            the minimal time in hours
     */
    public void setMinTimeBeforeAppointment( int nMinTimeBeforeAppointment )
    {
        this._nMinTimeBeforeAppointment = nMinTimeBeforeAppointment;
    }

    /**
     * Get the maximum number of appointments per user
     * 
     * @return the maximum number of appointments per user on a given period
     */
    public int getNbMaxAppointmentsPerUser( )
    {
        return _nNbMaxAppointmentsPerUser;
    }

    /**
     * Set the maximum number of appointments authorized
     * 
     * @param _nNbMaxAppointmentsPerUser
     *            the maximum number of appointments authorized
     */
    public void setNbMaxAppointmentsPerUser( int nNbMaxAppointmentsPerUser )
    {
        this._nNbMaxAppointmentsPerUser = nNbMaxAppointmentsPerUser;
    }

    /**
     * Get the number of days for the period of the maximum number of appointments authorized per user
     * 
     * @return the number of days of the period
     */
    public int getNbDaysForMaxAppointmentsPerUser( )
    {
        return _nNbDaysForMaxAppointmentsPerUser;
    }

    /**
     * Set the number of days of the period for the maximum number of appointments authorized
     * 
     * @param _nNbDaysForMaxAppointmentsPerUser
     *            the number of days to set
     */
    public void setNbDaysForMaxAppointmentsPerUser( int nNbDaysForMaxAppointmentsPerUser )
    {
        this._nNbDaysForMaxAppointmentsPerUser = nNbDaysForMaxAppointmentsPerUser;
    }

    /**
     * Returns the BoOverbooking
     * 
     * @return The BoOverbooking
     */
    public boolean getBoOverbooking( )
    {
        return _bBoOverbooking;
    }

    /**
     * Sets the BoOverbooking
     * 
     * @param bBoOverbooking
     *            The BoOverbooking
     */
    public void setBoOverbooking( boolean bBoOverbooking )
    {
        _bBoOverbooking = bBoOverbooking;
    }

    /**
     * Get the form id the formRule belongs to
     * 
     * @return the form id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the form id the formRule belongs to
     * 
     * @param nIdForm
     *            the form id to set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

}
