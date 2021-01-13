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
package fr.paris.lutece.plugins.appointment.business.rule;

import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Reservation Rule objects
 * 
 * @author Laurent Payen
 *
 */
public final class ReservationRuleHome
{

    // Static variable pointed at the DAO instance
    private static IReservationRuleDAO _dao = SpringContextService.getBean( "appointment.reservationRuleDAO");
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private ReservationRuleHome( )
    {
    }

    /**
     * Create an instance of the ReservationRule class
     * 
     * @param reservationRule
     *            The instance of the ReservationRule which contains the informations to store
     * @return The instance of the ReservationRule which has been created with its primary key.
     */
    public static ReservationRule create( ReservationRule reservationRule )
    {
        _dao.insert( reservationRule, _plugin );

        return reservationRule;
    }

    /**
     * Update of the ReservationRule which is specified in parameter
     * 
     * @param reservationRule
     *            The instance of the ReservationRule which contains the data to store
     * @return The instance of the ReservationRule which has been updated
     */
    public static ReservationRule update( ReservationRule reservationRule )
    {
        _dao.update( reservationRule, _plugin );

        return reservationRule;
    }

    /**
     * Delete the ReservationRule whose identifier is specified in parameter
     * 
     * @param nKey
     *            The ReservationRule Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of the ReservationRule whose identifier is specified in parameter
     * 
     * @param nKey
     *            The ReservationRule primary key
     * @return an instance of the ReservationRule
     */
    public static ReservationRule findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Returns all the Reservation Rule of a form
     * 
     * @param nIdForm
     *            the Form Id
     * @return a list of ReservationRule of the form
     */
    public static List<ReservationRule> findByIdForm( int nIdForm )
    {
        return _dao.findByIdForm( nIdForm, _plugin );
    }

    /**
     * Returns the Reservation Rule with the given search parameters
     * 
     * @param nIdForm
     *            the Form Id
     * @param dateOfApply
     *            the date of apply
     * @return the ReservationRule
     */
    public static ReservationRule findByIdFormAndDateOfApply( int nIdForm, LocalDate dateOfApply )
    {
        return _dao.findByIdFormAndDateOfApply( nIdForm, dateOfApply, _plugin );
    }

    /**
     * Find in database a reservation rule of a form closest to a date
     * 
     * @param nIdForm
     *            the Form Id
     * @param dateOfApply
     *            the date of apply
     * @param plugin
     *            the plugin
     * @return the reservation rule that matches
     */
    public static ReservationRule findReservationRuleByIdFormAndClosestToDateOfApply( int nIdForm, LocalDate dateOfApply )
    {
        return _dao.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, dateOfApply, _plugin );
    }

}
