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

import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Week Definition objects
 * 
 * @author Laurent Payen
 *
 */
public final class WeekDefinitionHome
{
    // Static variable pointed at the DAO instance
    private static IWeekDefinitionDAO _dao = SpringContextService.getBean( "appointment.weekDefinitionDAO");
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private WeekDefinitionHome( )
    {
    }

    /**
     * Create an instance of the WeekDefinition class
     * 
     * @param weekDefinition
     *            The instance of the WeekDefinition which contains the informations to store
     * @return The instance of the WeekDefinition which has been created with its primary key.
     */
    public static WeekDefinition create( WeekDefinition weekDefinition )
    {
        _dao.insert( weekDefinition, _plugin );

        return weekDefinition;
    }

    /**
     * Update of the WeekDefinition which is specified in parameter
     * 
     * @param weekDefinition
     *            The instance of the WeekDefinition which contains the data to store
     * @return The instance of the WeekDefinition which has been updated
     */
    public static WeekDefinition update( WeekDefinition weekDefinition )
    {
        _dao.update( weekDefinition, _plugin );

        return weekDefinition;
    }

    /**
     * Delete the WeekDefinition whose identifier is specified in parameter
     * 
     * @param nKey
     *            The WeekDefinition Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Delete the WeekDefinition whose identifier is specified in parameter
     * 
     * @param nIdReservationRule
     *            The reservation rule Id
     */
    public static void deleteByIdReservationRule( int nIdReservationRule )
    {

        _dao.deleteByIdReservationRule( nIdReservationRule, _plugin );

    }

    /**
     * Returns an instance of the WeekDefinition whose identifier is specified in parameter
     * 
     * @param nKey
     *            The WeekDefinition primary key
     * @return an instance of the WeekDefinition
     */
    public static WeekDefinition findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Get all the week definitions of the form given
     * 
     * @param nIdForm
     *            the Form Id
     * @return the list of the week definitions of the form
     */
    public static List<WeekDefinition> findByIdForm( int nIdForm )
    {
        return _dao.findByIdForm( nIdForm, _plugin );
    }

    /**
     * Get the week definitions of a form for reservation rule
     * 
     * @param nIdReservationRule
     * @param plugin
     *            the plugin
     * @return list of week definition
     */
    public static List<WeekDefinition> findByReservationRule( int nIdReservationRule )
    {
        return _dao.findByReservationRule( nIdReservationRule, _plugin );
    }

    /**
     * Get week definition for the form id and the date of apply given
     * 
     * @param nIdForm
     *            the Form Id
     * @param dateOfApply
     *            the date of apply
     * @return the week definition
     */
    public static WeekDefinition findByIdFormAndDateOfApply( int nIdForm, LocalDate dateOfApply )
    {
        return _dao.findByIdFormAndDateOfApply( nIdForm, dateOfApply, _plugin );
    }

    /**
     * Get week definition for the form id and the date of apply given
     * 
     * @param nIdReservationRule
     *            the ReservationRule id
     * @param dateOfApply
     *            the date of apply
     * @return the week definition
     */
    public static WeekDefinition findByIdReservationRuleAndDateOfApply( int nIdReservationRule, LocalDate dateOfApply )
    {
        return _dao.findByIdReservationRuleAndDateOfApply( nIdReservationRule, dateOfApply, _plugin );
    }

}
