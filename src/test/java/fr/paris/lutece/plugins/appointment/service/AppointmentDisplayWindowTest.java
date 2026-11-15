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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * The front office booking window ends on the Sunday of the last displayed week (ISO week), whatever the locale: the
 * front office locale is a bare language such as "fr", whose week starts on Sunday for the JDK.
 */
public class AppointmentDisplayWindowTest
{
    private static final LocalDate SUNDAY = LocalDate.of( 2026, 11, 15 );
    private static final LocalDate MONDAY = LocalDate.of( 2026, 11, 16 );
    private static final LocalDate WEDNESDAY = LocalDate.of( 2026, 11, 18 );

    @Test
    public void testOnSundayTwoWeeksEndOnTheNextSunday( )
    {
        assertEquals( LocalDate.of( 2026, 11, 22 ), AppointmentUtilities.getEndingDateOfDisplay( SUNDAY, 2 ) );
    }

    @Test
    public void testOnSundayOneWeekEndsTheSameDay( )
    {
        assertEquals( SUNDAY, AppointmentUtilities.getEndingDateOfDisplay( SUNDAY, 1 ) );
    }

    @Test
    public void testOnWednesdayTwoWeeksEndOnASunday( )
    {
        assertEquals( LocalDate.of( 2026, 11, 29 ), AppointmentUtilities.getEndingDateOfDisplay( WEDNESDAY, 2 ) );
    }

    @Test
    public void testOnMondayOneWeekEndsOnTheSameWeekSunday( )
    {
        assertEquals( LocalDate.of( 2026, 11, 22 ), AppointmentUtilities.getEndingDateOfDisplay( MONDAY, 1 ) );
    }
}
