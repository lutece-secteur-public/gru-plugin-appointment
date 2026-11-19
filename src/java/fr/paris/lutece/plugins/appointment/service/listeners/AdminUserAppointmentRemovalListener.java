/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.Locale;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.RemovalListener;

/**
 * Refuses the deletion of a back office user to whom upcoming appointments are assigned: they would be left without agent. Past and cancelled
 * appointments do not refuse the deletion.
 */
public class AdminUserAppointmentRemovalListener implements RemovalListener
{
    private static final String MESSAGE_USER_HAS_UPCOMING_APPOINTMENTS = "appointment.message.adminUserHasUpcomingAppointments";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRemoved( String strIdUser )
    {
        return !NumberUtils.isDigits( strIdUser ) || AppointmentHome.countUpcomingByIdAdminUser( NumberUtils.toInt( strIdUser ) ) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRemovalRefusedMessage( String strIdUser, Locale locale )
    {
        Object [ ] args = {
                AppointmentHome.countUpcomingByIdAdminUser( NumberUtils.toInt( strIdUser ) )
        };

        return I18nService.getLocalizedString( MESSAGE_USER_HAS_UPCOMING_APPOINTMENTS, args, locale );
    }
}
