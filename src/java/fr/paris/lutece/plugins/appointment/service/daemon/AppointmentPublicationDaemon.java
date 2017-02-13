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
package fr.paris.lutece.plugins.appointment.service.daemon;

import java.time.LocalDate;
import java.util.Collection;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.portal.service.daemon.Daemon;

/**
 * Daemon to publish and unpublish appointment form regarding the date of
 * valditity of the form
 * 
 * @author Laurent Payen
 */
public class AppointmentPublicationDaemon extends Daemon {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		Collection<Form> listForms = FormService.findAllForms();
		int nPublishedForms = 0;
		int nUnpublishedForms = 0;
		LocalDate dateNow = LocalDate.now();
		for (Form form : listForms) {
			if ((form.getStartingValidityDate() != null) && !form.isActive()
					&& (form.getStartingValidityDate().isBefore(dateNow))
					&& ((form.getEndingValidityDate() == null) || (form.getEndingValidityDate().isAfter(dateNow)))) {
				form.setIsActive(true);
				FormHome.update(form);
				nPublishedForms++;
			} else if ((form.getEndingValidityDate() != null) && form.isActive()
					&& (form.getEndingValidityDate().isBefore(dateNow))) {
				form.setIsActive(false);
				FormHome.update(form);
				nUnpublishedForms++;
			}
		}
		this.setLastRunLogs(nPublishedForms + " appointment form(s) have been published, and " + nUnpublishedForms
				+ " have been unpublished");
	}
}
