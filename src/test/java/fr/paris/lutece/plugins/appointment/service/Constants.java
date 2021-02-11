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

package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Constants
{

    // DATE TIMES
    public final static LocalDateTime STARTING_DATE_1 = LocalDateTime.parse( "2022-12-05T10:00" );
    public final static LocalDateTime STARTING_DATE_2 = LocalDateTime.parse( "2022-06-04T10:00" );
    public final static LocalDateTime STARTING_DATE_3 = LocalDateTime.parse( "2022-06-05T10:00" );
    public final static LocalDateTime STARTING_DATE_4 = LocalDateTime.parse( "2022-06-15T10:00" );
    public final static LocalDateTime STARTING_DATE_5 = LocalDateTime.parse( "2022-06-07T10:00" );
    public final static LocalDateTime STARTING_DATE_6 = LocalDateTime.parse( "2022-06-14T10:00" );
    public final static LocalDateTime STARTING_DATE_7 = LocalDateTime.parse( "2022-06-16T10:00" );
    public final static LocalDateTime STARTING_DATE_8 = LocalDateTime.parse( "2022-06-27T10:00" );
    public final static LocalDateTime STARTING_DATE_9 = LocalDateTime.parse( "2022-06-28T17:00" );
    public final static LocalDateTime STARTING_DATE_10 = LocalDateTime.parse( "2022-06-24T10:00" );
    public final static LocalDateTime STARTING_DATE_11 = LocalDateTime.parse( "2022-06-25T10:00" );
    public final static LocalDateTime STARTING_DATE_12 = LocalDateTime.parse( "2022-12-05T09:00" );
    public final static LocalDateTime STARTING_DATE_13 = LocalDateTime.parse( "2022-12-06T09:00" );
    public final static LocalDateTime STARTING_DATE_14 = LocalDateTime.parse( "2022-12-07T10:00" );
    public final static LocalDateTime STARTING_DATE_15 = LocalDateTime.parse( "2022-12-06T10:00" );
    public final static LocalDateTime ENDING_DATE_1 = LocalDateTime.parse( "2022-12-05T10:30" );
    public final static LocalDateTime ENDING_DATE_2 = LocalDateTime.parse( "2022-06-04T10:30" );
    public final static LocalDateTime ENDING_DATE_3 = LocalDateTime.parse( "2022-06-05T10:30" );
    public final static LocalDateTime ENDING_DATE_4 = LocalDateTime.parse( "2022-06-15T10:30" );
    public final static LocalDateTime ENDING_DATE_5 = LocalDateTime.parse( "2022-06-07T10:30" );
    public final static LocalDateTime ENDING_DATE_6 = LocalDateTime.parse( "2022-06-14T10:30" );
    public final static LocalDateTime ENDING_DATE_7 = LocalDateTime.parse( "2022-06-16T10:30" );
    public final static LocalDateTime ENDING_DATE_8 = LocalDateTime.parse( "2022-06-27T10:30" );
    public final static LocalDateTime ENDING_DATE_9 = LocalDateTime.parse( "2022-06-28T17:30" );
    public final static LocalDateTime ENDING_DATE_10 = LocalDateTime.parse( "2022-06-24T10:30" );
    public final static LocalDateTime ENDING_DATE_11 = LocalDateTime.parse( "2022-06-25T10:30" );
    public final static LocalDateTime ENDING_DATE_12 = LocalDateTime.parse( "2022-12-05T09:30" );
    public final static LocalDateTime ENDING_DATE_13 = LocalDateTime.parse( "2022-12-06T09:30" );
    public final static LocalDateTime ENDING_DATE_14 = LocalDateTime.parse( "2022-12-06T10:30" );
    public final static LocalDateTime ENDING_DATE_15 = LocalDateTime.parse( "2022-12-06T11:30" );
    public final static LocalDateTime DATE_TIME_1 = LocalDateTime.parse( "2022-06-05T10:15" );
    public final static LocalDateTime DATE_TIME_2 = LocalDateTime.parse( "2022-06-10T10:30" );
    public final static LocalDateTime DATE_TIME_3 = LocalDateTime.parse( "2022-06-25T11:15" );
    public final static LocalDateTime DATE_TIME_4 = LocalDateTime.parse( "2022-06-10T12:30" );
    public final static LocalDateTime DATE_TIME_5 = LocalDateTime.parse( "2022-06-25T00:00" );

    //DATES
    public final static LocalDate DATE_1 =  LocalDate.parse( "2022-06-04" );
    public final static LocalDate DATE_2 =  LocalDate.parse( "2022-06-30" );
    public final static LocalDate DATE_3 =  LocalDate.parse( "2022-06-26" );
    public final static LocalDate DATE_4 = LocalDate.parse( "2022-06-20" );
    public final static LocalDate DATE_5  = LocalDate.parse( "2025-12-25" );
    public final static LocalDate DATE_6 = LocalDate.parse( "2028-06-22" );
    public final static LocalDate DATE_7 = LocalDate.parse( "2028-06-20" );
    public final static LocalDate DATE_8 = LocalDate.parse( "2028-06-21" );
    public final static LocalDate DATE_9  = LocalDate.parse( "2022-09-01" );
    public final static LocalDate DATE_10  = LocalDate.parse( "2022-05-01" );
    public final static LocalDate DATE_11  = LocalDate.parse( "2022-05-08" );
    public final static LocalDate DATE_12  = LocalDate.parse( "2022-07-14" );
    public final static LocalDate DATE_13  = LocalDate.parse( "2022-08-15" );
    public final static LocalDate DATE_14  = LocalDate.parse( "2022-12-05" );
    public final static LocalDate DATE_15  = LocalDate.parse( "2022-12-16" );
    public final static LocalDate DATE_16  = LocalDate.parse( "2022-12-01" );
    public final static LocalDate DATE_17  = LocalDate.parse( "2022-12-31" );
    public final static LocalDate DATE_18  = LocalDate.parse( "2022-06-05" );
    public final static LocalDate DATE_19  = LocalDate.parse( "2022-06-10" );
    public final static LocalDate DATE_20  = LocalDate.parse( "2022-06-25" );
    public final static LocalDate DATE_21  = LocalDate.parse( "2022-06-15" );
    public final static LocalDate DATE_22 = LocalDate.parse( "2028-06-19" );

    //MISC
    public final static int NB_REMAINING_PLACES_1 = 1;
    public final static int NB_REMAINING_PLACES_2 = 2;
    public final static int NB_REMAINING_PLACES_3 = 3;

    // User
    public final static String FIRST_NAME_1 = "Jean";
    public final static String FIRST_NAME_2 = "Gérard";
    public final static String LAST_NAME_1 = "Dupont";
    public final static String LAST_NAME_2 = "Durand";
    public final static String EMAIL_1 = "mdp@mdp.fr";
    public final static String EMAIL_2 = "gerard.durand@mdp.fr";
    public final static String EMAIL_3 = "jean.dupont@mdp.fr";
    public final static String EMAIL_4 = "gerard.durand@mdp.fr";

    // TIME
    public final static LocalTime STARTING_TIME_1 = LocalTime.parse( "10:00" );
    public final static LocalTime STARTING_TIME_2 = LocalTime.parse( "17:00" );
    public final static LocalTime STARTING_TIME_3 = LocalTime.parse( "09:00" );
    public final static LocalTime STARTING_TIME_4 = LocalTime.parse( "11:00" );
    public final static LocalTime ENDING_TIME_1 = LocalTime.parse( "10:30" );
    public final static LocalTime ENDING_TIME_2 = LocalTime.parse( "17:30" );
    public final static LocalTime ENDING_TIME_3 = LocalTime.parse( "09:30" );
    public final static LocalTime TIME_1 = LocalTime.parse( "09:22" );
    public final static LocalTime TIME_2 = LocalTime.parse( "10:47" );
    public final static LocalTime TIME_3 = LocalTime.parse( "09:01" );
    public final static LocalTime TIME_4 = LocalTime.parse( "10:42" );
    public final static LocalTime TIME_5 = LocalTime.parse( "17:10" );
    public final static LocalTime TIME_6 = LocalTime.parse( "19:00" );
    public final static LocalTime TIME_7 = LocalTime.parse( "19:30" );
    public final static LocalTime TIME_8 =LocalTime.parse( "18:00" );
    public final static LocalTime TIME_9 =LocalTime.parse( "20:00" );
}