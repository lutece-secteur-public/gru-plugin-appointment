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
 * This class provides instances management methods for Closing Day objects
 * 
 * @author Laurent Payen
 *
 */
public final class ClosingDayHome
{
    // Static variable pointed at the DAO instance
    private static IClosingDayDAO _dao = SpringContextService.getBean( "appointment.closingDayDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private ClosingDayHome( )
    {
    }

    /**
     * Create an instance of the Form class
     * 
     * @param closingDay
     *            The instance of the ClosingDay which contains the informations to store
     * @return The instance of the ClosingDay which has been created with its primary key.
     */
    public static ClosingDay create( ClosingDay closingDay )
    {
        _dao.insert( closingDay, _plugin );

        return closingDay;
    }

    /**
     * Update of the ClosingDay which is specified in parameter
     * 
     * @param closingDay
     *            The instance of the ClosingDay which contains the data to store
     * @return The instance of the ClosingDay which has been updated
     */
    public static ClosingDay update( ClosingDay closingDay )
    {
        _dao.update( closingDay, _plugin );

        return closingDay;
    }

    /**
     * Delete the ClosingDay whose identifier is specified in parameter
     * 
     * @param nKey
     *            The ClosingDay Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of the ClosingDay whose identifier is specified in parameter
     * 
     * @param nKey
     *            The ClosingDay primary key
     * @return an instance of the ClosingDay
     */
    public static ClosingDay findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Returns all the Closing Days of a form
     * 
     * @param nIdForm
     *            the form id
     * @return the list of closing days of the form
     */
    public static List<ClosingDay> findByIdForm( int nIdForm )
    {
        return _dao.findByIdForm( nIdForm, _plugin );
    }

    /**
     * Returns a list of closing days of the form on a period
     * 
     * @param nIdForm
     *            the form Id
     * @param startingDate
     *            the starting date
     * @param endingDate
     *            the ending date
     * @return a list of closing days matches the criteria
     */
    public static List<ClosingDay> findByIdFormAndDateRange( int nIdForm, LocalDate startingDate, LocalDate endingDate )
    {
        return _dao.findByIdFormAndDateRange( nIdForm, startingDate, endingDate, _plugin );
    }

    /**
     * Returns the closing day
     * 
     * @param nIdForm
     *            the Form Id
     * @param dateOfClosingDay
     *            the date of the closing day
     * @return an instance of the ClosingDay if it exists, null otherwise
     */
    public static ClosingDay findByIdFormAndDateOfCLosingDay( int nIdForm, LocalDate dateOfClosingDay )
    {
        return _dao.findByIdFormAndDateOfClosingDay( nIdForm, dateOfClosingDay, _plugin );
    }
}
