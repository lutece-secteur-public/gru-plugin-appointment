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
package fr.paris.lutece.plugins.appointment.service.listeners;

import java.util.List;
import java.util.Locale;

/**
 * Interface for listeners that should be notified when appointments are removed or when the date changed. <b>The listener must be a Spring bean.</b>
 * 
 * @author Laurent Payen
 * 
 */
public interface IAppointmentListener
{
    /**
     * Notify the listener that an appointment has been removed
     * 
     * @param nIdAppointment
     *            The id of the appointment
     */
    void notifyAppointmentRemoval( int nIdAppointment );

    /**
     * Notify the listener that the date of an appointment has changed.
     * 
     * @param nIdAppointment
     *            the id of the appointment
     * @param nIdSlot
     *            The new slot of the appointment
     * @param locale
     *            The locale to display error messages with
     * @return The message to display to the user, if any.
     */
    String appointmentDateChanged( int nIdAppointment, List<Integer> listIdSlot, Locale locale );

    /**
     * Notify the listener that an appointment has been creates
     * 
     * @param nIdAppointment
     *            The id of the appointment
     */
    void notifyAppointmentCreated( int nIdAppointment );

    /**
     * Notify the listener that an appointment has been update
     * 
     * @param nIdAppointment
     *            The id of the appointment
     */
    void notifyAppointmentUpdated( int nIdAppointment );

}
