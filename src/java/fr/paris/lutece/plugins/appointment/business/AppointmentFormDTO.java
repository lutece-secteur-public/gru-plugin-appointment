/*
 * Copyright (c) 2002-2015, Mairie de Paris
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

import java.io.Serializable;
import java.sql.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * This is the business class for the object AppointmentForm
 */
public class AppointmentFormDTO implements RBACResource, Cloneable, Serializable {
	/**
	 * Name of the resource type of Appointment Forms
	 */
	public static final String RESOURCE_TYPE = "APPOINTMENT_FORM";

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 307685220867535209L;

	// Variables declarations
	private int _nIdForm;
	@NotBlank(message = "#i18n{appointment.validation.appointmentform.Title.notEmpty}")
	@Size(max = 255, message = "#i18n{appointment.validation.appointmentform.Title.size}")
	private String _strTitle;
	@NotBlank(message = "#i18n{appointment.validation.appointmentform.Description.notEmpty}")
	private String _strDescription;
	@NotNull(message = "#i18n{portal.validation.message.notEmpty}")
	private String _strTimeStart;
	@NotNull(message = "#i18n{portal.validation.message.notEmpty}")
	private String _strTimeEnd;
	@NotNull(message = "#i18n{portal.validation.message.notEmpty}")
	@Min(value = 1, message = "#i18n{portal.validation.message.notEmpty}")
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
	private Date _dateOfModification;
	private boolean _bIsActive;
	private boolean _bDisplayTitleFo;
	private int _nNbWeeksToDisplay;
	@Min(value = 1, message = "#i18n{portal.validation.message.notEmpty}")
	private int _nMaxCapacityPerSlot;
	@Min(value = 1, message = "#i18n{portal.validation.message.notEmpty}")
	private int _nMaxPeoplePerAppointment = 0;
	private int _nIdWorkflow;
	private boolean _bEnableCaptcha;
	@Min(value = 1, message = "#i18n{portal.validation.message.notEmpty}")
	private int _nCalendarTemplateId;
	private String _strReference;
	private boolean _bEnableMandatoryEmail;
	private ImageResource _imageResource;
	private String _strCategory;
	private int _nIdReservationRule;

	/**
	 * Returns the IdForm
	 * 
	 * @return The IdForm
	 */
	public int getIdForm() {
		return _nIdForm;
	}

	/**
	 * Sets the IdForm
	 * 
	 * @param nIdForm
	 *            The IdForm
	 */
	public void setIdForm(int nIdForm) {
		_nIdForm = nIdForm;
	}

	/**
	 * Returns the Title
	 * 
	 * @return The Title
	 */
	public String getTitle() {
		return _strTitle;
	}

	/**
	 * Sets the Title
	 * 
	 * @param strTitle
	 *            The Title
	 */
	public void setTitle(String strTitle) {
		_strTitle = strTitle;
	}

	public Date getDateOfModification() {
		return _dateOfModification;
	}

	public void setDateOfModification(Date dateOfModification) {
		this._dateOfModification = dateOfModification;
	}

	/**
	 * Get the description of the appointment form
	 * 
	 * @return The description of the appointment form
	 */
	public String getDescription() {
		return _strDescription;
	}

	/**
	 * Set the description of the appointment form
	 * 
	 * @param strDescription
	 *            The description of the appointment form
	 */
	public void setDescription(String strDescription) {
		this._strDescription = strDescription;
	}

	/**
	 * Returns the _strReference
	 * 
	 * @return The strRef
	 */
	public String getReference() {
		return _strReference;
	}

	/**
	 * Sets the Reference
	 * 
	 * @param strRef
	 *            The strRef
	 */
	public void setReference(String strRef) {
		_strReference = strRef;
	}

	/**
	 * Returns the TimeStart
	 * 
	 * @return The TimeStart
	 */
	public String getTimeStart() {
		return _strTimeStart;
	}

	/**
	 * Sets the TimeStart
	 * 
	 * @param timeStart
	 *            The TimeStart
	 */
	public void setTimeStart(String timeStart) {
		_strTimeStart = timeStart;
	}

	/**
	 * Returns the TimeEnd
	 * 
	 * @return The TimeEnd
	 */
	public String getTimeEnd() {
		return _strTimeEnd;
	}

	/**
	 * Sets the TimeEnd
	 * 
	 * @param nTimeEnd
	 *            The TimeEnd
	 */
	public void setTimeEnd(String timeEnd) {
		_strTimeEnd = timeEnd;
	}

	/**
	 * Returns the DurationAppointments
	 * 
	 * @return The DurationAppointments
	 */
	public int getDurationAppointments() {
		return _nDurationAppointments;
	}

	/**
	 * Sets the DurationAppointments
	 * 
	 * @param nDurationAppointments
	 *            The DurationAppointments
	 */
	public void setDurationAppointments(int nDurationAppointments) {
		_nDurationAppointments = nDurationAppointments;
	}

	/**
	 * Returns the IsOpenMonday
	 * 
	 * @return The IsOpenMonday
	 */
	public boolean getIsOpenMonday() {
		return _bIsOpenMonday;
	}

	/**
	 * Sets the IsOpenMonday
	 * 
	 * @param bIsOpenMonday
	 *            The IsOpenMonday
	 */
	public void setIsOpenMonday(boolean bIsOpenMonday) {
		_bIsOpenMonday = bIsOpenMonday;
	}

	/**
	 * Returns the IsOpenTuesday
	 * 
	 * @return The IsOpenTuesday
	 */
	public boolean getIsOpenTuesday() {
		return _bIsOpenTuesday;
	}

	/**
	 * Sets the IsOpenTuesday
	 * 
	 * @param bIsOpenTuesday
	 *            The IsOpenTuesday
	 */
	public void setIsOpenTuesday(boolean bIsOpenTuesday) {
		_bIsOpenTuesday = bIsOpenTuesday;
	}

	/**
	 * Returns the IsOpenWednesday
	 * 
	 * @return The IsOpenWednesday
	 */
	public boolean getIsOpenWednesday() {
		return _bIsOpenWednesday;
	}

	/**
	 * Sets the IsOpenWednesday
	 * 
	 * @param bIsOpenWednesday
	 *            The IsOpenWednesday
	 */
	public void setIsOpenWednesday(boolean bIsOpenWednesday) {
		_bIsOpenWednesday = bIsOpenWednesday;
	}

	/**
	 * Returns the IsOpenThursday
	 * 
	 * @return The IsOpenThursday
	 */
	public boolean getIsOpenThursday() {
		return _bIsOpenThursday;
	}

	/**
	 * Sets the IsOpenThursday
	 * 
	 * @param bIsOpenThursday
	 *            The IsOpenThursday
	 */
	public void setIsOpenThursday(boolean bIsOpenThursday) {
		_bIsOpenThursday = bIsOpenThursday;
	}

	/**
	 * Returns the IsOpenFriday
	 * 
	 * @return The IsOpenFriday
	 */
	public boolean getIsOpenFriday() {
		return _bIsOpenFriday;
	}

	/**
	 * Sets the IsOpenFriday
	 * 
	 * @param bIsOpenFriday
	 *            The IsOpenFriday
	 */
	public void setIsOpenFriday(boolean bIsOpenFriday) {
		_bIsOpenFriday = bIsOpenFriday;
	}

	/**
	 * Returns the IsOpenSaturday
	 * 
	 * @return The IsOpenSaturday
	 */
	public boolean getIsOpenSaturday() {
		return _bIsOpenSaturday;
	}

	/**
	 * Sets the IsOpenSaturday
	 * 
	 * @param bIsOpenSaturday
	 *            The IsOpenSaturday
	 */
	public void setIsOpenSaturday(boolean bIsOpenSaturday) {
		_bIsOpenSaturday = bIsOpenSaturday;
	}

	/**
	 * Returns the IsOpenSunday
	 * 
	 * @return The IsOpenSunday
	 */
	public boolean getIsOpenSunday() {
		return _bIsOpenSunday;
	}

	/**
	 * Sets the IsOpenSunday
	 * 
	 * @param bIsOpenSunday
	 *            The IsOpenSunday
	 */
	public void setIsOpenSunday(boolean bIsOpenSunday) {
		_bIsOpenSunday = bIsOpenSunday;
	}

	/**
	 * Returns the DateStartValidity
	 * 
	 * @return The DateStartValidity
	 */
	public Date getDateStartValidity() {
		return _dateDateStartValidity;
	}

	/**
	 * Sets the DateStartValidity
	 * 
	 * @param dateDateStartValidity
	 *            The DateStartValidity
	 */
	public void setDateStartValidity(Date dateDateStartValidity) {
		_dateDateStartValidity = dateDateStartValidity;
	}

	/**
	 * Returns the DateEndValidity
	 * 
	 * @return The DateEndValidity
	 */
	public Date getDateEndValidity() {
		return _dateDateEndValidity;
	}

	/**
	 * Sets the DateEndValidity
	 * 
	 * @param dateDateEndValidity
	 *            The DateEndValidity
	 */
	public void setDateEndValidity(Date dateDateEndValidity) {
		_dateDateEndValidity = dateDateEndValidity;
	}

	/**
	 * Returns the IsActive
	 * 
	 * @return The IsActive
	 */
	public boolean getIsActive() {
		return _bIsActive;
	}

	/**
	 * Sets the IsActive
	 * 
	 * @param bIsActive
	 *            The IsActive
	 */
	public void setIsActive(boolean bIsActive) {
		_bIsActive = bIsActive;
	}

	/**
	 * Returns the DisplayTitleFo
	 * 
	 * @return The DisplayTitleFo
	 */
	public boolean getDisplayTitleFo() {
		return _bDisplayTitleFo;
	}

	/**
	 * Sets the DispolayTitleFo
	 * 
	 * @param bDisplayTitleFo
	 *            The DisplayTitleFo
	 */
	public void setDisplayTitleFo(boolean bDisplayTitleFo) {
		_bDisplayTitleFo = bDisplayTitleFo;
	}

	/**
	 * Returns the NbWeeksToDisplay
	 * 
	 * @return The NbWeeksToDisplay
	 */
	public int getNbWeeksToDisplay() {
		return _nNbWeeksToDisplay;
	}

	/**
	 * Sets the NbWeeksToDisplay
	 * 
	 * @param nNbWeeksToDisplay
	 *            The NbWeeksToDisplay
	 */
	public void setNbWeeksToDisplay(int nNbWeeksToDisplay) {
		_nNbWeeksToDisplay = nNbWeeksToDisplay;
	}

	/**
	 * Get the id of the workflow associated with this appointment form
	 * 
	 * @return The id of the workflow
	 */
	public int getIdWorkflow() {
		return _nIdWorkflow;
	}

	/**
	 * Set the id of the workflow associated with this appointment form
	 * 
	 * @param nIdWorkflow
	 *            The id of the workflow
	 */
	public void setIdWorkflow(int nIdWorkflow) {
		_nIdWorkflow = nIdWorkflow;
	}

	/**
	 * Check if the captcha is enabled for this appointment form
	 * 
	 * @return True if the captcha is enabled, false otherwise
	 */
	public boolean getEnableCaptcha() {
		return _bEnableCaptcha;
	}

	/**
	 * Enable or disable the captcha for this appointment form
	 * 
	 * @param bEnableCaptcha
	 *            True to enable the captcha, false to disable it
	 */
	public void setEnableCaptcha(boolean bEnableCaptcha) {
		this._bEnableCaptcha = bEnableCaptcha;
	}

	/**
	 * Get the id of the calendar template of this appointment form
	 * 
	 * @return The id of the calendar template of this appointment form
	 */
	public int getCalendarTemplateId() {
		return _nCalendarTemplateId;
	}

	/**
	 * Set the id of the calendar template of this appointment form
	 * 
	 * @param nCalendarTemplateId
	 *            The id of the calendar template of this appointment form
	 */
	public void setCalendarTemplateId(int nCalendarTemplateId) {
		_nCalendarTemplateId = nCalendarTemplateId;
	}

	/**
	 * Get enable mandatory email
	 * 
	 * @return enable mandatory email
	 */
	public boolean getEnableMandatoryEmail() {
		return _bEnableMandatoryEmail;
	}

	/**
	 * Set enable mandatory email
	 * 
	 * @param bEnableMandatoryEmail
	 *            mandatory email
	 */
	public void setEnableMandatoryEmail(boolean bEnableMandatoryEmail) {
		this._bEnableMandatoryEmail = bEnableMandatoryEmail;
	}

	public ImageResource getIcon() {
		return _imageResource;
	}

	public void setIcon(ImageResource imgIcon) {
		this._imageResource = imgIcon;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResourceTypeCode() {
		return RESOURCE_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResourceId() {
		return Integer.toString(getIdForm());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			AppLogService.error(e.getMessage(), e);

			return null;
		}
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return _strCategory;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String strCategory) {
		_strCategory = strCategory;
	}

	public int getIdReservationRule() {
		return _nIdReservationRule;
	}

	public void setIdReservationRule(int nIdReservationRule) {
		this._nIdReservationRule = nIdReservationRule;
	}

	public int getMaxCapacityPerSlot() {
		return _nMaxCapacityPerSlot;
	}

	public void setMaxCapacityPerSlot(int nMaxCapacityPerSlot) {
		this._nMaxCapacityPerSlot = nMaxCapacityPerSlot;
	}

	public int getMaxPeoplePerAppointment() {
		return _nMaxPeoplePerAppointment;
	}

	public void setMaxPeoplePerAppointment(int nMaxPeoplePerAppointment) {
		this._nMaxPeoplePerAppointment = nMaxPeoplePerAppointment;
	}

}
