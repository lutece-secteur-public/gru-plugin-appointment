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

import fr.paris.lutece.portal.service.cache.AbstractCacheableService;

/**
 * Get the instance of the cache service
 */
public final class AppointmentFormCacheService extends AbstractCacheableService {
	private static final String SERVICE_NAME = "appointment.appointmentFormCacheService";
	private static final String CACHE_KEY_FORM = "appointment.appointmentForm.";
	private static final String CACHE_KEY_FORM_MESSAGE = "appointment.appointmentFormMessage.";
	private static final String CACHE_KEY_APPOINTMENT_RESPONSE = "appointment.appointmentResponse";
	private static final String CACHE_KEY_LIST_APPOINTMENT_TIMES = "appointment.listAppointmentTimes.";
	private static final String CACHE_KEY_APPOINTMENT_DAY = "appointment.appointmentDay.";
	private static final String CACHE_KEY_APPOINTMENT_SLOT = "appointment.appointmentSlot.";
	private static final String CACHE_KEY_CALENDAR_TEMPLATE = "appointment.calendarTemplate.";
	private static AppointmentFormCacheService _instance = new AppointmentFormCacheService();

	/**
	 * Private constructor
	 */
	private AppointmentFormCacheService() {
		initCache();
	}

	/**
	 * Get the instance of the cache service
	 * 
	 * @return The instance of the service
	 */
	public static AppointmentFormCacheService getInstance() {
		return _instance;
	}

	/**
	 * Get the cache key for a given form
	 * 
	 * @param nIdForm
	 *            The id of the form
	 * @return The cache key for the form
	 */
	public static String getFormCacheKey(int nIdForm) {
		return CACHE_KEY_FORM + nIdForm;
	}

	/**
	 * Get the cache key for a given form message
	 * 
	 * @param nIdForm
	 *            The id of the form
	 * @return The cache key for the form message
	 */
	public static String getFormMessageCacheKey(int nIdForm) {
		return CACHE_KEY_FORM_MESSAGE + nIdForm;
	}

	/**
	 * Get the cache key form appointment responses
	 * 
	 * @param nIdAppointment
	 *            The id of the appointment
	 * @return The cache key for the given appointment
	 */
	public String getAppointmentResponseCacheKey(int nIdAppointment) {
		return CACHE_KEY_APPOINTMENT_RESPONSE + nIdAppointment;
	}

	/**
	 * Get the cache key to lists of appointment times
	 * 
	 * @param nAppointmentDuration
	 *            The appointment duration
	 * @param nOpeningHour
	 *            The opening hour
	 * @param nOpeningMinutes
	 *            The opening minute
	 * @param nClosingHour
	 *            The closing hour
	 * @param nClosingMinutes
	 *            The closing minute
	 * @return The cache key
	 */
	public static String getListAppointmentTimesCacheKey(int nAppointmentDuration, int nOpeningHour,
			int nOpeningMinutes, int nClosingHour, int nClosingMinutes) {
		StringBuilder sbCacheKey = new StringBuilder(CACHE_KEY_LIST_APPOINTMENT_TIMES);
		sbCacheKey.append(nAppointmentDuration);
		sbCacheKey.append(nOpeningHour);
		sbCacheKey.append(nOpeningMinutes);
		sbCacheKey.append(nClosingHour);
		sbCacheKey.append(nClosingMinutes);

		return sbCacheKey.toString();
	}

	/**
	 * Get the cache key for a given appointment day
	 * 
	 * @param nIdDay
	 *            The id of the day
	 * @return The cache key for the given day
	 */
	public static String getAppointmentDayKey(int nIdDay) {
		return CACHE_KEY_APPOINTMENT_DAY + nIdDay;
	}

	/**
	 * Get the cache key for a given appointment day
	 * 
	 * @param nIdSlot
	 *            The id of the slot
	 * @return The cache key for the given slot
	 */
	public static String getAppointmentSlotKey(int nIdSlot) {
		return CACHE_KEY_APPOINTMENT_SLOT + nIdSlot;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return SERVICE_NAME;
	}

	/**
	 * Get the cache key for a calendar template
	 * 
	 * @param nId
	 *            The id of the calendar template
	 * @return The cache key of the calendar template
	 */
	public static String getCalendarTemplateCacheKey(int nId) {
		return CACHE_KEY_CALENDAR_TEMPLATE + nId;
	}
}
