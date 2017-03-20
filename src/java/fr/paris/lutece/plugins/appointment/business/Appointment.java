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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.i18n.I18nService;

public class Appointment implements Serializable {

	private static final String PROPERTY_EMPTY_FIELD_FIRST_NAME = "appointment.validation.appointment.FirstName.notEmpty";
	private static final String PROPERTY_EMPTY_FIELD_LAST_NAME = "appointment.validation.appointment.LastName.notEmpty";
	private static final String PROPERTY_UNVAILABLE_EMAIL = "appointment.validation.appointment.Email.email";
	private static final String PROPERTY_MESSAGE_EMPTY_EMAIL = "appointment.validation.appointment.Email.notEmpty";
	private static final String PROPERTY_EMPTY_CONFIRM_EMAIL = "appointment.validation.appointment.EmailConfirmation.email";
	private static final String PROPERTY_UNVAILABLE_CONFIRM_EMAIL = "appointment.message.error.confirmEmail";
	private static final String PROPERTY_EMPTY_NB_SEATS = "appointment.validation.appointment.NbBookedSeat.notEmpty";
	private static final String PROPERTY_UNVAILABLE_NB_SEATS = "appointment.validation.appointment.NbBookedSeat.error";
	private static final String PROPERTY_MAX_APPOINTMENT_PERIODE = "appointment.message.error.MaxAppointmentPeriode";
	private static final String PROPERTY_MAX_APPOINTMENT_PERIODE_BACK = "appointment.info.appointment.emailerror";

	/**
	 * Appointment resource type
	 */
	public static final String APPOINTMENT_RESOURCE_TYPE = "appointment";

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 703930649594406505L;

	private int _nIdAppointment;
	private String _strDateOfTheAppointment;
	private int _nIdForm;
	private int _nIdUser;
	@NotBlank(message = "appointment.validation.appointment.FirstName.notEmpty")
	@Size(max = 255, message = "appointment.validation.appointment.FirstName.size")
	private String _strFirstName;

	@NotBlank(message = "appointment.validation.appointment.LastName.notEmpty")
	@Size(max = 255, message = "appointment.validation.appointment.LastName.size")
	private String _strLastName;

	@Size(max = 255, message = "appointment.validation.appointment.Email.size")
	@Email(message = "appointment.validation.appointment.Email.email")
	private String _strEmail;

	private String _strDescription;
	private boolean _bDisplayTitleFo;
	private String str_Title;

	private int _nNbBookedSeats;
	private int _nNbMaxPeoplePerAppointment;
	private Slot _slot;

	private boolean _bIsMandatoryEmail;
	private Map<Integer, List<Response>> _mapResponsesByIdEntry = new HashMap<Integer, List<Response>>();
	private List<Response> _listResponse;

	private boolean _bCaptchaEnabled;

	public String getDescription() {
		return _strDescription;
	}

	public void setDescription(String description) {
		this._strDescription = description;
	}

	public boolean getDisplayTitleFo() {
		return _bDisplayTitleFo;
	}

	public void setDisplayTitleFo(boolean bDisplayTitleFo) {
		this._bDisplayTitleFo = bDisplayTitleFo;
	}

	public String getTitle() {
		return str_Title;
	}

	public void setTitle(String title) {
		this.str_Title = title;
	}

	public boolean getCaptchaEnabled() {
		return _bCaptchaEnabled;
	}

	public void setCaptchaEnabled(boolean bCaptchaEnabled) {
		this._bCaptchaEnabled = bCaptchaEnabled;
	}

	public String getDateOfTheAppointment() {
		return _strDateOfTheAppointment;
	}

	public void setDateOfTheAppointment(String strDateOfTheAppointment) {
		this._strDateOfTheAppointment = strDateOfTheAppointment;
	}

	public boolean getIsMandatoryEmail() {
		return _bIsMandatoryEmail;
	}

	public void setIsMandatoryEmail(boolean bIsMandatoryEmail) {
		this._bIsMandatoryEmail = bIsMandatoryEmail;
	}

	public int getNbMaxPeoplePerAppointment() {
		return _nNbMaxPeoplePerAppointment;
	}

	public void setNbMaxPeoplePerAppointment(int nNbMaxPeoplePerAppointment) {
		this._nNbMaxPeoplePerAppointment = nNbMaxPeoplePerAppointment;
	}

	public List<Response> getListResponse() {
		return _listResponse;
	}

	public void setListResponse(List<Response> listResponse) {
		this._listResponse = listResponse;
	}

	public int getIdForm() {
		return _nIdForm;
	}

	public void setIdForm(int nIdForm) {
		this._nIdForm = nIdForm;
	}

	public int getIdAppointment() {
		return _nIdAppointment;
	}

	public void setIdAppointment(int nIdAppointment) {
		this._nIdAppointment = nIdAppointment;
	}

	public int getIdUser() {
		return _nIdUser;
	}

	public void setIdUser(int nIdUser) {
		this._nIdUser = nIdUser;
	}

	public String getFirstName() {
		return _strFirstName;
	}

	public void setFirstName(String strFirstName) {
		this._strFirstName = strFirstName;
	}

	public String getLastName() {
		return _strLastName;
	}

	public void setLastName(String strLastName) {
		this._strLastName = strLastName;
	}

	public String getEmail() {
		return _strEmail;
	}

	public void setEmail(String strEmail) {
		this._strEmail = strEmail;
	}

	public int getNbBookedSeats() {
		return _nNbBookedSeats;
	}

	public void setNbBookedSeats(int nNumberOfPlacesReserved) {
		this._nNbBookedSeats = nNumberOfPlacesReserved;
	}

	public Slot getSlot() {
		return _slot;
	}

	public void setSlot(Slot slot) {
		this._slot = slot;
	}

	public Map<Integer, List<Response>> getMapResponsesByIdEntry() {
		return _mapResponsesByIdEntry;
	}

	public void setMapResponsesByIdEntry(Map<Integer, List<Response>> mapResponsesByIdEntry) {
		this._mapResponsesByIdEntry = mapResponsesByIdEntry;
	}

	public static List<String> getAllErrors(HttpServletRequest request) {
		List<String> listAllErrors = new ArrayList<String>();
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_FIELD_LAST_NAME, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_FIELD_FIRST_NAME, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_UNVAILABLE_EMAIL, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_MESSAGE_EMPTY_EMAIL, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_CONFIRM_EMAIL, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_UNVAILABLE_CONFIRM_EMAIL, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_EMPTY_NB_SEATS, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_UNVAILABLE_NB_SEATS, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_MAX_APPOINTMENT_PERIODE, request.getLocale()));
		listAllErrors.add(I18nService.getLocalizedString(PROPERTY_MAX_APPOINTMENT_PERIODE_BACK, request.getLocale()));
		return listAllErrors;
	}

}
