/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.appointment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import org.apache.commons.fileupload.FileItem;

import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentResponseHome;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;

/**
 * Service Class for the appointment Response
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentResponseService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private AppointmentResponseService( )
    {
    }

    /**
     * Associate a response to an appointment
     * 
     * @param nIdAppointment
     *            the appointment
     * @param nIdResponse
     *            the response
     */
    public static void insertAppointmentResponse( int nIdAppointment, int nIdResponse )
    {
        AppointmentResponseHome.insertAppointmentResponse( nIdAppointment, nIdResponse );
    }

    /**
     * Remove the responses for the given entry
     * 
     * @param nIdEntry
     *            the entry
     */
    public static void removeResponseById( int nIdResponse )
    {
        AppointmentResponseHome.removeResponsesById( nIdResponse );
    }

    /**
     * Return the list of the responses of the appointment
     * 
     * @param nIdAppointment
     *            the appointment id
     * @return the list of the responses
     */
    public static List<Response> findListResponse( int nIdAppointment )
    {
        return AppointmentResponseHome.findListResponse( nIdAppointment );
    }

    /**
     * Return the list of the id of the response of the appointment
     * 
     * @param nIdAppointment
     *            the appointment id
     * @return the list of the response id
     */
    public static List<Integer> findListIdResponse( int nIdAppointment )
    {
        return AppointmentResponseHome.findListIdResponse( nIdAppointment );
    }

    /**
     * Find and build all the response of an appointment
     * 
     * @param nIdAppointment
     *            the appointment id
     * @param request
     *            the request
     * @return a list of response
     */
    public static List<Response> findAndBuildListResponse( int nIdAppointment, HttpServletRequest request )
    {
        List<Integer> listIdResponse = AppointmentResponseService.findListIdResponse( nIdAppointment );
        List<Response> listResponses = new ArrayList<>( listIdResponse.size( ) );
        for ( int nIdResponse : listIdResponse )
        {
            Response response = ResponseHome.findByPrimaryKey( nIdResponse );
            if ( response.getField( ) != null )
            {
                response.setField( FieldHome.findByPrimaryKey( response.getField( ).getIdField( ) ) );
            }
            if ( response.getFile( ) != null )
            {
                IFileStoreServiceProvider fileStoreService = FileService.getInstance( ).getFileStoreServiceProvider( );
                File file = fileStoreService.getFile( response.getFile( ).getFileKey( ) );
                response.setFile( file );
                String strIdEntry = Integer.toString( response.getEntry( ).getIdEntry( ) );
                FileItem fileItem = new GenAttFileItem( file.getPhysicalFile( ).getValue( ), file.getTitle( ), IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry,
                        response.getIdResponse( ) );
                AppointmentAsynchronousUploadHandler.getHandler( ).addFileItemToUploadedFilesList( fileItem, IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry,
                        request );
            }
            listResponses.add( response );
        }
        return listResponses;
    }

    /**
     * Build a map from the list response
     * 
     * @param listResponse
     *            the list response
     * @return a map with the nIdEntry as key and the list response for this entry as value
     */
    public static Map<Integer, List<Response>> buildMapFromListResponse( List<Response> listResponse )
    {
        HashMap<Integer, List<Response>> mapResponse = new HashMap<>( );
        for ( Response response : listResponse )
        {
            Integer nIdEntry = response.getEntry( ).getIdEntry( );
            List<Response> listResponseForThisEntry = mapResponse.computeIfAbsent( nIdEntry, ArrayList::new );
            listResponseForThisEntry.add( response );
        }
        return mapResponse;
    }

    /**
     * Remove all the response of an appointment
     * 
     * @param nIdAppointment
     *            the id of the appointment
     */
    public static void removeResponsesByIdAppointment( int nIdAppointment )
    {
        List<Response> listResponse = AppointmentResponseService.findListResponse( nIdAppointment );
        for ( Response response : listResponse )
        {
            AppointmentResponseService.removeResponseById( response.getIdResponse( ) );
        }
    }

    /**
     * Remove the response of an appointment
     * 
     * @param deleteBoOnly
     * @param nIdAppointment
     *            the id of the appointment
     */
    public static void removeResponsesByIdAppointmentAndBoOnly( int nIdAppointment, boolean deleteBoOnly )
    {
        List<Response> listResponse = AppointmentResponseService.findListResponse( nIdAppointment );
        for ( Response response : listResponse )
        {
            Entry entry = EntryHome.findByPrimaryKey( response.getEntry( ).getIdEntry( ) );
            if ( !entry.isOnlyDisplayInBack( ) || deleteBoOnly )
            {
                AppointmentResponseService.removeResponseById( response.getIdResponse( ) );
            }
        }
    }

    /**
     * Remove the updatable Responses of an appointment
     * 
     * @param nIdAppointment
     *            The id of the appointment
     * @param deleteBoOnly
     *            Set if the action is Back Office only
     */
    public static void removeUpdatableResponsesOnly( int nIdAppointment, boolean deleteBoOnly )
    {
        List<Response> listResponse = AppointmentResponseService.findListResponse( nIdAppointment );
        for ( Response response : listResponse )
        {
            Entry entry = EntryHome.findByPrimaryKey( response.getEntry( ).getIdEntry( ) );
            if ( !entry.isOnlyDisplayInBack( ) || deleteBoOnly )
            {
                Field updatableField = entry.getFieldByCode( IEntryTypeService.FIELD_IS_UPDATABLE );
                // In case the Response is not updatable, make sure it doesn't get removed
                if ( updatableField != null && !Boolean.valueOf( updatableField.getValue( ) ) )
                {
                    // Do nothing with this Response and process the next one
                    continue;
                }
                AppointmentResponseService.removeResponseById( response.getIdResponse( ) );
            }
        }
    }
}
