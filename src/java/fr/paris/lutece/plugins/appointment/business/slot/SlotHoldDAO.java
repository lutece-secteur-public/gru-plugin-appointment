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
package fr.paris.lutece.plugins.appointment.business.slot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * This class provides Data Access methods for SlotHold objects (soft-hold of places during a booking in progress)
 *
 * @author City of Paris
 */
@ApplicationScoped
public class SlotHoldDAO implements ISlotHoldDAO
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_slot_hold ( id_slot, hold_token, nb_places, expired_date ) VALUES ( ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_slot_hold WHERE id_slot = ? AND hold_token = ?";
    private static final String SQL_QUERY_DELETE_BY_TOKEN = "DELETE FROM appointment_slot_hold WHERE hold_token = ?";
    private static final String SQL_QUERY_SELECT_BY_TOKEN = "SELECT id_slot, hold_token, nb_places, expired_date FROM appointment_slot_hold WHERE hold_token = ?";
    private static final String SQL_QUERY_SELECT_EXPIRED = "SELECT id_slot, hold_token, nb_places, expired_date FROM appointment_slot_hold WHERE expired_date <= CURRENT_TIMESTAMP";
    private static final String SQL_QUERY_DELETE_EXPIRED = "DELETE FROM appointment_slot_hold WHERE expired_date <= CURRENT_TIMESTAMP";
    private static final String SQL_QUERY_DELETE_BY_ID_SLOT = "DELETE FROM appointment_slot_hold WHERE id_slot = ?";
    private static final String SQL_QUERY_DELETE_BY_ID_FORM = "DELETE FROM appointment_slot_hold WHERE id_slot IN ( SELECT id_slot FROM appointment_slot WHERE id_form = ? )";
    private static final String SQL_QUERY_HAS_ACTIVE = "SELECT 1 FROM appointment_slot_hold WHERE id_slot = ? AND hold_token = ? AND expired_date > CURRENT_TIMESTAMP LIMIT 1";

    @Override
    public void insert( SlotHold slotHold, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, slotHold.getIdSlot( ) );
            daoUtil.setString( nIndex++, slotHold.getHoldToken( ) );
            daoUtil.setInt( nIndex++, slotHold.getNbPlaces( ) );
            daoUtil.setTimestamp( nIndex, Timestamp.valueOf( slotHold.getExpiredDateTime( ) ) );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdSlot, String strHoldToken, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdSlot );
            daoUtil.setString( 2, strHoldToken );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void deleteByToken( String strHoldToken, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_TOKEN, plugin ) )
        {
            daoUtil.setString( 1, strHoldToken );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public List<SlotHold> selectByToken( String strHoldToken, Plugin plugin )
    {
        List<SlotHold> listHold = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_TOKEN, plugin ) )
        {
            daoUtil.setString( 1, strHoldToken );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listHold.add( buildSlotHold( daoUtil ) );
            }
        }
        return listHold;
    }

    @Override
    public List<SlotHold> selectExpired( Plugin plugin )
    {
        List<SlotHold> listHold = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EXPIRED, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listHold.add( buildSlotHold( daoUtil ) );
            }
        }
        return listHold;
    }

    @Override
    public void deleteExpired( Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_EXPIRED, plugin ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void deleteByIdSlot( int nIdSlot, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_SLOT, plugin ) )
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
    public boolean hasActiveHold( int nIdSlot, String strHoldToken, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_HAS_ACTIVE, plugin ) )
        {
            daoUtil.setInt( 1, nIdSlot );
            daoUtil.setString( 2, strHoldToken );
            daoUtil.executeQuery( );
            return daoUtil.next( );
        }
    }

    /**
     * Build a SlotHold business object from the resultset
     *
     * @param daoUtil
     *            the prepare statement util object
     * @return a new SlotHold with all its attributes assigned
     */
    private SlotHold buildSlotHold( DAOUtil daoUtil )
    {
        int nIndex = 1;
        SlotHold slotHold = new SlotHold( );
        slotHold.setIdSlot( daoUtil.getInt( nIndex++ ) );
        slotHold.setHoldToken( daoUtil.getString( nIndex++ ) );
        slotHold.setNbPlaces( daoUtil.getInt( nIndex++ ) );
        slotHold.setExpiredDateTime( daoUtil.getTimestamp( nIndex ).toLocalDateTime( ) );
        return slotHold;
    }
}
