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

import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Working Day objects
 * 
 * @author Laurent Payen
 *
 */
public final class WorkingDayHome
{
    // Static variable pointed at the DAO instance
    private static IWorkingDayDAO _dao = SpringContextService.getBean( "appointment.workingDayDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private WorkingDayHome( )
    {
    }

    /**
     * Create an instance of the WorkingDay class
     * 
     * @param workingDay
     *            The instance of the WorkingDay which contains the informations to store
     * @return The instance of the WorkingDay which has been created with its primary key.
     */
    public static WorkingDay create( WorkingDay workingDay )
    {
        _dao.insert( workingDay, _plugin );

        return workingDay;
    }

    /**
     * Update of the WorkingDay which is specified in parameter
     * 
     * @param workingDay
     *            The instance of the WorkingDay which contains the data to store
     * @return The instance of the WorkingDay which has been updated
     */
    public static WorkingDay update( WorkingDay workingDay )
    {
        _dao.update( workingDay, _plugin );

        return workingDay;
    }

    /**
     * Delete the WorkingDay whose identifier is specified in parameter
     * 
     * @param nKey
     *            The WorkingDay Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Delete the working day whose id reservation rule is specified in parameter
     * 
     * @param nIdReservationRule
     *            the reservation rule id
     */
    public static void deleteByIdReservationRule( int nIdReservationRule )
    {

        _dao.deleteByIdReservationRule( nIdReservationRule, _plugin );
    }

    /**
     * Returns an instance of the WorkingDay whose identifier is specified in parameter
     * 
     * @param nKey
     *            The WorkingDay primary key
     * @return an instance of the WorkingDay
     */
    public static WorkingDay findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Find the Working Day of the Reservation Rule
     * 
     * @param nIdReservationRule
     *            the Reservation Id Rule
     * @return a list of the workingDay defined rule
     */
    public static List<WorkingDay> findByIdReservationRule( int nIdReservationRule )
    {
        return _dao.findByIdReservationRule( nIdReservationRule, _plugin );
    }
    
}
