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
package fr.paris.lutece.plugins.appointment.service.daemon;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.portal.service.daemon.Daemon;

import java.sql.Date;

import java.util.Collection;


/**
 * Daemon to publish and unpublish appointment forms
 */
public class AppointmentPublicationDaemon extends Daemon
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run(  )
    {
        Collection<AppointmentForm> listForms = AppointmentFormHome.getAppointmentFormsList(  );
        Date dateNow = new Date( System.currentTimeMillis(  ) );
        int nPublishedForms = 0;
        int nUnpublishedForms = 0;

        for ( AppointmentForm form : listForms )
        {
            if ( ( form.getDateStartValidity(  ) != null ) && !form.getIsActive(  ) &&
                    ( form.getDateStartValidity(  ).getTime(  ) < dateNow.getTime(  ) ) &&
                    ( ( form.getDateEndValidity(  ) == null ) ||
                    ( form.getDateEndValidity(  ).getTime(  ) > dateNow.getTime(  ) ) ) )
            {
                form.setIsActive( true );
                AppointmentFormHome.update( form );
                nPublishedForms++;
            }
            else if ( ( form.getDateEndValidity(  ) != null ) && form.getIsActive(  ) &&
                    ( form.getDateEndValidity(  ).getTime(  ) < dateNow.getTime(  ) ) )
            {
                form.setIsActive( false );
                AppointmentFormHome.update( form );
                nUnpublishedForms++;
            }
        }

        this.setLastRunLogs( nPublishedForms + " appointment form(s) have been published, and " + nUnpublishedForms +
            " have been unpublished" );
    }
}
