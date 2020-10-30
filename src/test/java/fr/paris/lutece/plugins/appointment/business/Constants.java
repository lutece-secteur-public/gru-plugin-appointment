/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.appointment.business;

import java.time.LocalDateTime;

public class Constants
{

    // Slot
    public final static LocalDateTime STARTING_DATE_1 = LocalDateTime.parse( "2017-01-27T09:00" );
    public final static LocalDateTime STARTING_DATE_2 = LocalDateTime.parse( "2017-01-28T09:30" );
    public final static LocalDateTime STARTING_DATE_3 = LocalDateTime.parse( "2017-01-29T09:30" );
    public final static LocalDateTime ENDING_DATE_1 = LocalDateTime.parse( "2017-01-27T09:30" );
    public final static LocalDateTime ENDING_DATE_2 = LocalDateTime.parse( "2017-01-28T10:00" );
    public final static LocalDateTime ENDING_DATE_3 = LocalDateTime.parse( "2017-01-29T10:00" );
    public final static boolean IS_OPEN_1 = true;
    public final static boolean IS_OPEN_2 = false;
    public final static int NB_REMAINING_PLACES_1 = 1;
    public final static int NB_REMAINING_PLACES_2 = 2;
    public final static int NB_REMAINING_PLACES_3 = 3;

    // User
    public final static String GUID_1 = "guid1";
    public final static String GUID_2 = "guid2";
    public final static String FIRST_NAME_1 = "firstName1";
    public final static String FIRST_NAME_2 = "firstName2";
    public final static String LAST_NAME_1 = "lastName1";
    public final static String LAST_NAME_2 = "lastName2";
    public final static String EMAIL_1 = "email1";
    public final static String EMAIL_2 = "email2";
    public final static String PHONE_NUMBER_1 = "0605040302";
    public final static String PHONE_NUMBER_2 = "0605040303";
}
