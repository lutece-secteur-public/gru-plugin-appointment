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
package fr.paris.lutece.plugins.appointment.business.planning;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Time Slot objects
 * 
 * @author Laurent Payen
 *
 */
public final class TimeSlotDAO implements ITimeSlotDAO
{

    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_time_slot ( starting_time, ending_time, is_open, max_capacity, id_working_day) VALUES ( ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_time_slot SET starting_time = ?, ending_time = ?, is_open = ?, max_capacity = ?, id_working_day = ? WHERE id_time_slot = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_time_slot WHERE id_time_slot = ?";
    private static final String SQL_QUERY_DELETE_BY_ID_DAY = "DELETE FROM appointment_time_slot WHERE id_working_day = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_time_slot, starting_time, ending_time, is_open, max_capacity, id_working_day FROM appointment_time_slot";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_time_slot = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_WORKING_DAY = SQL_QUERY_SELECT_COLUMNS + " WHERE id_working_day = ?";

    @Override
    public void insert( TimeSlot timeSlot, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, timeSlot, plugin, true ) )
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                timeSlot.setIdTimeSlot( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    @Override
    public void update( TimeSlot timeSlot, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, timeSlot, plugin, false ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdTimeSlot, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdTimeSlot );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void deleteByIdWorkingDay( int nIdWorkingDay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_DAY, plugin ) )
        {
            daoUtil.setInt( 1, nIdWorkingDay );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public TimeSlot select( int nIdTimeSlot, Plugin plugin )
    {
        TimeSlot timeSlot = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdTimeSlot );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                timeSlot = buildTimeSlot( daoUtil );
            }
        }
        return timeSlot;
    }

    @Override
    public List<TimeSlot> findByIdWorkingDay( int nIdWorkingDay, Plugin plugin )
    {
        List<TimeSlot> listTimeSLots = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_WORKING_DAY, plugin ) )
        {
            daoUtil.setInt( 1, nIdWorkingDay );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listTimeSLots.add( buildTimeSlot( daoUtil ) );
            }
        }
        return listTimeSLots;
    }

    /**
     * Build a time slot business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new time slot with all its attributes assigned
     */
    private TimeSlot buildTimeSlot( DAOUtil daoUtil )
    {
        int nIndex = 1;
        TimeSlot timeSlot = new TimeSlot( );
        timeSlot.setIdTimeSlot( daoUtil.getInt( nIndex++ ) );
        timeSlot.setSqlStartingTime( daoUtil.getTime( nIndex++ ) );
        timeSlot.setSqlEndingTime( daoUtil.getTime( nIndex++ ) );
        timeSlot.setIsOpen( daoUtil.getBoolean( nIndex++ ) );
        timeSlot.setMaxCapacity( daoUtil.getInt( nIndex++ ) );
        timeSlot.setIdWorkingDay( daoUtil.getInt( nIndex ) );
        return timeSlot;
    }

    /**
     * Build a daoUtil object with time slot business object
     * 
     * @param query
     *            the query
     * @param timeSlot
     *            the time slot
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, TimeSlot timeSlot, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = null;
        if ( isInsert )
        {
            daoUtil = new DAOUtil( query, Statement.RETURN_GENERATED_KEYS, plugin );
        }
        else
        {
            daoUtil = new DAOUtil( query, plugin );
        }
        daoUtil.setTime( nIndex++, timeSlot.getStartingTimeSqlTime( ) );
        daoUtil.setTime( nIndex++, timeSlot.getEndingTimeSqlTime( ) );
        daoUtil.setBoolean( nIndex++, timeSlot.getIsOpen( ) );
        daoUtil.setInt( nIndex++, timeSlot.getMaxCapacity( ) );
        daoUtil.setInt( nIndex++, timeSlot.getIdWorkingDay( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, timeSlot.getIdTimeSlot( ) );
        }
        return daoUtil;
    }
}
