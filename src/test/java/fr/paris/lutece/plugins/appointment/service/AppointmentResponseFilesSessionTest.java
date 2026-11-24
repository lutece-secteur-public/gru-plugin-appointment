/*
 * Copyright (c) 2002-2025, City of Paris
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.AppointmentTest;
import fr.paris.lutece.plugins.appointment.business.SlotTest;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentResponseHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.file.GenericAttributeFileService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The responses of a saved appointment read for a summary (FO cancel page) do not put its files in the upload session
 */
public class AppointmentResponseFilesSessionTest extends LuteceTestCase
{
    @Test
    public void testFilesNotPutInTheUploadSession( ) throws Exception
    {
        AppointmentFormDTO form = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( form );
        LocalDate day = LocalDate.now( ).plusWeeks( 2 ).with( DayOfWeek.TUESDAY );
        Slot slot = SlotService.saveSlot( SlotTest.buildSlot( nIdForm, day.atTime( 10, 0 ), day.atTime( 10, 30 ), 3, 3, 0, 3, Boolean.TRUE, Boolean.FALSE ) );
        AppointmentDTO appointment = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "bhfile@mdp.fr", "Jean", "Dupont", LocalTime.of( 10, 0 ),
                LocalTime.of( 10, 30 ), 1 );
        int nIdAppointment = AppointmentService.saveAppointment( appointment );
        int nIdEntry = createEntry( nIdForm );
        try
        {
            PhysicalFile physicalFile = new PhysicalFile( );
            physicalFile.setValue( "bh".getBytes( ) );
            File file = new File( );
            file.setTitle( "a.pdf" );
            file.setMimeType( "application/pdf" );
            file.setSize( 2 );
            file.setPhysicalFile( physicalFile );
            file.setFileKey( CDI.current( ).select( GenericAttributeFileService.class ).get( ).save( file ) );
            Response response = new Response( );
            response.setEntry( EntryHome.findByPrimaryKey( nIdEntry ) );
            response.setFile( file );
            ResponseHome.create( response );
            AppointmentResponseHome.insertAppointmentResponse( nIdAppointment, response.getIdResponse( ) );
            MockHttpServletRequest request = new MockHttpServletRequest( );

            List<Response> listResponses = AppointmentResponseService.findAndBuildListResponse( nIdAppointment, request );

            assertEquals( 1, listResponses.size( ) );
            assertEquals( "a.pdf", listResponses.get( 0 ).getFile( ).getTitle( ) );
            List<MultipartItem> listSessionFiles = CDI.current( ).select( AppointmentAsynchronousUploadHandler.class ).get( )
                    .getListUploadedFiles( "attribute" + nIdEntry, request.getSession( ) );
            assertTrue( listSessionFiles == null || listSessionFiles.isEmpty( ) );
        }
        finally
        {
            AppointmentResponseService.removeResponsesByIdAppointment( nIdAppointment );
            AppointmentHome.delete( nIdAppointment );
            User user = UserHome.findByFirstNameLastNameAndEmail( "Jean", "Dupont", "bhfile@mdp.fr" );
            if ( user != null )
            {
                UserHome.delete( user.getIdUser( ) );
            }
            EntryHome.remove( nIdEntry );
            FormServiceTest.cleanForm( nIdForm );
        }
    }

    private static int createEntry( int nIdForm )
    {
        EntryType entryType = new EntryType( );
        entryType.setIdType( 124 );
        Entry entry = new Entry( );
        entry.setIdResource( nIdForm );
        entry.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        entry.setTitle( "bh file" );
        entry.setEntryType( entryType );
        return EntryHome.create( entry );
    }
}
