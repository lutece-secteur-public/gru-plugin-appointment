/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.test.LuteceTestCase;

import java.sql.Date;


public class AppointmentFormBusinessTest extends LuteceTestCase
{
    private final static int IDFORM1 = 1;
    private final static int IDFORM2 = 2;
    private final static String TITLE1 = "Title1";
    private final static String TITLE2 = "Title2";
    private final static int TIMESTART1 = 1;
    private final static int TIMESTART2 = 2;
    private final static int TIMEEND1 = 1;
    private final static int TIMEEND2 = 2;
    private final static int DURATIONAPPOINTMENTS1 = 1;
    private final static int DURATIONAPPOINTMENTS2 = 2;
    private final static boolean ISOPENMONDAY1 = false;
    private final static boolean ISOPENMONDAY2 = true;
    private final static boolean ISOPENTUESDAY1 = false;
    private final static boolean ISOPENTUESDAY2 = true;
    private final static boolean ISOPENWEDNESDAY1 = false;
    private final static boolean ISOPENWEDNESDAY2 = true;
    private final static boolean ISOPENTHURSDAY1 = false;
    private final static boolean ISOPENTHURSDAY2 = true;
    private final static boolean ISOPENFRIDAY1 = false;
    private final static boolean ISOPENFRIDAY2 = true;
    private final static boolean ISOPENSATURDAY1 = false;
    private final static boolean ISOPENSATURDAY2 = true;
    private final static boolean ISOPENSUNDAY1 = false;
    private final static boolean ISOPENSUNDAY2 = true;
    private final static Date DATESTARTVALIDITY1 = new Date( System.currentTimeMillis( ) );
    private final static Date DATESTARTVALIDITY2 = new Date( System.currentTimeMillis( ) + 100000l );
    private final static Date DATEENDVALIDITY1 = new Date( System.currentTimeMillis( ) );
    private final static Date DATEENDVALIDITY2 = new Date( System.currentTimeMillis( ) + 100000l );
    private final static boolean ISACTIVE1 = false;
    private final static boolean ISACTIVE2 = true;
    private final static boolean DISPOLAYTITLEFO1 = false;
    private final static boolean DISPOLAYTITLEFO2 = true;
    private final static int NBWEEKSTODISPLAY1 = 1;
    private final static int NBWEEKSTODISPLAY2 = 2;
    private final static int PEOPLEPERAPPOINTMENT1 = 1;
    private final static int PEOPLEPERAPPOINTMENT2 = 2;

    public void testBusiness(  )
    {
        // Initialize an object
        AppointmentForm appointmentForm = new AppointmentForm();
        appointmentForm.setIdForm( IDFORM1 );
        appointmentForm.setTitle( TITLE1 );
        appointmentForm.setTimeStart( TIMESTART1 );
        appointmentForm.setTimeEnd( TIMEEND1 );
        appointmentForm.setDurationAppointments( DURATIONAPPOINTMENTS1 );
        appointmentForm.setIsOpenMonday( ISOPENMONDAY1 );
        appointmentForm.setIsOpenTuesday( ISOPENTUESDAY1 );
        appointmentForm.setIsOpenWednesday( ISOPENWEDNESDAY1 );
        appointmentForm.setIsOpenThursday( ISOPENTHURSDAY1 );
        appointmentForm.setIsOpenFriday( ISOPENFRIDAY1 );
        appointmentForm.setIsOpenSaturday( ISOPENSATURDAY1 );
        appointmentForm.setIsOpenSunday( ISOPENSUNDAY1 );
        appointmentForm.setDateStartValidity( DATESTARTVALIDITY1 );
        appointmentForm.setDateEndValidity( DATEENDVALIDITY1 );
        appointmentForm.setIsActive( ISACTIVE1 );
        appointmentForm.setDisplayTitleFo( DISPOLAYTITLEFO1 );
        appointmentForm.setNbWeeksToDisplay( NBWEEKSTODISPLAY1 );
        appointmentForm.setPeoplePerAppointment( PEOPLEPERAPPOINTMENT1 );

        // Create test
        AppointmentFormHome.create( appointmentForm );
        AppointmentForm appointmentFormStored = AppointmentFormHome.findByPrimaryKey( appointmentForm.getIdForm() );
        assertEquals( appointmentFormStored.getIdForm() , appointmentForm.getIdForm() );
        assertEquals( appointmentFormStored.getTitle() , appointmentForm.getTitle() );
        assertEquals( appointmentFormStored.getTimeStart() , appointmentForm.getTimeStart() );
        assertEquals( appointmentFormStored.getTimeEnd() , appointmentForm.getTimeEnd() );
        assertEquals( appointmentFormStored.getDurationAppointments() , appointmentForm.getDurationAppointments() );
        assertEquals( appointmentFormStored.getIsOpenMonday() , appointmentForm.getIsOpenMonday() );
        assertEquals( appointmentFormStored.getIsOpenTuesday() , appointmentForm.getIsOpenTuesday() );
        assertEquals( appointmentFormStored.getIsOpenWednesday() , appointmentForm.getIsOpenWednesday() );
        assertEquals( appointmentFormStored.getIsOpenThursday() , appointmentForm.getIsOpenThursday() );
        assertEquals( appointmentFormStored.getIsOpenFriday() , appointmentForm.getIsOpenFriday() );
        assertEquals( appointmentFormStored.getIsOpenSaturday() , appointmentForm.getIsOpenSaturday() );
        assertEquals( appointmentFormStored.getIsOpenSunday() , appointmentForm.getIsOpenSunday() );
        assertEquals( appointmentFormStored.getDateStartValidity() , appointmentForm.getDateStartValidity() );
        assertEquals( appointmentFormStored.getDateEndValidity() , appointmentForm.getDateEndValidity() );
        assertEquals( appointmentFormStored.getIsActive() , appointmentForm.getIsActive() );
        assertEquals( appointmentFormStored.getDisplayTitleFo() , appointmentForm.getDisplayTitleFo() );
        assertEquals( appointmentFormStored.getNbWeeksToDisplay() , appointmentForm.getNbWeeksToDisplay() );
        assertEquals( appointmentFormStored.getPeoplePerAppointment() , appointmentForm.getPeoplePerAppointment() );

        // Update test
        appointmentForm.setIdForm( IDFORM2 );
        appointmentForm.setTitle( TITLE2 );
        appointmentForm.setTimeStart( TIMESTART2 );
        appointmentForm.setTimeEnd( TIMEEND2 );
        appointmentForm.setDurationAppointments( DURATIONAPPOINTMENTS2 );
        appointmentForm.setIsOpenMonday( ISOPENMONDAY2 );
        appointmentForm.setIsOpenTuesday( ISOPENTUESDAY2 );
        appointmentForm.setIsOpenWednesday( ISOPENWEDNESDAY2 );
        appointmentForm.setIsOpenThursday( ISOPENTHURSDAY2 );
        appointmentForm.setIsOpenFriday( ISOPENFRIDAY2 );
        appointmentForm.setIsOpenSaturday( ISOPENSATURDAY2 );
        appointmentForm.setIsOpenSunday( ISOPENSUNDAY2 );
        appointmentForm.setDateStartValidity( DATESTARTVALIDITY2 );
        appointmentForm.setDateEndValidity( DATEENDVALIDITY2 );
        appointmentForm.setIsActive( ISACTIVE2 );
        appointmentForm.setDisplayTitleFo( DISPOLAYTITLEFO2 );
        appointmentForm.setNbWeeksToDisplay( NBWEEKSTODISPLAY2 );
        appointmentForm.setPeoplePerAppointment( PEOPLEPERAPPOINTMENT2 );
        AppointmentFormHome.update( appointmentForm );
        appointmentFormStored = AppointmentFormHome.findByPrimaryKey( appointmentForm.getIdForm() );
        assertEquals( appointmentFormStored.getIdForm() , appointmentForm.getIdForm() );
        assertEquals( appointmentFormStored.getTitle() , appointmentForm.getTitle() );
        assertEquals( appointmentFormStored.getTimeStart() , appointmentForm.getTimeStart() );
        assertEquals( appointmentFormStored.getTimeEnd() , appointmentForm.getTimeEnd() );
        assertEquals( appointmentFormStored.getDurationAppointments() , appointmentForm.getDurationAppointments() );
        assertEquals( appointmentFormStored.getIsOpenMonday() , appointmentForm.getIsOpenMonday() );
        assertEquals( appointmentFormStored.getIsOpenTuesday() , appointmentForm.getIsOpenTuesday() );
        assertEquals( appointmentFormStored.getIsOpenWednesday() , appointmentForm.getIsOpenWednesday() );
        assertEquals( appointmentFormStored.getIsOpenThursday() , appointmentForm.getIsOpenThursday() );
        assertEquals( appointmentFormStored.getIsOpenFriday() , appointmentForm.getIsOpenFriday() );
        assertEquals( appointmentFormStored.getIsOpenSaturday() , appointmentForm.getIsOpenSaturday() );
        assertEquals( appointmentFormStored.getIsOpenSunday() , appointmentForm.getIsOpenSunday() );
        assertEquals( appointmentFormStored.getDateStartValidity() , appointmentForm.getDateStartValidity() );
        assertEquals( appointmentFormStored.getDateEndValidity() , appointmentForm.getDateEndValidity() );
        assertEquals( appointmentFormStored.getIsActive() , appointmentForm.getIsActive() );
        assertEquals( appointmentFormStored.getDisplayTitleFo() , appointmentForm.getDisplayTitleFo() );
        assertEquals( appointmentFormStored.getNbWeeksToDisplay() , appointmentForm.getNbWeeksToDisplay() );
        assertEquals( appointmentFormStored.getPeoplePerAppointment() , appointmentForm.getPeoplePerAppointment() );

        // List test
        AppointmentFormHome.getAppointmentFormsList();

        // Delete test
        AppointmentFormHome.remove( appointmentForm.getIdForm() );
        appointmentFormStored = AppointmentFormHome.findByPrimaryKey( appointmentForm.getIdForm() );
        assertNull( appointmentFormStored );
        
    }

}