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
package fr.paris.lutece.plugins.appointment.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * Service to manage appointment forms
 */
public class AppointmentSlotService {
	private static final String BEAN_NAME = "appointment.appointmentSlotService";
	private static volatile AppointmentSlotService _instance;

	/**
	 * Get the instance of the service
	 * 
	 * @return The instance of the service
	 */
	public static AppointmentSlotService getInstance() {
		if (_instance == null) {
			_instance = SpringContextService.getBean(BEAN_NAME);
		}

		return _instance;
	}

	/**
	 * Compute slots for a given day, and create them
	 * 
	 * @param day
	 *            The day to create slots of. The day must have been inserted in
	 *            the database.
	 * @param form
	 *            The form associated with the day.
	 */
	public synchronized void computeAndCreateSlotsForDay(AppointmentDay day, AppointmentForm form) {
		List<AppointmentSlot> listSlots;

		listSlots = AppointmentService.getService().computeDaySlots(day);

		for (AppointmentSlot slot : listSlots) {
			slot.setIdDay(day.getIdDay());
			slot.setNbPlaces(day.getPeoplePerAppointment());
			AppointmentSlotHome.create(slot);
		}

		day.setFreePlaces(day.getPeoplePerAppointment() * listSlots.size());
		AppointmentDayHome.update(day);
	}

	/**
	 * Compute slots for a given day, and create them
	 * 
	 * @param appointmentForm
	 *            The form to create slots of. The form must have been inserted
	 *            in the database.
	 */
	public synchronized void computeAndCreateSlotsForForm(AppointmentForm appointmentForm) {
		List<AppointmentDay> listAppointmentDay = AppointmentService.getService().computeDayList(appointmentForm);

		for (AppointmentDay day : listAppointmentDay) {
			int nFreePlaces = 0;

			for (AppointmentSlot slot : day.getListSlots()) {
				AppointmentSlotHome.create(slot);
				nFreePlaces += slot.getNbPlaces();
			}

			day.setFreePlaces(nFreePlaces);
			AppointmentDayHome.update(day);
		}
	}

	/**
	 * Update slots of a day or of a form after the modification of the ending
	 * time of a given slot of this day or form
	 * 
	 * @param modifiedSlot
	 *            The modified slot with its new values.
	 */
	public synchronized void updateSlotsOfDayAfterSlotModification(AppointmentSlot modifiedSlot) {
		List<AppointmentSlot> listSlotsDay;
		int nDurationAppointments;
		int nEndingTime;
		int nPlaces;
		int nEndingHour;
		int nEndingMinute;

		if (modifiedSlot.getIdDay() > 0) {
			listSlotsDay = AppointmentSlotHome.findByIdDay(modifiedSlot.getIdDay());

			AppointmentDay day = AppointmentDayHome.findByPrimaryKey(modifiedSlot.getIdDay());
			nDurationAppointments = day.getAppointmentDuration();
			nEndingHour = day.getClosingHour();
			nEndingMinute = day.getClosingMinutes();
			nEndingTime = (day.getClosingHour() * 60) + day.getClosingMinutes();
			nPlaces = day.getPeoplePerAppointment();
		} else {
			listSlotsDay = AppointmentSlotHome.findByIdFormAndDayOfWeek(modifiedSlot.getIdForm(),
					modifiedSlot.getDayOfWeek());

			AppointmentForm form = AppointmentFormHome.findByPrimaryKey(modifiedSlot.getIdForm());
			nDurationAppointments = form.getDurationAppointments();
			nEndingHour = form.getClosingHour();
			nEndingMinute = form.getClosingMinutes();
			nEndingTime = (form.getClosingHour() * 60) + form.getClosingMinutes();
			nPlaces = form.getPeoplePerAppointment();
		}

		for (AppointmentSlot slot : listSlotsDay) {
			if (((slot.getStartingHour() * 60) + slot.getStartingMinute()) > ((modifiedSlot.getStartingHour() * 60)
					+ modifiedSlot.getStartingMinute())) {
				AppointmentSlotHome.delete(slot.getIdSlot());
			}
		}

		for (int nTime = (modifiedSlot.getEndingHour() * 60)
				+ modifiedSlot.getEndingMinute(); nTime < nEndingTime; nTime = nTime + nDurationAppointments) {
			AppointmentSlot slot = new AppointmentSlot();
			slot.setIdForm(modifiedSlot.getIdForm());
			slot.setIdDay(modifiedSlot.getIdDay());
			slot.setNbPlaces(nPlaces);
			slot.setDayOfWeek(modifiedSlot.getDayOfWeek());
			slot.setIsEnabled(true);

			int nHour = nTime / 60;
			int nMinute = nTime % 60;
			slot.setStartingHour(nHour);
			slot.setStartingMinute(nMinute);
			nHour = (nTime + nDurationAppointments) / 60;
			nMinute = (nTime + nDurationAppointments) % 60;

			if (((nHour * 60) + nMinute) > nEndingTime) {
				slot.setEndingHour(nEndingHour);
				slot.setEndingMinute(nEndingMinute);
			} else {
				slot.setEndingHour(nHour);
				slot.setEndingMinute(nMinute);
			}

			AppointmentSlotHome.create(slot);
		}

		if (modifiedSlot.getIdDay() > 0) {
			AppointmentDayHome.resetDayFreePlaces(modifiedSlot.getIdDay());
		}
	}

	/**
	 * Check if a day has changed its appointment duration or has been enabled
	 * or disabled, and update slots to be compliant with new parameters of the
	 * day.
	 * 
	 * @param day
	 *            The day with new values of attributes. New values should be
	 *            saved in the database before this method is called.
	 * @param dayFromDb
	 *            The day with old values of attributes.
	 * @param form
	 *            The form associated with the day.
	 * @return True if some slots was modified, false otherwise
	 */
	public boolean checkForDayModification(AppointmentDay day, AppointmentDay dayFromDb, AppointmentForm form) {
		// If the appointment duration or the opening or closing time of the day
		// has changed, we reinitialized slots
		if ((dayFromDb.getAppointmentDuration() != day.getAppointmentDuration())
				|| (((dayFromDb.getOpeningHour() * 60) + dayFromDb.getOpeningMinutes()) != ((day.getOpeningHour() * 60)
						+ day.getOpeningMinutes()))
				|| (((dayFromDb.getClosingHour() * 60) + dayFromDb.getClosingMinutes()) != ((day.getClosingHour() * 60)
						+ day.getClosingMinutes()))) {
			AppointmentSlotHome.deleteByIdDay(day.getIdDay());
			AppointmentSlotService.getInstance().computeAndCreateSlotsForDay(day, form);

			return true;
		}

		if (dayFromDb.getIsOpen() != day.getIsOpen()) {
			if (day.getIsOpen()) {
				// If the day has been opened, we create slots
				computeAndCreateSlotsForDay(day, form);
			} else {
				// If the day has been closed, we remove slots
				AppointmentSlotHome.deleteByIdDay(day.getIdDay());
			}

			return true;
		}

		if (day.getPeoplePerAppointment() != dayFromDb.getPeoplePerAppointment()) {
			List<AppointmentSlot> listSlots = AppointmentSlotHome.findByIdDay(day.getIdDay());

			for (AppointmentSlot slot : listSlots) {
				slot.setNbPlaces(day.getPeoplePerAppointment());
				AppointmentSlotHome.update(slot);
			}

			return true;
		}

		return false;
	}

	/**
	 * Check if a form has changed its appointment duration or has enabled or
	 * disabled a day of the week, and update slots to be compliant with new
	 * parameters of the form.
	 * 
	 * @param appointmentForm
	 *            The form with new values of attributes. New values should be
	 *            saved in the database before this method is called.
	 * @param formFromDb
	 *            The form with old values of attributes.
	 * @return True if some slots was modified, false otherwise
	 */
	public boolean checkForFormModification(AppointmentForm appointmentForm, AppointmentForm formFromDb) {
		// If the duration of appointments, the starting time or the ending time
		// has changed we recreate appointments slots associated with this form
		if ((formFromDb.getDurationAppointments() != appointmentForm.getDurationAppointments())
				|| !StringUtils.equals(formFromDb.getTimeStart(), appointmentForm.getTimeStart())
				|| !StringUtils.equals(formFromDb.getTimeEnd(), appointmentForm.getTimeEnd())) {
			AppointmentSlotHome.deleteByIdForm(appointmentForm.getIdForm());

			List<AppointmentDay> listAppointmentDay = AppointmentService.getService().computeDayList(appointmentForm);

			for (AppointmentDay day : listAppointmentDay) {
				int nFreePlaces = 0;

				for (AppointmentSlot slot : day.getListSlots()) {
					AppointmentSlotHome.create(slot);
					nFreePlaces += slot.getNbPlaces();
				}

				day.setFreePlaces(nFreePlaces);
				AppointmentDayHome.update(day);
			}

			return true;
		}

		boolean[] bArrayDayOpenedfromDb = { formFromDb.getIsOpenMonday(), formFromDb.getIsOpenTuesday(),
				formFromDb.getIsOpenWednesday(), formFromDb.getIsOpenThursday(), formFromDb.getIsOpenFriday(),
				formFromDb.getIsOpenSaturday(), formFromDb.getIsOpenSunday(), };
		boolean[] bArrayDayOpened = { appointmentForm.getIsOpenMonday(), appointmentForm.getIsOpenTuesday(),
				appointmentForm.getIsOpenWednesday(), appointmentForm.getIsOpenThursday(),
				appointmentForm.getIsOpenFriday(), appointmentForm.getIsOpenSaturday(),
				appointmentForm.getIsOpenSunday(), };
		boolean bHasModifications = false;

		for (int i = 0; i < bArrayDayOpened.length; i++) {
			if (bArrayDayOpenedfromDb[i] != bArrayDayOpened[i]) {
				if (bArrayDayOpened[i]) {
					AppointmentDay day = AppointmentService.getService().getAppointmentDayFromForm(appointmentForm);
					day.setIsOpen(true);

					List<AppointmentSlot> listSlots = AppointmentService.getService().computeDaySlots(day, i + 1);

					for (AppointmentSlot slot : listSlots) {
						AppointmentSlotHome.create(slot);
					}
				} else {
					AppointmentSlotHome.deleteByIdFormAndDayOfWeek(appointmentForm.getIdForm(), i + 1);
				}

				bHasModifications = true;
			}
		}

		return bHasModifications;
	}
}
