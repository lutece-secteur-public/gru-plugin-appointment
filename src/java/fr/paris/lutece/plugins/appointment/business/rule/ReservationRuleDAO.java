/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.appointment.business.rule;

import java.sql.Date;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.UtilDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Reservation Rule objects
 * 
 * @author Laurent Payen
 *
 */
public final class ReservationRuleDAO extends UtilDAO implements IReservationRuleDAO
{

    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_reservation_rule SET date_of_apply = ?, max_capacity_per_slot = ?, max_people_per_appointment =?, id_form = ? WHERE id_reservation_rule = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_reservation_rule, date_of_apply, max_capacity_per_slot, max_people_per_appointment, id_form FROM appointment_reservation_rule";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_reservation_rule (date_of_apply, max_capacity_per_slot, max_people_per_appointment, id_form) VALUES ( ?, ?, ?, ?)";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_reservation_rule = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_reservation_rule WHERE id_reservation_rule = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_OF_APPLY = SQL_QUERY_SELECT_BY_ID_FORM + " AND date_of_apply = ?";

    @Override
    public void insert( ReservationRule reservationRule, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, reservationRule, plugin, true );
        try
        {
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                reservationRule.setIdReservationRule( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        finally
        {
            daoUtil.free( );
        }
    }

    @Override
    public void update( ReservationRule reservationRule, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, reservationRule, plugin, false );
        executeUpdate( daoUtil );
    }

    @Override
    public void delete( int nIdReservationRule, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdReservationRule );
        executeUpdate( daoUtil );
    }

    @Override
    public ReservationRule select( int nIdReservationRule, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        ReservationRule reservationRule = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
            daoUtil.setInt( 1, nIdReservationRule );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                reservationRule = buildReservationRule( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return reservationRule;
    }

    @Override
    public List<ReservationRule> findByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<ReservationRule> listReservationRule = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listReservationRule.add( buildReservationRule( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listReservationRule;
    }

    @Override
    public ReservationRule findByIdFormAndDateOfApply( int nIdForm, LocalDate dateOfApply, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        ReservationRule reservationRule = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_OF_APPLY, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setDate( 2, Date.valueOf( dateOfApply ) );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                reservationRule = buildReservationRule( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return reservationRule;
    }

    /**
     * Build a ReservationRule business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new ReservationRule with all its attributes assigned
     */
    private ReservationRule buildReservationRule( DAOUtil daoUtil )
    {
        int nIndex = 1;
        ReservationRule reservationRule = new ReservationRule( );
        reservationRule.setIdReservationRule( daoUtil.getInt( nIndex++ ) );
        reservationRule.setSqlDateOfApply( daoUtil.getDate( nIndex++ ) );
        reservationRule.setMaxCapacityPerSlot( daoUtil.getInt( nIndex++ ) );
        reservationRule.setMaxPeoplePerAppointment( daoUtil.getInt( nIndex++ ) );
        reservationRule.setIdForm( daoUtil.getInt( nIndex ) );
        return reservationRule;
    }

    /**
     * Build a daoUtil object with the ReservationRule business object
     * 
     * @param query
     *            the query
     * @param reservationRule
     *            the ReservationRule
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, ReservationRule reservationRule, Plugin plugin, boolean isInsert )
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
        daoUtil.setDate( nIndex++, reservationRule.getSqlDateOfApply( ) );
        daoUtil.setInt( nIndex++, reservationRule.getMaxCapacityPerSlot( ) );
        daoUtil.setInt( nIndex++, reservationRule.getMaxPeoplePerAppointment( ) );
        daoUtil.setInt( nIndex++, reservationRule.getIdForm( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, reservationRule.getIdReservationRule( ) );
        }
        return daoUtil;
    }

    /**
     * Execute a safe update (Free the connection in case of error when execute the query)
     * 
     * @param daoUtil
     *            the daoUtil
     */
    private void executeUpdate( DAOUtil daoUtil )
    {
        try
        {
            daoUtil.executeUpdate( );
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
    }

}
