/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import java.util.ArrayList;
import java.util.List;


/**
 * DAOUtuil for appointment slot
 */
public class AppointmentSlotDAO implements IAppointmentSlotDAO
{
    private static final String SQL_QUERY_NEW_PRIMARY_KEY = "SELECT MAX(id_slot) FROM appointment_slot";
    private static final String SQL_QUERY_CREATE = "INSERT INTO appointment_slot (id_slot, id_form, id_day, day_of_week, nb_free_places, starting_hour, starting_minute, ending_hour, ending_minute, is_enabled) VALUES (?,?,?,?,?,?,?,?,?,?) ";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_slot SET id_form = ?, id_day = ?, day_of_week = ?, nb_free_places = ?, starting_hour = ?, starting_minute = ?, ending_hour = ?, ending_minute = ?, is_enabled = ? WHERE id_slot = ? ";
    private static final String SQL_QUERY_DELETE_BY_ID = " DELETE FROM appointment_slot WHERE id_slot = ?";
    private static final String SQL_QUERY_DELETE_ALL_BY_ID_FORM = "DELETE FROM appointment_slot WHERE id_form = ?";
    private static final String SQL_QUERY_DELETE_BY_ID_FORM = SQL_QUERY_DELETE_ALL_BY_ID_FORM + " AND id_day = 0";
    private static final String SQL_QUERY_DELETE_BY_ID_DAY = "DELETE FROM appointment_slot WHERE id_day = ?";
    private static final String SQL_QUERY_DELETE_BY_ID_FORM_AND_DAY_OF_WEEK = SQL_QUERY_DELETE_BY_ID_FORM +
        " AND day_of_week = ?";
    private static final String SQL_QUERY_SELECT = "SELECT id_slot, id_form, id_day, day_of_week, nb_free_places, starting_hour, starting_minute, ending_hour, ending_minute, is_enabled FROM appointment_slot";
    private static final String SQL_QUERY_SELECT_BY_PRIMARY_KEY = SQL_QUERY_SELECT + " WHERE id_slot = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT +
        " WHERE id_form = ? AND id_day = 0 ORDER BY starting_hour, starting_minute, day_of_week ASC";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_DAY_OF_WEEK = SQL_QUERY_SELECT +
        " WHERE id_form = ? AND id_day = 0 AND day_of_week = ? ORDER BY starting_hour, starting_minute, day_of_week ASC";
    private static final String SQL_QUERY_SELECT_BY_ID_DAY = SQL_QUERY_SELECT +
        " WHERE id_day = ? ORDER BY day_of_week ASC";

    //    private static final String SQL_QUERY_UPDATE_BY_ID_FORM_AND_DAY_OF_WEEK = "UPDATE appointment_slot SET is_enabled = ? WHERE id_form = ? AND id_day = 0 AND day_of_week = ?";
    //    private static final String SQL_QUERY_UPDATE_BY_ID_DAY = "UPDATE appointment_slot SET is_enabled = ? WHERE id_day = ? ";

    /**
     * Get a new primary key for a slot
     * @param plugin the plugin
     * @return The new value of the primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PRIMARY_KEY, plugin );
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void create( AppointmentSlot slot, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CREATE, plugin );
        int nIndex = 1;
        slot.setIdSlot( newPrimaryKey( plugin ) );
        daoUtil.setInt( nIndex++, slot.getIdSlot(  ) );
        daoUtil.setInt( nIndex++, slot.getIdForm(  ) );
        daoUtil.setInt( nIndex++, slot.getIdDay(  ) );
        daoUtil.setInt( nIndex++, slot.getDayOfWeek(  ) );
        daoUtil.setInt( nIndex++, slot.getNbFreePlaces(  ) );
        daoUtil.setInt( nIndex++, slot.getStartingHour(  ) );
        daoUtil.setInt( nIndex++, slot.getStartingMinute(  ) );
        daoUtil.setInt( nIndex++, slot.getEndingHour(  ) );
        daoUtil.setInt( nIndex++, slot.getEndingMinute(  ) );
        daoUtil.setBoolean( nIndex, slot.getIsEnabled(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update( AppointmentSlot slot, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, slot.getIdForm(  ) );
        daoUtil.setInt( nIndex++, slot.getIdDay(  ) );
        daoUtil.setInt( nIndex++, slot.getDayOfWeek(  ) );
        daoUtil.setInt( nIndex++, slot.getNbFreePlaces(  ) );
        daoUtil.setInt( nIndex++, slot.getStartingHour(  ) );
        daoUtil.setInt( nIndex++, slot.getStartingMinute(  ) );
        daoUtil.setInt( nIndex++, slot.getEndingHour(  ) );
        daoUtil.setInt( nIndex++, slot.getEndingMinute(  ) );
        daoUtil.setBoolean( nIndex++, slot.getIsEnabled(  ) );
        daoUtil.setInt( nIndex, slot.getIdSlot(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdSlot, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID, plugin );
        daoUtil.setInt( 1, nIdSlot );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL_BY_ID_FORM, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdDay( int nIdDay, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_DAY, plugin );
        daoUtil.setInt( 1, nIdDay );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_FORM, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppointmentSlot findByPrimaryKey( int nIdSlot, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdSlot );
        daoUtil.executeQuery(  );

        AppointmentSlot slot = null;

        if ( daoUtil.next(  ) )
        {
            slot = getSlotDataFromDAOUtil( daoUtil );
        }

        daoUtil.free(  );

        return slot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentSlot> findByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeQuery(  );

        List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>(  );

        while ( daoUtil.next(  ) )
        {
            listSlots.add( getSlotDataFromDAOUtil( daoUtil ) );
        }

        daoUtil.free(  );

        return listSlots;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentSlot> findByIdFormAndDayOfWeek( int nIdForm, int nDayOfWeek, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_AND_DAY_OF_WEEK, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.setInt( 2, nDayOfWeek );
        daoUtil.executeQuery(  );

        List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>(  );

        while ( daoUtil.next(  ) )
        {
            listSlots.add( getSlotDataFromDAOUtil( daoUtil ) );
        }

        daoUtil.free(  );

        return listSlots;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppointmentSlot> findByIdDay( int nIdDay, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_DAY, plugin );
        daoUtil.setInt( 1, nIdDay );
        daoUtil.executeQuery(  );

        List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>(  );

        while ( daoUtil.next(  ) )
        {
            listSlots.add( getSlotDataFromDAOUtil( daoUtil ) );
        }

        daoUtil.free(  );

        return listSlots;
    }

    //    /**
    //     * {@inheritDoc}
    //     */
    //    @Override
    //    public void updateByIdFormAndDayOfWeek( int nIdForm, boolean bEnable, int nDayOfWeek, Plugin plugin )
    //    {
    //        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_BY_ID_FORM_AND_DAY_OF_WEEK, plugin );
    //        daoUtil.setBoolean( 1, bEnable );
    //        daoUtil.setInt( 2, nIdForm );
    //        daoUtil.setInt( 3, nDayOfWeek );
    //        daoUtil.executeUpdate( );
    //        daoUtil.free( );
    //    }
    //
    //    /**
    //     * {@inheritDoc}
    //     */
    //    @Override
    //    public void updateByIdDay( int nIdDay, boolean bEnable, Plugin plugin )
    //    {
    //        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_BY_ID_DAY, plugin );
    //        daoUtil.setBoolean( 1, bEnable );
    //        daoUtil.setInt( 2, nIdDay );
    //        daoUtil.executeUpdate( );
    //        daoUtil.free( );
    //    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdFormAndDayOfWeek( int nIdForm, int nDayOfWeek, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_FORM_AND_DAY_OF_WEEK, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.setInt( 2, nDayOfWeek );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Get data of an appointment slot from a DAOUtil
     * @param daoUtil The daoUtil to read data from. The method
     *            {@link DAOUtil#next()} must have been called before this
     *            method is called. The {@link DAOUtil#free()} method of the
     *            DAOUtil will NOT be called by this method.
     * @return The appointment slot
     */
    private AppointmentSlot getSlotDataFromDAOUtil( DAOUtil daoUtil )
    {
        AppointmentSlot slot = new AppointmentSlot(  );
        int nIndex = 1;
        slot.setIdSlot( daoUtil.getInt( nIndex++ ) );
        slot.setIdForm( daoUtil.getInt( nIndex++ ) );
        slot.setIdDay( daoUtil.getInt( nIndex++ ) );
        slot.setDayOfWeek( daoUtil.getInt( nIndex++ ) );
        slot.setNbFreePlaces( daoUtil.getInt( nIndex++ ) );
        slot.setStartingHour( daoUtil.getInt( nIndex++ ) );
        slot.setStartingMinute( daoUtil.getInt( nIndex++ ) );
        slot.setEndingHour( daoUtil.getInt( nIndex++ ) );
        slot.setEndingMinute( daoUtil.getInt( nIndex++ ) );
        slot.setIsEnabled( daoUtil.getBoolean( nIndex ) );

        return slot;
    }
}
