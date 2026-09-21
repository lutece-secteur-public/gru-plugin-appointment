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
package fr.paris.lutece.plugins.appointment.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * Round-trips the session-scoped state of the booking wizard (the {@link AppointmentFormDTO} and the
 * {@link AppointmentDTO} of the booking in progress) through Java serialization, failing the build if a
 * non-serializable field enters the graph.
 */
public class AppointmentAppSessionPassivationTest
{

    /**
     * The AppointmentDTO of the booking in progress (with its slot and responses) survives passivation.
     *
     * @throws IOException
     *             if the round-trip fails
     * @throws ClassNotFoundException
     *             if a class cannot be resolved on read
     */
    @Test
    public void testAppointmentDtoSurvivesSessionPassivation( ) throws IOException, ClassNotFoundException
    {
        AppointmentDTO original = buildAppointment( );

        AppointmentDTO restored = roundTrip( original );

        assertNotNull( restored );
        assertEquals( original.getNbBookedSeats( ), restored.getNbBookedSeats( ) );
        assertEquals( original.getDateOfTheAppointment( ), restored.getDateOfTheAppointment( ) );
        assertEquals( original.getSlot( ).size( ), restored.getSlot( ).size( ) );
        assertEquals( original.getSlot( ).get( 0 ).getIdSlot( ), restored.getSlot( ).get( 0 ).getIdSlot( ) );
        assertEquals( original.getListResponse( ).size( ), restored.getListResponse( ).size( ) );
        assertEquals( "hello", restored.getListResponse( ).get( 0 ).getResponseValue( ) );
    }

    /**
     * The AppointmentFormDTO held in session survives passivation.
     *
     * @throws IOException
     *             if the round-trip fails
     * @throws ClassNotFoundException
     *             if a class cannot be resolved on read
     */
    @Test
    public void testAppointmentFormDtoSurvivesSessionPassivation( ) throws IOException, ClassNotFoundException
    {
        AppointmentFormDTO original = new AppointmentFormDTO( );
        original.setIdForm( 42 );
        original.setTitle( "passivation" );

        AppointmentFormDTO restored = roundTrip( original );

        assertNotNull( restored );
        assertEquals( 42, restored.getIdForm( ) );
        assertEquals( "passivation", restored.getTitle( ) );
    }

    /**
     * The full session state of the wizard (form + both appointment DTOs + places to take) survives passivation,
     * also catching cross-field regressions.
     *
     * @throws IOException
     *             if the round-trip fails
     * @throws ClassNotFoundException
     *             if a class cannot be resolved on read
     */
    @Test
    public void testWizardSessionStateSurvivesPassivation( ) throws IOException, ClassNotFoundException
    {
        WizardSessionState original = new WizardSessionState( );
        original._nNbPlacesToTake = 2;
        original._appointmentForm = new AppointmentFormDTO( );
        original._appointmentForm.setIdForm( 42 );
        original._notValidatedAppointment = buildAppointment( );
        original._validatedAppointment = buildAppointment( );

        WizardSessionState restored = roundTrip( original );

        assertNotNull( restored._appointmentForm );
        assertNotNull( restored._notValidatedAppointment );
        assertNotNull( restored._validatedAppointment );
        assertEquals( 2, restored._nNbPlacesToTake );
        assertEquals( 42, restored._appointmentForm.getIdForm( ) );
        assertEquals( original._notValidatedAppointment.getSlot( ).size( ), restored._notValidatedAppointment.getSlot( ).size( ) );
    }

    /**
     * Build a representative AppointmentDTO of a booking in progress (one slot, one response).
     *
     * @return the appointment DTO
     */
    private static AppointmentDTO buildAppointment( )
    {
        AppointmentDTO appointment = new AppointmentDTO( );
        appointment.setNbBookedSeats( 1 );
        appointment.setNbMaxPotentialBookedSeats( 1 );
        appointment.setDateOfTheAppointment( "23/06/2026" );

        Slot slot = new Slot( );
        slot.setIdSlot( 7 );
        slot.setStartingDateTime( LocalDateTime.of( 2026, 6, 23, 9, 0 ) );
        slot.setEndingDateTime( LocalDateTime.of( 2026, 6, 23, 9, 30 ) );
        slot.setMaxCapacity( 2 );
        slot.setNbRemainingPlaces( 1 );
        appointment.addSlot( slot );

        Response response = new Response( );
        response.setIdResponse( 1 );
        response.setResponseValue( "hello" );
        List<Response> listResponse = new ArrayList<>( );
        listResponse.add( response );
        appointment.setListResponse( listResponse );

        return appointment;
    }

    /**
     * Serialize then deserialize an object, mirroring what the container does to passivate a session.
     *
     * @param <T>
     *            the serializable type
     * @param object
     *            the object to round-trip
     * @return the deserialized copy
     * @throws IOException
     *             if serialization fails
     * @throws ClassNotFoundException
     *             if a class cannot be resolved on read
     */
    @SuppressWarnings( "unchecked" )
    private static <T extends Serializable> T roundTrip( T object ) throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream( );
        try ( ObjectOutputStream oos = new ObjectOutputStream( baos ) )
        {
            oos.writeObject( object );
        }
        try ( ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream( baos.toByteArray( ) ) ) )
        {
            return (T) ois.readObject( );
        }
    }

    /**
     * Holder mirroring the session-scoped fields of {@link AppointmentApp}, to passivate them together.
     */
    private static class WizardSessionState implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private int _nNbPlacesToTake;
        private AppointmentFormDTO _appointmentForm;
        private AppointmentDTO _notValidatedAppointment;
        private AppointmentDTO _validatedAppointment;
    }
}
