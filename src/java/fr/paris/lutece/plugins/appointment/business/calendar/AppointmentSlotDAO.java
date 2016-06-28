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

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Date;
import java.sql.Time;

import java.util.ArrayList;
import java.util.List;


/**
 * DAOUtuil for appointment slot
 */
public class AppointmentSlotDAO implements IAppointmentSlotDAO
{
    private static final String SQL_QUERY_NEW_PRIMARY_KEY = "SELECT MAX(id_slot) FROM appointment_slot";
    private static final String SQL_QUERY_CREATE = "INSERT INTO appointment_slot (id_slot, id_form, id_day, day_of_week, nb_places, starting_hour, starting_minute, ending_hour, ending_minute, is_enabled) VALUES (?,?,?,?,?,?,?,?,?,?) ";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_slot SET id_form = ?, id_day = ?, day_of_week = ?, nb_places = ?, starting_hour = ?, starting_minute = ?, ending_hour = ?, ending_minute = ?, is_enabled = ? WHERE id_slot = ? ";
    private static final String SQL_QUERY_DELETE_BY_ID = " DELETE FROM appointment_slot WHERE id_slot = ?";
    private static final String SQL_QUERY_DELETE_ALL_BY_ID_FORM = "DELETE FROM appointment_slot WHERE id_form = ?";
    private static final String SQL_QUERY_DELETE_BY_ID_FORM = SQL_QUERY_DELETE_ALL_BY_ID_FORM + " AND id_day = 0";
    private static final String SQL_QUERY_DELETE_BY_ID_DAY = "DELETE FROM appointment_slot WHERE id_day = ?";
    private static final String SQL_QUERY_DELETE_BY_ID_FORM_AND_DAY_OF_WEEK = SQL_QUERY_DELETE_BY_ID_FORM +
        " AND day_of_week = ?";
    private static final String SQL_QUERY_DELETE_OLD_SLOTS = "DELETE FROM appointment_slot WHERE id_day IN ( SELECT id_day FROM appointment_day WHERE date_day < ? ) AND id_slot NOT IN ( SELECT DISTINCT id_slot FROM appointment_appointment ) ";
    private static final String SQL_QUERY_SELECT = "SELECT id_slot, id_form, id_day, day_of_week, nb_places, starting_hour, starting_minute, ending_hour, ending_minute, is_enabled FROM appointment_slot";
    private static final String SQL_QUERY_SELECT_BY_PRIMARY_KEY = SQL_QUERY_SELECT + " WHERE id_slot = ?";
    private static final String SQL_QUERY_SELECT_BY_PRIMARY_KEY_WITH_FREE_PLACES = "SELECT id_slot, id_form, id_day, day_of_week, nb_places, starting_hour, starting_minute, ending_hour, ending_minute, is_enabled, (SELECT COUNT(id_appointment) FROM appointment_appointment app WHERE app.id_slot = slot.id_slot AND app.date_appointment = ? AND status != ? ) FROM appointment_slot slot WHERE id_slot = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT +
        " WHERE id_form = ? AND id_day = 0 ORDER BY starting_hour, starting_minute, day_of_week ASC";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_ALL = SQL_QUERY_SELECT +
        " WHERE id_form = ? ORDER BY starting_hour, starting_minute, day_of_week ASC";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_DAY_OF_WEEK = SQL_QUERY_SELECT +
        " WHERE id_form = ? AND id_day = 0 AND day_of_week = ? ORDER BY starting_hour, starting_minute, day_of_week ASC";

    //    private static final String SQL_QUERY_SELECT_BY_ID_FORM_WITH_FREE_PLACES = "SELECT id_slot, id_form, id_day, day_of_week, nb_places, starting_hour, starting_minute, ending_hour, ending_minute, is_enabled, (SELECT COUNT(id_appointment) FROM appointment_appointment app WHERE app.id_slot = slot.id_slot AND app.date_appointment = ? AND status != ? ) FROM appointment_slot slot WHERE id_form = ? AND id_day = 0 AND day_of_week = ? ORDER BY starting_hour, starting_minute, day_of_week ASC";
    private static final String SQL_QUERY_SELECT_BY_ID_DAY = SQL_QUERY_SELECT +
        " WHERE id_day = ? ORDER BY starting_hour, starting_minute, day_of_week ASC";
    private static final String SQL_QUERY_SELECT_BY_ID_DAY_WITH_FREE_PLACES = "SELECT id_slot, id_form, id_day, day_of_week, nb_places, starting_hour, starting_minute, ending_hour, ending_minute, is_enabled, (SELECT SUM(nb_place_reserved) FROM appointment_appointment app WHERE app.id_slot = slot.id_slot AND status != ? ) FROM appointment_slot slot WHERE id_day = ? ORDER BY starting_hour, starting_minute, day_of_week ASC";
    private static final String SQL_QUERY_FIND_LIMITS_MOMENT = "select count(*) nbre, TIME_FORMAT(CONCAT_WS(':',slot.starting_hour, slot.starting_minute),'%H:%i:%s') startHour, " +
        " TIME_FORMAT( CONCAT_WS(':',slot.ending_hour,slot.ending_minute),'%H:%i:%s') maxRdv," +
        "  slot.nb_places from appointment_appointment apmt, appointment_slot slot, appointment_form form" +
        " where  apmt.id_slot<>" + Appointment.Status.STATUS_UNRESERVED.getValeur(  ) + " and apmt.status<>" +
        Appointment.Status.STATUS_UNRESERVED.getValeur(  ) + " and apmt.id_slot=slot.id_slot and slot.id_day = ?" +
        " and form.id_form=slot.id_form and form.id_form= ? group by apmt.id_slot" +
        " order by TIME_FORMAT(CONCAT_WS(':',slot.starting_hour, slot.starting_minute),'%H:%i:%s') ";
    private static final String SQL_QUERY_FIND_SLOTS__UNAVAILABLED = "select id_slot, id_form, id_day, day_of_week, nb_places, starting_hour, starting_minute, ending_hour, ending_minute, is_enabled from appointment_slot slot" +
        " where slot.id_form=? and slot.id_day = ?" +
        " and TIME_FORMAT(CONCAT_WS(':',slot.starting_hour, slot.starting_minute),'%H:%i:%s') >=" +
        " TIME_FORMAT(?,'%H:%i:%s')  and" +
        " TIME_FORMAT(CONCAT_WS(':',slot.starting_hour, slot.starting_minute),'%H:%i:%s') <" +
        " TIME_FORMAT(?,'%H:%i:%s')" + " order by id_slot";
    private static final String SQL_QUERY_SELECT_BY_PRIMARY_KEY_WITH_FREE_PLACE = "SELECT id_slot, id_form, id_day, day_of_week, nb_places, starting_hour, starting_minute, ending_hour, ending_minute, is_enabled, (SELECT SUM(nb_place_reserved) FROM appointment_appointment app WHERE app.id_slot = slot.id_slot  AND status != ? ) FROM appointment_slot slot WHERE id_slot=?";
    private int _nDefaultSlotListSize;

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
        slot.setIdSlot( newPrimaryKey( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CREATE, plugin );
        int nIndex = 1;
        daoUtil.setInt( nIndex++, slot.getIdSlot(  ) );
        daoUtil.setInt( nIndex++, slot.getIdForm(  ) );
        daoUtil.setInt( nIndex++, slot.getIdDay(  ) );
        daoUtil.setInt( nIndex++, slot.getDayOfWeek(  ) );
        daoUtil.setInt( nIndex++, slot.getNbPlaces(  ) );
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
        daoUtil.setInt( nIndex++, slot.getNbPlaces(  ) );
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
    public void deleteByIdFormAndDayOfWeek( int nIdForm, int nDayOfWeek, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_FORM_AND_DAY_OF_WEEK, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.setInt( 2, nDayOfWeek );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOldSlots( Date dateMonday, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_OLD_SLOTS, plugin );
        daoUtil.setDate( 1, dateMonday );
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
    public AppointmentSlot findByPrimaryKeyWithFreePlaces( int nIdSlot, Date date, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PRIMARY_KEY_WITH_FREE_PLACES, plugin );
        daoUtil.setDate( 1, date );
        daoUtil.setInt( 2, Appointment.Status.STATUS_UNRESERVED.getValeur(  ) );
        daoUtil.setInt( 3, nIdSlot );
        daoUtil.executeQuery(  );

        AppointmentSlot slot = null;

        if ( daoUtil.next(  ) )
        {
            slot = getSlotDataFromDAOUtilWithFreePlaces( daoUtil );
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

        List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>( _nDefaultSlotListSize );

        while ( daoUtil.next(  ) )
        {
            listSlots.add( getSlotDataFromDAOUtil( daoUtil ) );
        }

        daoUtil.free(  );

        return listSlots;
    }

    @Override
    public List<AppointmentSlot> findByIdFormAll( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_ALL, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeQuery(  );

        List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>( _nDefaultSlotListSize );

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

        List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>( _nDefaultSlotListSize );

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

        List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>( _nDefaultSlotListSize );

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
    public List<AppointmentSlot> findByIdDayWithFreePlaces( int nIdDay, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_DAY_WITH_FREE_PLACES, plugin );
        daoUtil.setInt( 1, Appointment.Status.STATUS_UNRESERVED.getValeur(  ) );
        daoUtil.setInt( 2, nIdDay );
        daoUtil.executeQuery(  );

        List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>( _nDefaultSlotListSize );

        while ( daoUtil.next(  ) )
        {
            listSlots.add( getSlotDataFromDAOUtilWithFreePlaces( daoUtil ) );
        }

        daoUtil.free(  );

        return listSlots;
    }

    //    /**
    //     * {@inheritDoc}
    //     */
    //    @Override
    //    public List<AppointmentSlot> findByIdFormWithFreePlaces( int nIdForm, int nDayOfWeek, Date dateDay, Plugin plugin )
    //    {
    //        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_WITH_FREE_PLACES, plugin );
    //        daoUtil.setDate( 1, dateDay );
    //        daoUtil.setInt( 2, Appointment.STATUS_REJECTED );
    //        daoUtil.setInt( 3, nIdForm );
    //        daoUtil.setInt( 4, nDayOfWeek );
    //        daoUtil.executeQuery(  );
    //
    //        List<AppointmentSlot> listSlots = new ArrayList<AppointmentSlot>( _nDefaultSlotListSize );
    //
    //        while ( daoUtil.next(  ) )
    //        {
    //            listSlots.add( getSlotDataFromDAOUtilWithFreePlaces( daoUtil ) );
    //        }
    //
    //        daoUtil.free(  );
    //
    //        return listSlots;
    //    }

    /**
     * Get data of an appointment slot from a DAOUtil. Also load the number of
     * free places for the slot from the daoUtil
     * @param daoUtil The daoUtil to read data from. The method
     *            {@link DAOUtil#next()} must have been called before this
     *            method is called. The {@link DAOUtil#free()} method of the
     *            DAOUtil will NOT be called by this method.
     * @return The appointment slot
     */
    private AppointmentSlot getSlotDataFromDAOUtilWithFreePlaces( DAOUtil daoUtil )
    {
        AppointmentSlot slot = getSlotDataFromDAOUtil( daoUtil );
        slot.setNbRDV(daoUtil.getInt( 11 ));
        slot.setNbFreePlaces( slot.getNbPlaces(  ) - daoUtil.getInt( 11 ) );

        return slot;
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
        slot.setNbPlaces( daoUtil.getInt( nIndex++ ) );
        slot.setStartingHour( daoUtil.getInt( nIndex++ ) );
        slot.setStartingMinute( daoUtil.getInt( nIndex++ ) );
        slot.setEndingHour( daoUtil.getInt( nIndex++ ) );
        slot.setEndingMinute( daoUtil.getInt( nIndex++ ) );
        slot.setIsEnabled( daoUtil.getBoolean( nIndex++) );
        slot.setNbFreePlaces( slot.getNbPlaces(  ) );

        return slot;
    }

    /**
     * Set the default size of slot lists
     * @param nDefaultSlotListSize the default size of slot list
     */
    public void setDefaultSlotListSize( int nDefaultSlotListSize )
    {
        this._nDefaultSlotListSize = nDefaultSlotListSize;
    }

    /**
     * @param strToppings
     * @param plugin
     * @return
     */
    public List<AppointmentSlot> getSlotsUnavailable( int nIdDay, int nIdForm, Plugin plugin )
    {
        List<AppointmentSlot> objSlots = new ArrayList<AppointmentSlot>(  );
        List<String[]> objTab = updateAppointmentsUnavailable( nIdDay, nIdForm, plugin );

        if ( objTab.size(  ) > 0 )
        {
            for ( String[] tmpTab : objTab )
            {
                if ( Boolean.parseBoolean( tmpTab[4] ) )
                {
                    DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_SLOTS__UNAVAILABLED, plugin );
                    daoUtil.setInt( 1, nIdForm );
                    daoUtil.setInt( 2, nIdDay );
                    daoUtil.setString( 3, tmpTab[1] );
                    daoUtil.setString( 4, tmpTab[2] );
                    daoUtil.executeQuery(  );

                    while ( daoUtil.next(  ) )
                    {
                        objSlots.add( getSlotDataFromDAOUtil( daoUtil ) );
                    }

                    daoUtil.free(  );
                }
            }
        }

        return objSlots;
    }

    /**
      * Get maxLimits slots
      * @param slot
      * @param plugin
      */
    private static List<String[]> updateAppointmentsUnavailable( int nIdDay, int nIdForm, Plugin plugin )
    {
        List<String[]> objTab = new ArrayList<String[]>(  );
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_LIMITS_MOMENT, plugin );
        daoUtil.setInt( 1, nIdDay );
        daoUtil.setInt( 2, nIdForm );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            String[] strToppings = new String[5];
            strToppings[0] = daoUtil.getString( nIndex++ ); //Nbre appointments in this case
            strToppings[1] = daoUtil.getString( nIndex++ ); //Slot from this appointment
            strToppings[2] = daoUtil.getString( nIndex++ ); // maximum Appointment Hour from this rdv
            strToppings[3] = daoUtil.getString( nIndex++ ); // People max by appointment
            strToppings[4] = "false"; // Tag true or false tu update

            int nNumberappointmentbySlot = Integer.valueOf( strToppings[0] );
            int nMaxByAppointment = Integer.valueOf( strToppings[3] );

            if ( nNumberappointmentbySlot >= nMaxByAppointment )
            {
                strToppings[4] = "true";
            }

            nIndex = 1;
            objTab.add( strToppings );
        }

        daoUtil.free(  );

        return objTab;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppointmentSlot findByPrimaryKeyWithFreePlace( int nIdSlot, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PRIMARY_KEY_WITH_FREE_PLACE, plugin );
        daoUtil.setInt( 2, nIdSlot );
        daoUtil.setInt( 1, Appointment.Status.STATUS_UNRESERVED.getValeur(  ) );
        daoUtil.executeQuery(  );

        AppointmentSlot slot = new AppointmentSlot(  );

        if ( daoUtil.next(  ) )
        {
            int nIndex = 1;
            slot.setIdSlot( daoUtil.getInt( nIndex++ ) );
            slot.setIdForm( daoUtil.getInt( nIndex++ ) );
            slot.setIdDay( daoUtil.getInt( nIndex++ ) );
            slot.setDayOfWeek( daoUtil.getInt( nIndex++ ) );
            slot.setNbPlaces( daoUtil.getInt( nIndex++ ) );
            slot.setStartingHour( daoUtil.getInt( nIndex++ ) );
            slot.setStartingMinute( daoUtil.getInt( nIndex++ ) );
            slot.setEndingHour( daoUtil.getInt( nIndex++ ) );
            slot.setEndingMinute( daoUtil.getInt( nIndex++ ) );
            slot.setIsEnabled( daoUtil.getBoolean( nIndex++ ) );
            slot.setNbRDV(daoUtil.getInt( nIndex ));
            slot.setNbFreePlaces( slot.getNbPlaces(  ) - daoUtil.getInt( nIndex ) );

            return slot;
        }

        daoUtil.free(  );

        return slot;
    }
}
