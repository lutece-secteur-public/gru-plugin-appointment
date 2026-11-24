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
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * The answer of a "Liste déroulante SQL" question is a line of its query: read back from a saved appointment, it keeps the value saved
 */
public class AppointmentResponseSqlFieldTest extends LuteceTestCase
{
    @Test
    public void testAnswerOfAQueryIsReadBack( ) throws Exception
    {
        AppointmentFormDTO form = FormServiceTest.buildAppointmentForm( );
        int nIdForm = FormService.createAppointmentForm( form );
        LocalDate day = LocalDate.now( ).plusWeeks( 2 ).with( DayOfWeek.WEDNESDAY );
        Slot slot = SlotService.saveSlot( SlotTest.buildSlot( nIdForm, day.atTime( 10, 0 ), day.atTime( 10, 30 ), 3, 3, 0, 3, Boolean.TRUE, Boolean.FALSE ) );
        AppointmentDTO appointment = AppointmentTest.buildAppointmentDTO( nIdForm, slot, "bhsql@mdp.fr", "Jean", "Dupont", LocalTime.of( 10, 0 ),
                LocalTime.of( 10, 30 ), 1 );
        int nIdAppointment = AppointmentService.saveAppointment( appointment );
        int nIdEntry = createEntry( nIdForm, "bh sql" );
        int nIdOtherEntry = createEntry( nIdForm, "bh other" );
        Field fieldOfOtherQuestion = new Field( );
        fieldOfOtherQuestion.setParentEntry( EntryHome.findByPrimaryKey( nIdOtherEntry ) );
        fieldOfOtherQuestion.setTitle( "unrelated" );
        fieldOfOtherQuestion.setCode( "answer_choice" );
        int nIdUnrelated = FieldHome.create( fieldOfOtherQuestion );
        try
        {
            // one answer whose id has no row in genatt_field, one whose id is the answer choice of another question
            int [ ] ids = {
                    900002, nIdUnrelated
            };
            for ( int nIdField : ids )
            {
                Field fieldOfQuery = new Field( );
                fieldOfQuery.setIdField( nIdField );
                Response response = new Response( );
                response.setEntry( EntryHome.findByPrimaryKey( nIdEntry ) );
                response.setField( fieldOfQuery );
                response.setResponseValue( "Choix B" );
                ResponseHome.create( response );
                AppointmentResponseHome.insertAppointmentResponse( nIdAppointment, response.getIdResponse( ) );
            }

            for ( List<Response> listResponses : List.of( AppointmentResponseHome.findListResponse( nIdAppointment ),
                    AppointmentResponseService.findAndBuildListResponse( nIdAppointment, new MockHttpServletRequest( ) ) ) )
            {
                assertEquals( 2, listResponses.size( ) );
                for ( Response response : listResponses )
                {
                    assertNotNull( response.getField( ) );
                    assertEquals( "Choix B", response.getField( ).getTitle( ) );
                }
            }
        }
        finally
        {
            AppointmentResponseService.removeResponsesByIdAppointment( nIdAppointment );
            AppointmentHome.delete( nIdAppointment );
            User user = UserHome.findByFirstNameLastNameAndEmail( "Jean", "Dupont", "bhsql@mdp.fr" );
            if ( user != null )
            {
                UserHome.delete( user.getIdUser( ) );
            }
            EntryHome.remove( nIdEntry );
            EntryHome.remove( nIdOtherEntry );
            FormServiceTest.cleanForm( nIdForm );
        }
    }

    private static int createEntry( int nIdForm, String strTitle )
    {
        EntryType entryType = new EntryType( );
        entryType.setIdType( 124 );
        Entry entry = new Entry( );
        entry.setIdResource( nIdForm );
        entry.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        entry.setTitle( strTitle );
        entry.setEntryType( entryType );
        return EntryHome.create( entry );
    }
}
