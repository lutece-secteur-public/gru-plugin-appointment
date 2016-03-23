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
package fr.paris.lutece.plugins.appointment.business.calendar;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Date;

import java.util.ArrayList;
import java.util.List;


/**
 * DAO form appointment days
 */
public class AppointmentHoliDaysDAO implements IAppointmentHoliDaysDAO
{
    private static final String SQL_QUERY_INSERT_HOLIDAYS = "INSERT INTO appointment_holidays (id_form,date_day) VALUES (?,?)";
    private static final String SQL_QUERY_REMOVE_HOLIDAYS = "DELETE FROM appointment_holidays WHERE id_form = ? AND date_day= ?";
    private static final String SQL_QUERY_SELECT_DAY = "SELECT date_day FROM appointment_holidays WHERE id_form = ? ";
    private static final String SQL_QUERY_REMOVE_DAYS_HOLIDAYS = "DELETE FROM appointment_holidays WHERE id_form = ? ";

    public void insert( Date date, int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_HOLIDAYS, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, nIdForm );
        daoUtil.setDate( nIndex, date );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public void remove( Date date, int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_HOLIDAYS, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.setDate( 2, date );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    public void remove( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_DAYS_HOLIDAYS, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Date> findByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DAY, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeQuery(  );

        List<Date> listDays = new ArrayList<Date>(  );

        while ( daoUtil.next(  ) )
        {
            listDays.add( daoUtil.getDate( 1 ) );
        }

        daoUtil.free(  );

        return listDays;
    }
}
