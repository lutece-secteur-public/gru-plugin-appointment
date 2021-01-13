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
package fr.paris.lutece.plugins.appointment.web.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class AppointmentPackageDTO implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 7039306494521789575L;
    /**
     * the number of booked seats for this appointment package
     */
    private int _nNbBookedSeats;
    private Set<AppointmentDTO> _listAppointmentDTO = new HashSet<>( );

    /**
     * Get the number of booked seats for the appointment
     * 
     * @return the number of booked seats
     */
    public int getNbBookedSeats( )
    {
        return _nNbBookedSeats;
    }

    /**
     * Set the number of booked seats for the appointment package
     * 
     * @param nNumberOfPlacesReserved
     *            the number to set
     */
    public void setNbBookedSeats( int nNumberOfPlacesReserved )
    {
        _nNbBookedSeats = nNumberOfPlacesReserved;
    }

    /**
     * Get the list of the AppointmentDTO
     * 
     * @return the list of the AppointmentDTO
     */
    public Set<AppointmentDTO> getListAppointmentDTO( )
    {
        return _listAppointmentDTO;
    }

    /**
     * Set the list of the AppointmentDTO
     * 
     * @param listAppointmentDTO
     *            the list of the AppointmentDTO to set
     */
    public void setListAppointmentDTO( Set<AppointmentDTO> listAppointmentDTO )
    {
        _listAppointmentDTO = listAppointmentDTO;
    }

    public void addAppointmentDto( AppointmentDTO appointmentDTO )
    {
        _listAppointmentDTO.add( appointmentDTO );
    }
}
