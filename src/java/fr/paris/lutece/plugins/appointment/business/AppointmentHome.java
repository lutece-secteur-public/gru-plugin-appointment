/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *         and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *         and the following disclaimer in the documentation and/or other materials
 *         provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *         contributors may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
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
package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentDayHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentFormCacheService;
import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.plugins.appointment.service.listeners.AppointmentListenerManager;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.sql.Date;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for
 * Appointment objects
 */
public final class AppointmentHome
{
    // Static variable pointed at the DAO instance
    private static IAppointmentDAO _dao = SpringContextService.getBean( "appointment.appointmentDAO" );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );
    private static AppointmentFormCacheService _cacheService = AppointmentFormCacheService.getInstance(  );

    /**
     * Private constructor - this class need not be instantiated
     */
    private AppointmentHome(  )
    {
    }

    /**
     * Create an instance of the appointment class
     * @param appointment The instance of the Appointment which contains the
     *            informations to store
     * @return The instance of appointment which has been created with its
     *         primary key.
     */
    public static Appointment create( Appointment appointment )
    {
        _dao.insert( appointment, _plugin );

        // We update the number of free places of the day
        AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
        AppointmentDayHome.decrementDayFreePlaces( slot.getIdDay(  ) );

        return appointment;
    }

    /**
     * Update of the appointment which is specified in parameter
     * @param appointment The instance of the Appointment which contains the
     *            data to store
     */
    public static void update( Appointment appointment )
    {
        Appointment appointmentFromDb = findByPrimaryKey( appointment.getIdAppointment(  ) );

        _dao.store( appointment, _plugin );

        // If the status changed, we check if we need to update the number of free places of the associated day
        if ( appointment.getStatus(  ) != appointmentFromDb.getStatus(  ) )
        {
            if ( ( appointmentFromDb.getStatus(  ) != Appointment.STATUS_REJECTED ) &&
                    ( appointment.getStatus(  ) == Appointment.STATUS_REJECTED ) )
            {
                AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
                AppointmentDayHome.incrementDayFreePlaces( slot.getIdDay(  ) );
            }
            else if ( ( appointmentFromDb.getStatus(  ) == Appointment.STATUS_REJECTED ) &&
                    ( appointment.getStatus(  ) != Appointment.STATUS_REJECTED ) )
            {
                AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
                AppointmentDayHome.decrementDayFreePlaces( slot.getIdDay(  ) );
            }
        }
    }

    /**
     * Remove the appointment whose identifier is specified in parameter, and
     * removed any associated response
     * @param nAppointmentId The appointment Id
     */
    public static void remove( int nAppointmentId )
    {
        AppointmentListenerManager.notifyListenersAppointmentRemoval( nAppointmentId );

        Appointment appointment = findByPrimaryKey( nAppointmentId );

        for ( int nIdResponse : _dao.findListIdResponse( nAppointmentId, _plugin ) )
        {
            ResponseHome.remove( nIdResponse );
        }

        _dao.deleteAppointmentResponse( nAppointmentId, _plugin );
        _dao.delete( nAppointmentId, _plugin );

        if ( appointment.getStatus(  ) != Appointment.STATUS_REJECTED )
        {
            AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
            AppointmentDayHome.incrementDayFreePlaces( slot.getIdDay(  ) );
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a appointment whose identifier is specified in
     * parameter
     * @param nKey The appointment primary key
     * @return an instance of Appointment
     */
    public static Appointment findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the appointment objects and returns them in form of
     * a collection
     * @return the collection which contains the data of all the appointment
     *         objects
     */
    public static List<Appointment> getAppointmentsList(  )
    {
        return _dao.selectAppointmentsList( _plugin );
    }

    /**
     * Get the number of appointments associated with a given form and with a
     * date after a given date
     * @param nIdForm The id of the form
     * @param date The minimum date of appointments to consider
     * @return The number of appointments associated with the form
     */
    public static int countAppointmentsByIdForm( int nIdForm, Date date )
    {
        return _dao.countAppointmentsByIdForm( nIdForm, date, _plugin );
    }

    /**
     * Load the data of appointment objects associated with a given form and
     * returns them in a collection
     * @param nIdForm the id of the form
     * @return the collection which contains the data of appointment objects
     */
    public static List<Appointment> getAppointmentsListByIdForm( int nIdForm )
    {
        return _dao.selectAppointmentsListByIdForm( nIdForm, _plugin );
    }

    /**
     * Get the list of appointments matching a given filter
     * @param appointmentFilter The filter appointments must match
     * @return The list of appointments that match the given filter
     */
    public static List<Appointment> getAppointmentListByFilter( AppointmentFilter appointmentFilter )
    {
        return _dao.selectAppointmentListByFilter( appointmentFilter, _plugin );
    }

    /**
     * Get the list of ids of appointments matching a given filter
     * @param appointmentFilter The filter appointments must match
     * @return The list of ids of appointments that match the given filter
     */
    public static List<Integer> getAppointmentIdByFilter( AppointmentFilter appointmentFilter )
    {
        return _dao.selectAppointmentIdByFilter( appointmentFilter, _plugin );
    }

    /**
     * Get a list of appointments from their ids
     * @param listIdAppointments The list of ids of appointments to get
     * @return The list of appointments which ids are given in parameters
     */
    public static List<Appointment> getAppointmentListById( List<Integer> listIdAppointments )
    {
        return _dao.selectAppointmentListById( listIdAppointments, AppointmentFilter.CONSTANT_DEFAULT_ORDER_BY, true,
            _plugin );
    }

    /**
     * Get a list of appointments from their ids
     * @param listIdAppointments The list of ids of appointments to get
     * @param strOrderBy The name of the column to sort rows
     * @param bSortAsc True to sort ascending, false otherwise
     * @return The list of appointments which ids are given in parameters
     */
    public static List<Appointment> getAppointmentListById( List<Integer> listIdAppointments, String strOrderBy,
        boolean bSortAsc )
    {
        return _dao.selectAppointmentListById( listIdAppointments, strOrderBy, bSortAsc, _plugin );
    }

    /**
     * Get the number of appointment of a given date and associated with a given
     * form
     * @param dateAppointment The date of appointments to count
     * @param nIdForm The id of the appointment form
     * @return the number of appointments, or 0 if no appointment was found
     */
    public static int getNbAppointmentByIdDay( Date dateAppointment, int nIdForm )
    {
        return _dao.getNbAppointmentByIdDay( dateAppointment, nIdForm, _plugin );
    }

    // -----------------------------------------------
    // Appointment response management
    // -----------------------------------------------

    /**
     * Associates a response to an appointment
     * @param nIdAppointment The id of the appointment
     * @param nIdResponse The id of the response
     */
    public static void insertAppointmentResponse( int nIdAppointment, int nIdResponse )
    {
        _dao.insertAppointmentResponse( nIdAppointment, nIdResponse, _plugin );
        _cacheService.removeKey( _cacheService.getAppointmentResponseCacheKey( nIdAppointment ) );
    }

    /**
     * Get the list of id of responses associated with an appointment
     * @param nIdAppointment the id of the appointment
     * @return the list of responses, or an empty list if no response was found
     */
    public static List<Integer> findListIdResponse( int nIdAppointment )
    {
        String strCacheKey = _cacheService.getAppointmentResponseCacheKey( nIdAppointment );
        List<Integer> listIdResponse = (List<Integer>) _cacheService.getFromCache( strCacheKey );

        if ( listIdResponse == null )
        {
            listIdResponse = _dao.findListIdResponse( nIdAppointment, _plugin );
            _cacheService.putInCache( strCacheKey, new ArrayList<Integer>( listIdResponse ) );
        }
        else
        {
            listIdResponse = new ArrayList<Integer>( listIdResponse );
        }

        return listIdResponse;
    }

    /**
     * Get the list of responses associated with an appointment
     * @param nIdAppointment the id of the appointment
     * @return the list of responses, or an empty list if no response was found
     */
    public static List<Response> findListResponse( int nIdAppointment )
    {
        List<Integer> listIdResponse = findListIdResponse( nIdAppointment );
        List<Response> listResponse = new ArrayList<Response>( listIdResponse.size(  ) );

        for ( Integer nIdResponse : listIdResponse )
        {
            listResponse.add( ResponseHome.findByPrimaryKey( nIdResponse ) );
        }

        return listResponse;
    }

    /**
     * Find the id of the appointment associated with a given response
     * @param nIdResponse The id of the response
     * @return The id of the appointment, or 0 if no appointment is associated
     *         with he given response.
     */
    public static int findIdAppointmentByIdResponse( int nIdResponse )
    {
        return _dao.findIdAppointmentByIdResponse( nIdResponse, _plugin );
    }

    /**
     * Remove the association between an appointment and responses
     * @param nIdAppointment The id of the appointment
     */
    public static void removeAppointmentResponse( int nIdAppointment )
    {
        _dao.deleteAppointmentResponse( nIdAppointment, _plugin );
        _cacheService.removeKey( _cacheService.getAppointmentResponseCacheKey( nIdAppointment ) );
    }

    /**
     * Remove every appointment responses associated with a given entry.
     * @param nIdEntry The id of the entry
     */
    public static void removeResponsesByIdEntry( int nIdEntry )
    {
        ResponseFilter filter = new ResponseFilter(  );
        filter.setIdEntry( nIdEntry );

        List<Response> listResponses = ResponseHome.getResponseList( filter );

        for ( Response response : listResponses )
        {
            _dao.removeAppointmentResponsesByIdResponse( response.getIdResponse(  ), _plugin );
            ResponseHome.remove( response.getIdResponse(  ) );
        }

        _cacheService.resetCache(  );
    }
}
