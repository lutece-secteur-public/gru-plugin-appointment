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
package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.sql.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * This is the business class for the object AppointmentForm
 */
public class AppointmentForm implements RBACResource, Cloneable, Serializable
{
    /**
     * Name of the resource type of Appointment Forms
     */
    public static final String RESOURCE_TYPE = "APPOINTMENT_FORM";

    /**
     * Constant for separator between hours and minutes.
     */
    public static final String CONSTANT_H = "h";

    /**
     * Regular expression used to control time format
     */
    public static final String CONSTANT_TIME_REGEX = "^[0-2][0-9]" + CONSTANT_H + "[0-5][0-9]$";
    private static final long serialVersionUID = 307685220867535209L;

    // Variables declarations 
    private int _nIdForm;
    @NotBlank( message = "#i18n{appointment.validation.appointmentform.Title.notEmpty}" )
    @Size( max = 255, message = "#i18n{appointment.validation.appointmentform.Title.size}" )
    private String _strTitle;
    @NotBlank( message = "#i18n{appointment.validation.appointmentform.Description.notEmpty}" )
    private String _strDescription;
    @NotNull( message = "#i18n{portal.validation.message.notEmpty}" )
    @Pattern( regexp = CONSTANT_TIME_REGEX, message = "#i18n{appointment.modify_appointmentForm.patternTimeStart}" )
    private String _strTimeStart;
    @Pattern( regexp = CONSTANT_TIME_REGEX, message = "#i18n{appointment.modify_appointmentForm.patternTimeEnd}" )
    @NotNull( message = "#i18n{portal.validation.message.notEmpty}" )
    private String _strTimeEnd;
    @NotNull( message = "#i18n{portal.validation.message.notEmpty}" )
    @Min( value = 1, message = "#i18n{portal.validation.message.notEmpty}" )
    private int _nDurationAppointments;
    private boolean _bIsOpenMonday;
    private boolean _bIsOpenTuesday;
    private boolean _bIsOpenWednesday;
    private boolean _bIsOpenThursday;
    private boolean _bIsOpenFriday;
    private boolean _bIsOpenSaturday;
    private boolean _bIsOpenSunday;
    private Date _dateDateStartValidity;
    private Date _dateDateEndValidity;
    private boolean _bIsActive;
    private boolean _bDisplayTitleFo;
    @Min( value = 1, message = "#i18n{portal.validation.message.notEmpty}" )
    private int _nNbWeeksToDisplay;
    @Min( value = 1, message = "#i18n{portal.validation.message.notEmpty}" )
    private int _nPeoplePerAppointment;
    private int _nIdWorkflow;
    private int _nOpeningHour;
    private int _nOpeningMinutes;
    private int _nClosingHour;
    private int _nClosingMinutes;
    private int _nMinDaysBeforeAppointment;
    private boolean _bEnableCaptcha;
    private boolean _bAllowUsersToCancelAppointments;
    private boolean _bOpeningHourInitialized;
    private boolean _bOpeningMinutesInitialized;
    private boolean _bClosingHourInitialized;
    private boolean _bClosingMinutesInitialized;
    @Min( value = 1, message = "#i18n{portal.validation.message.notEmpty}" )
    private int _nCalendarTemplateId;
    private int _nMaxAppointmentMail;
    private int _nNbWeeksLimits;
    private String _strReference;
    private boolean _bIsFormStep;
    private boolean _bEnableConfirmEmail;
    
    /**
     * Returns the IdForm
     * @return The IdForm
     */
    public int getIdForm(  )
    {
        return _nIdForm;
    }

    /**
     * Sets the IdForm
     * @param nIdForm The IdForm
     */
    public void setIdForm( int nIdForm )
    {
        _nIdForm = nIdForm;
    }

    /**
     * Returns the Title
     * @return The Title
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * Sets the Title
     * @param strTitle The Title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Get the description of the appointment form
     * @return The description of the appointment form
     */
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * Set the description of the appointment form
     * @param strDescription The description of the appointment form
     */
    public void setDescription( String strDescription )
    {
        this._strDescription = strDescription;
    }

    /**
     * Returns the _strReference
     * @return The strRef
     */
    public String getReference(  )
    {
        return _strReference;
    }

    /**
     * Sets the Reference
     * @param strRef The strRef
     */
    public void setReference( String strRef )
    {
    	_strReference =  strRef;
    } 
    /**
     * Returns the _strReference
     * @return The strRef
     */
    public boolean getIsFormStep(  )
    {
        return _bIsFormStep;
    }

    /**
     * Sets the Reference
     * @param strRef The strRef
     */
    public void setIsFormStep( boolean nStep )
    {
    	_bIsFormStep =  nStep;
    } /**
     * Returns the TimeStart
     * @return The TimeStart
     */
    public String getTimeStart(  )
    {
        return _strTimeStart;
    }

    /**
     * Sets the TimeStart
     * @param nTimeStart The TimeStart
     */
    public void setTimeStart( String nTimeStart )
    {
        _strTimeStart = nTimeStart;
        _bOpeningHourInitialized = false;
        _bOpeningMinutesInitialized = false;
    }

    /**
     * Returns the TimeEnd
     * @return The TimeEnd
     */
    public String getTimeEnd(  )
    {
        return _strTimeEnd;
    }

    /**
     * Sets the TimeEnd
     * @param nTimeEnd The TimeEnd
     */
    public void setTimeEnd( String nTimeEnd )
    {
        _strTimeEnd = nTimeEnd;
        _bClosingHourInitialized = false;
        _bClosingMinutesInitialized = false;
    }

    /**
     * Returns the DurationAppointments
     * @return The DurationAppointments
     */
    public int getDurationAppointments(  )
    {
        return _nDurationAppointments;
    }

    /**
     * Sets the DurationAppointments
     * @param nDurationAppointments The DurationAppointments
     */
    public void setDurationAppointments( int nDurationAppointments )
    {
        _nDurationAppointments = nDurationAppointments;
    }

    /**
     * Returns the IsOpenMonday
     * @return The IsOpenMonday
     */
    public boolean getIsOpenMonday(  )
    {
        return _bIsOpenMonday;
    }

    /**
     * Sets the IsOpenMonday
     * @param bIsOpenMonday The IsOpenMonday
     */
    public void setIsOpenMonday( boolean bIsOpenMonday )
    {
        _bIsOpenMonday = bIsOpenMonday;
    }

    /**
     * Returns the IsOpenTuesday
     * @return The IsOpenTuesday
     */
    public boolean getIsOpenTuesday(  )
    {
        return _bIsOpenTuesday;
    }

    /**
     * Sets the IsOpenTuesday
     * @param bIsOpenTuesday The IsOpenTuesday
     */
    public void setIsOpenTuesday( boolean bIsOpenTuesday )
    {
        _bIsOpenTuesday = bIsOpenTuesday;
    }

    /**
     * Returns the IsOpenWednesday
     * @return The IsOpenWednesday
     */
    public boolean getIsOpenWednesday(  )
    {
        return _bIsOpenWednesday;
    }

    /**
     * Sets the IsOpenWednesday
     * @param bIsOpenWednesday The IsOpenWednesday
     */
    public void setIsOpenWednesday( boolean bIsOpenWednesday )
    {
        _bIsOpenWednesday = bIsOpenWednesday;
    }

    /**
     * Returns the IsOpenThursday
     * @return The IsOpenThursday
     */
    public boolean getIsOpenThursday(  )
    {
        return _bIsOpenThursday;
    }

    /**
     * Sets the IsOpenThursday
     * @param bIsOpenThursday The IsOpenThursday
     */
    public void setIsOpenThursday( boolean bIsOpenThursday )
    {
        _bIsOpenThursday = bIsOpenThursday;
    }

    /**
     * Returns the IsOpenFriday
     * @return The IsOpenFriday
     */
    public boolean getIsOpenFriday(  )
    {
        return _bIsOpenFriday;
    }

    /**
     * Sets the IsOpenFriday
     * @param bIsOpenFriday The IsOpenFriday
     */
    public void setIsOpenFriday( boolean bIsOpenFriday )
    {
        _bIsOpenFriday = bIsOpenFriday;
    }

    /**
     * Returns the IsOpenSaturday
     * @return The IsOpenSaturday
     */
    public boolean getIsOpenSaturday(  )
    {
        return _bIsOpenSaturday;
    }

    /**
     * Sets the IsOpenSaturday
     * @param bIsOpenSaturday The IsOpenSaturday
     */
    public void setIsOpenSaturday( boolean bIsOpenSaturday )
    {
        _bIsOpenSaturday = bIsOpenSaturday;
    }

    /**
     * Returns the IsOpenSunday
     * @return The IsOpenSunday
     */
    public boolean getIsOpenSunday(  )
    {
        return _bIsOpenSunday;
    }

    /**
     * Sets the IsOpenSunday
     * @param bIsOpenSunday The IsOpenSunday
     */
    public void setIsOpenSunday( boolean bIsOpenSunday )
    {
        _bIsOpenSunday = bIsOpenSunday;
    }

    /**
     * Returns the DateStartValidity
     * @return The DateStartValidity
     */
    public Date getDateStartValidity(  )
    {
        return _dateDateStartValidity;
    }

    /**
     * Sets the DateStartValidity
     * @param dateDateStartValidity The DateStartValidity
     */
    public void setDateStartValidity( Date dateDateStartValidity )
    {
        _dateDateStartValidity = dateDateStartValidity;
    }

    /**
     * Returns the DateEndValidity
     * @return The DateEndValidity
     */
    public Date getDateEndValidity(  )
    {
        return _dateDateEndValidity;
    }

    /**
     * Sets the DateEndValidity
     * @param dateDateEndValidity The DateEndValidity
     */
    public void setDateEndValidity( Date dateDateEndValidity )
    {
        _dateDateEndValidity = dateDateEndValidity;
    }

    /**
     * Returns the IsActive
     * @return The IsActive
     */
    public boolean getIsActive(  )
    {
        return _bIsActive;
    }

    /**
     * Sets the IsActive
     * @param bIsActive The IsActive
     */
    public void setIsActive( boolean bIsActive )
    {
        _bIsActive = bIsActive;
    }

    /**
     * Returns the DisplayTitleFo
     * @return The DisplayTitleFo
     */
    public boolean getDisplayTitleFo(  )
    {
        return _bDisplayTitleFo;
    }

    /**
     * Sets the DispolayTitleFo
     * @param bDisplayTitleFo The DisplayTitleFo
     */
    public void setDisplayTitleFo( boolean bDisplayTitleFo )
    {
        _bDisplayTitleFo = bDisplayTitleFo;
    }

    /**
     * Returns the NbWeeksToDisplay
     * @return The NbWeeksToDisplay
     */
    public int getNbWeeksToDisplay(  )
    {
        return _nNbWeeksToDisplay;
    }

    /**
     * Sets the NbWeeksToDisplay
     * @param nNbWeeksToDisplay The NbWeeksToDisplay
     */
    public void setNbWeeksToDisplay( int nNbWeeksToDisplay )
    {
        _nNbWeeksToDisplay = nNbWeeksToDisplay;
    }

    /**
     * Returns the number of person per appointment
     * @return The number of person per appointment
     */
    public int getPeoplePerAppointment(  )
    {
        return _nPeoplePerAppointment;
    }

    /**
     * Sets the number of person per appointment
     * @param nPeoplePerAppointment The number of person per appointment
     */
    public void setPeoplePerAppointment( int nPeoplePerAppointment )
    {
        _nPeoplePerAppointment = nPeoplePerAppointment;
    }

    /**
     * Get the id of the workflow associated with this appointment form
     * @return The id of the workflow
     */
    public int getIdWorkflow(  )
    {
        return _nIdWorkflow;
    }

    /**
     * Set the id of the workflow associated with this appointment form
     * @param nIdWorkflow The id of the workflow
     */
    public void setIdWorkflow( int nIdWorkflow )
    {
        _nIdWorkflow = nIdWorkflow;
    }

    /**
     * Get the opening hour
     * @return the opening hour
     */
    public int getOpeningHour(  )
    {
        if ( !_bOpeningHourInitialized )
        {
            _nOpeningHour = Integer.parseInt( _strTimeStart.split( CONSTANT_H )[0] );
            _bOpeningHourInitialized = true;
        }

        return _nOpeningHour;
    }

    /**
     * Set the opening hour
     * @param nOpeningHour The opening hour
     */
    public void setOpeningHour( int nOpeningHour )
    {
        this._nOpeningHour = nOpeningHour;
        _bOpeningHourInitialized = true;
    }

    /**
     * Get the opening minutes
     * @return The opening minutes
     */
    public int getOpeningMinutes(  )
    {
        if ( !_bOpeningMinutesInitialized )
        {
            _nOpeningMinutes = Integer.parseInt( _strTimeStart.split( CONSTANT_H )[1] );
            _bOpeningMinutesInitialized = true;
        }

        return _nOpeningMinutes;
    }

    /**
     * Set the opening minutes
     * @param nOpeningMinutes The opening minutes
     */
    public void setOpeningMinutes( int nOpeningMinutes )
    {
        this._nOpeningMinutes = nOpeningMinutes;
        _bOpeningMinutesInitialized = true;
    }

    /**
     * Get the closing hour
     * @return the closing hour
     */
    public int getClosingHour(  )
    {
        if ( !_bClosingHourInitialized )
        {
            _nClosingHour = Integer.parseInt( _strTimeEnd.split( CONSTANT_H )[0] );
            _bClosingHourInitialized = true;
        }

        return _nClosingHour;
    }

    /**
     * Set the closing hour
     * @param nClosingHour The closing hour
     */
    public void setClosingHour( int nClosingHour )
    {
        this._nClosingHour = nClosingHour;
        _bClosingHourInitialized = true;
    }

    /**
     * Get the closing minutes
     * @return the closing minutes
     */
    public int getClosingMinutes(  )
    {
        if ( !_bClosingMinutesInitialized )
        {
            _nClosingMinutes = Integer.parseInt( _strTimeEnd.split( CONSTANT_H )[1] );
            _bClosingMinutesInitialized = true;
        }

        return _nClosingMinutes;
    }

    /**
     * Set the closing minutes
     * @param nClosingMinutes the closing minutes
     */
    public void setClosingMinutes( int nClosingMinutes )
    {
        this._nClosingMinutes = nClosingMinutes;
        _bClosingMinutesInitialized = true;
    }

    /**
     * Get the minimum number of days between the current date and the date of
     * appointment make by users
     * @return The minimum number of days between the current date and the date
     *         of appointment make by users
     */
    public int getMinDaysBeforeAppointment(  )
    {
        return _nMinDaysBeforeAppointment;
    }

    /**
     * Set the minimum number of days between the current date and the date of
     * appointment make by users
     * @param nMinDayBeforeAppointment The minimum number of days between the
     *            current date and the date of appointment make by users
     */
    public void setMinDaysBeforeAppointment( int nMinDayBeforeAppointment )
    {
        this._nMinDaysBeforeAppointment = nMinDayBeforeAppointment;
    }

    /**
     * Check if the captcha is enabled for this appointment form
     * @return True if the captcha is enabled, false otherwise
     */
    public boolean getEnableCaptcha(  )
    {
        return _bEnableCaptcha;
    }

    /**
     * Enable or disable the captcha for this appointment form
     * @param bEnableCaptcha True to enable the captcha, false to disable it
     */
    public void setEnableCaptcha( boolean bEnableCaptcha )
    {
        this._bEnableCaptcha = bEnableCaptcha;
    }

    /**
     * Check if a FO user can cancel appointments of this form
     * @return True if a FO user can cancel appointments of this form, false
     *         otherwise
     */
    public boolean getAllowUsersToCancelAppointments(  )
    {
        return _bAllowUsersToCancelAppointments;
    }

    /**
     * Set whether FO user can cancel appointments of this form
     * @param bAllowUsersToCancelAppointments True if a FO user can cancel
     *            appointments of this form, false otherwise
     */
    public void setAllowUsersToCancelAppointments( boolean bAllowUsersToCancelAppointments )
    {
        this._bAllowUsersToCancelAppointments = bAllowUsersToCancelAppointments;
    }

    /**
     * Get the id of the calendar template of this appointment form
     * @return The id of the calendar template of this appointment form
     */
    public int getCalendarTemplateId(  )
    {
        return _nCalendarTemplateId;
    }

    /**
     * Set the id of the calendar template of this appointment form
     * @param nCalendarTemplateId The id of the calendar template of this
     *            appointment form
     */
    public void setCalendarTemplateId( int nCalendarTemplateId )
    {
        _nCalendarTemplateId = nCalendarTemplateId;
    }

    /**
     * Get the max for weeks of this appointment form to validate
     * @param nMaxAppointment The nb of the max appointpents for weeks
     *       of this  appointment form
     */
    public int getMaxAppointments()
    {
        return _nMaxAppointmentMail;
    }    
    /**
     * Set the max for weeks of this appointment form to validate
     *  @param nMaxAppointment The nb of the max appointpents for weeks
     *       of this  appointment form
     */
    public void setMaxAppointmentMail( int nMaxAppointment)
    {
        _nMaxAppointmentMail = nMaxAppointment;
    }

    /**
     * Get the limits for weeks of this appointment form to validate
     * @param _nNbWeeksLimits The nb of the max appointpents for weeks
     *       of this  appointment form
     */
    public int getWeeksLimits()
    {
        return _nNbWeeksLimits;
    }    
    /**
     * Set the limits for weeks of this appointment form to validate
     *  @param _nNbWeeksLimits The nb of the max appointpents for weeks
     *       of this  appointment form
     */
    public void setNbWeeksLimits( int nWeekLimits)
    {
    	_nNbWeeksLimits = nWeekLimits;
    }
    /**
     * Check if a day of the week is opened or not
     * @param nDayOfWeek The number of the day of the week : 1 for Monday, 2 for
     *            Tuesday, ...
     * @return True if the requested day of the week is open, false otherwise
     */
    public boolean isDayOfWeekOpened( int nDayOfWeek )
    {
        boolean[] bArrayDaysOpened = 
            {
                _bIsOpenMonday, _bIsOpenTuesday, _bIsOpenWednesday, _bIsOpenThursday, _bIsOpenFriday, _bIsOpenSaturday,
                _bIsOpenSunday,
            };

        return ( ( nDayOfWeek > 0 ) && ( nDayOfWeek < bArrayDaysOpened.length ) ) ? bArrayDaysOpened[nDayOfWeek - 1]
                                                                                  : false;
    }
    /**
     * Get enable confirm email
     * @return boolean  	enable confirm email
     */
    public boolean getEnableConfirmEmail() {
		return _bEnableConfirmEmail;
	}
    /**
     * Set enabme confirm email
     * @param bEnableConfirmEmail
     */
	public void setEnableConfirmEmail(boolean bEnableConfirmEmail) {
		this._bEnableConfirmEmail = bEnableConfirmEmail;
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public String getResourceTypeCode(  )
    {
        return RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceId(  )
    {
        return Integer.toString( getIdForm(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone(  )
    {
        try
        {
            return super.clone(  );
        }
        catch ( CloneNotSupportedException e )
        {
            AppLogService.error( e.getMessage(  ), e );

            return null;
        }
    }
}
