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
package fr.paris.lutece.plugins.appointment.business.slot;

import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Slot objects
 * 
 * @author Laurent Payen
 *
 */
public final class SlotDAO implements ISlotDAO
{

    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_slot (starting_date_time, ending_date_time, is_open, is_specific, max_capacity, nb_remaining_places, nb_potential_remaining_places, nb_places_taken, id_form) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_slot SET starting_date_time = ?, ending_date_time = ?, is_open = ?, is_specific = ?, max_capacity = ?, nb_remaining_places = ?, nb_potential_remaining_places = ?, nb_places_taken = ?, id_form = ? WHERE id_slot = ?";
    private static final String SQL_QUERY_UPDATE_POTENTIAL_REMAINING_PLACE = "UPDATE appointment_slot SET nb_potential_remaining_places = ? WHERE id_slot = ?";
    private static final String SQL_QUERY_UPDATE_POTENTIAL_REMAINING_PLACE_IF_SHUTDOWN = "UPDATE appointment_slot SET nb_potential_remaining_places = nb_remaining_places WHERE nb_potential_remaining_places < nb_remaining_places ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_slot WHERE id_slot = ?";
    private static final String SQL_QUERY_DELETE_BY_ID_FORM = "DELETE FROM appointment_slot WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_slot, starting_date_time, ending_date_time, is_open, is_specific, max_capacity, nb_remaining_places, nb_potential_remaining_places, nb_places_taken, id_form ";
    private static final String SQL_FROM_APPOINTMENT_SLOT = "FROM appointment_slot";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + SQL_FROM_APPOINTMENT_SLOT + " WHERE id_slot = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + SQL_FROM_APPOINTMENT_SLOT + " WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_IS_SPECIFIC = SQL_QUERY_SELECT_BY_ID_FORM + " AND is_specific = 1";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_RANGE = SQL_QUERY_SELECT_COLUMNS + SQL_FROM_APPOINTMENT_SLOT
            + " WHERE id_form = ? AND starting_date_time >= ? AND ending_date_time <= ?";
    private static final String SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM_AND_DATE_RANGE = SQL_QUERY_SELECT_COLUMNS + SQL_FROM_APPOINTMENT_SLOT
            + " WHERE id_form = ? AND starting_date_time >= ? AND ending_date_time <= ? AND is_open = 1";
    private static final String SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + SQL_FROM_APPOINTMENT_SLOT
            + " WHERE id_form = ? AND is_open = 1";
    private static final String SQL_QUERY_SELECT_SLOT_WITH_MAX_DATE = SQL_QUERY_SELECT_COLUMNS + "FROM appointment_slot slot"
            + " WHERE slot.id_form = ? ORDER BY slot.starting_date_time DESC LIMIT 1";
    private static final String SQL_QUERY_SELECT_BY_ID_APPOINTMENT = "SELECT slot.id_slot, slot.starting_date_time, slot.ending_date_time, slot.is_open, slot.is_specific, slot.max_capacity, slot.nb_remaining_places, slot.nb_potential_remaining_places, slot.nb_places_taken, slot.id_form "
            + " FROM appointment_slot slot INNER JOIN appointment_appointment_slot appt_slot ON (slot.id_slot = appt_slot.id_slot) "
            + " INNER JOIN appointment_appointment appt ON (appt_slot.id_appointment = appt.id_appointment ) WHERE appt.id_appointment = ?";

    private static final String SQL_QUERY_SELECT_SLOT_WITH_APPOINTMNT_BY_ID_FORM_AND_DATE_RANGE = "SELECT distinct slot.id_slot, slot.starting_date_time, slot.ending_date_time, slot.is_open, slot.is_specific, slot.max_capacity, slot.nb_remaining_places, slot.nb_potential_remaining_places, slot.nb_places_taken, slot.id_form  from appointment_slot slot JOIN appointment_appointment_slot appt_slot on ( slot.id_slot = appt_slot.id_slot ) WHERE slot.id_form = ? AND slot.starting_date_time >= ? AND slot.ending_date_time <= ? ";

    @Override
    public void insert( Slot slot, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, slot, plugin, true ) )
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                slot.setIdSlot( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    @Override
    public void update( Slot slot, Plugin plugin )
    {
        try ( DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, slot, plugin, false ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdSlot, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdSlot );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void deleteByIdForm( int nIdForm, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public Slot select( int nIdSlot, Plugin plugin )
    {
        Slot slot = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdSlot );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                slot = buildSlot( daoUtil );
            }
        }
        return slot;
    }

    @Override
    public List<Slot> findByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, Plugin plugin )
    {
        List<Slot> listSlots = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_RANGE, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setTimestamp( 2, Timestamp.valueOf( startingDateTime ) );
            daoUtil.setTimestamp( 3, Timestamp.valueOf( endingDateTime ) );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listSlots.add( buildSlot( daoUtil ) );
            }
        }
        return listSlots;
    }

    @Override
    public List<Slot> findSlotWithAppointmentByDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, Plugin plugin )
    {
        List<Slot> listSlots = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_SLOT_WITH_APPOINTMNT_BY_ID_FORM_AND_DATE_RANGE, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setTimestamp( 2, Timestamp.valueOf( startingDateTime ) );
            daoUtil.setTimestamp( 3, Timestamp.valueOf( endingDateTime ) );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listSlots.add( buildSlot( daoUtil ) );
            }
        }
        return listSlots;
    }

    @Override
    public List<Slot> findIsSpecificByIdForm( int nIdForm, Plugin plugin )
    {
        List<Slot> listSpecificSlots = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_AND_IS_SPECIFIC, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listSpecificSlots.add( buildSlot( daoUtil ) );
            }
        }
        return listSpecificSlots;
    }

    @Override
    public List<Slot> findByIdForm( int nIdForm, Plugin plugin )
    {
        List<Slot> listSlot = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listSlot.add( buildSlot( daoUtil ) );
            }
        }
        return listSlot;
    }

    @Override
    public List<Slot> findOpenSlotsByIdFormAndDateRange( int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime, Plugin plugin )
    {
        List<Slot> listSLot = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM_AND_DATE_RANGE, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setTimestamp( 2, Timestamp.valueOf( startingDateTime ) );
            daoUtil.setTimestamp( 3, Timestamp.valueOf( endingDateTime ) );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listSLot.add( buildSlot( daoUtil ) );
            }
        }
        return listSLot;
    }

    @Override
    public List<Slot> findOpenSlotsByIdForm( int nIdForm, Plugin plugin )
    {
        List<Slot> listSLot = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listSLot.add( buildSlot( daoUtil ) );
            }
        }
        return listSLot;
    }

    @Override
    public List<Slot> findByIdAppointment( int nIdAppointment, Plugin plugin )
    {
        List<Slot> listSlot = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_APPOINTMENT, plugin ) )
        {
            daoUtil.setInt( 1, nIdAppointment );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listSlot.add( buildSlot( daoUtil ) );
            }
        }
        return listSlot;
    }

    @Override
    public Slot findSlotWithMaxDate( int nIdForm, Plugin plugin )
    {
        Slot slot = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_SLOT_WITH_MAX_DATE, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                slot = buildSlot( daoUtil );
            }
        }
        return slot;
    }

    @Override
    public void updatePotentialRemainingPlaces( int nbPotentialRemainingPlaces, int nIdSlot, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_POTENTIAL_REMAINING_PLACE, plugin ) )
        {
            daoUtil.setInt( 1, nbPotentialRemainingPlaces );
            daoUtil.setInt( 2, nIdSlot );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Build a Slot business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new Slot with all its attributes assigned
     */
    private Slot buildSlot( DAOUtil daoUtil )
    {
        int nIndex = 1;
        Slot slot = new Slot( );
        slot.setIdSlot( daoUtil.getInt( nIndex++ ) );
        slot.setStartingTimeStampDate( daoUtil.getTimestamp( nIndex++ ) );
        slot.setEndingTimeStampDate( daoUtil.getTimestamp( nIndex++ ) );
        slot.setIsOpen( daoUtil.getBoolean( nIndex++ ) );
        slot.setIsSpecific( daoUtil.getBoolean( nIndex++ ) );
        slot.setMaxCapacity( daoUtil.getInt( nIndex++ ) );
        slot.setNbRemainingPlaces( daoUtil.getInt( nIndex++ ) );
        slot.setNbPotentialRemainingPlaces( daoUtil.getInt( nIndex++ ) );
        slot.setNbPlacestaken( daoUtil.getInt( nIndex++ ) );
        slot.setIdForm( daoUtil.getInt( nIndex ) );

        return slot;
    }

    /**
     * Build a daoUtil object with the Slot business object
     * 
     * @param query
     *            the query
     * @param slot
     *            the SLot
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, Slot slot, Plugin plugin, boolean isInsert )
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
        daoUtil.setTimestamp( nIndex++, slot.getStartingTimestampDate( ) );
        daoUtil.setTimestamp( nIndex++, slot.getEndingTimestampDate( ) );
        daoUtil.setBoolean( nIndex++, slot.getIsOpen( ) );
        daoUtil.setBoolean( nIndex++, slot.getIsSpecific( ) );
        daoUtil.setInt( nIndex++, slot.getMaxCapacity( ) );
        daoUtil.setInt( nIndex++, slot.getNbRemainingPlaces( ) );
        daoUtil.setInt( nIndex++, slot.getNbPotentialRemainingPlaces( ) );
        daoUtil.setInt( nIndex++, slot.getNbPlacesTaken( ) );
        daoUtil.setInt( nIndex++, slot.getIdForm( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, slot.getIdSlot( ) );
        }
        return daoUtil;
    }

    @Override
    public void resetPotentialRemainingPlaces( Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_POTENTIAL_REMAINING_PLACE_IF_SHUTDOWN, plugin ) )
        {
            daoUtil.executeUpdate( );
        }
    }
}
