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

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDay;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.portal.service.daemon.Daemon;

import java.sql.Date;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Daemon that creates slots and weeks of appointment forms
 */
public class DayCreationDaemon extends Daemon
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run(  )
    {
        for ( AppointmentForm form : AppointmentFormHome.getAppointmentFormsList(  ) )
        {
            AppointmentService.getService(  ).checkFormDays( form, false );

            Calendar calendar = new GregorianCalendar(  );
            Date dateLastMonday = AppointmentService.getService(  ).getDateLastMonday(  );
            calendar.setTime( dateLastMonday );
            calendar.add( Calendar.DAY_OF_MONTH, form.getNbWeeksToDisplay(  ) * 7 );

            List<AppointmentDay> listDays = AppointmentDayHome.getDaysBetween( form.getIdForm(  ), dateLastMonday,
                    new Date( calendar.getTimeInMillis(  ) ) );

            for ( AppointmentDay day : listDays )
            {
                AppointmentDayHome.resetDayFreePlaces( day.getIdDay(  ) );
            }
        }
    }
}
