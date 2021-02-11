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
import java.time.LocalTime;
import java.util.List;

import com.sun.jersey.json.impl.provider.entity.JSONArrayProvider;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

public class WeekDefinitionServiceTest extends LuteceTestCase
{
    public final static String TIME_1 = "18:00";
    public final static String TIME_2 = "19:00";
    public final static String TIME_3 = "20:00";
    public final static String TIME_4 = "09:00";
    public final static String TIME_5 = "09:30";
    public final static String TIME_6 = "10:00";
    public final static String TIME_7 = "19:30";
    private AppointmentFormDTO appointmentForm;
    /**
     * Find a week definition of a form and a date of apply
     */
    public void testFindWeekDefinitionByIdFormAndClosestToDateOfApply( )
    {
        //Build form
        this.appointmentForm.setTimeEnd( TIME_1 );
        int nIdForm = FormService.createAppointmentForm( this.appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeEnd( TIME_3 );
        LocalDate dateOfModification = Constants.DATE_7;
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );
        LocalDate dateOfApply = Constants.DATE_6;
        WeekDefinition foundWeekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, dateOfApply );
        assertEquals( dateOfModification, foundWeekDefinition.getDateOfApply( ) );

        AppointmentFormDTO appointmentForm3 = FormServiceTest.buildAppointmentForm( );
        appointmentForm3.setIdForm( nIdForm );
        appointmentForm3.setTimeEnd( TIME_2 );
        LocalDate dateOfModification2 = Constants.DATE_8;
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification2 );

        foundWeekDefinition = WeekDefinitionService.findWeekDefinitionByIdFormAndClosestToDateOfApply( nIdForm, dateOfApply );
        assertEquals( dateOfModification2, foundWeekDefinition.getDateOfApply( ) );

    }

    /**
     * Return, if it exists, the next week definition after a given date
     */
    public void testFindNextWeekDefinition( )
    {
        //Build form
        this.appointmentForm.setTimeEnd( TIME_1 );
        int nIdForm = FormService.createAppointmentForm( this.appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeEnd( TIME_3 );
        LocalDate dateOfModification = Constants.DATE_7;
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );
        LocalDate givenDate = Constants.DATE_22;

        WeekDefinition foundWeekDefinition = WeekDefinitionService.findNextWeekDefinition( nIdForm, givenDate );

        assertEquals( dateOfModification, foundWeekDefinition.getDateOfApply( ) );

        LocalDate givenDate2 = Constants.DATE_8;
        foundWeekDefinition = WeekDefinitionService.findNextWeekDefinition( nIdForm, givenDate2 );
        assertNull( foundWeekDefinition );
    }

    /**
     * Return the min starting time of a list of week definitions
     */
    public void testGetMinStartingTimeOfAListOfWeekDefinition( )
    {
        //Build form
        this.appointmentForm.setTimeStart( TIME_4 );
        int nIdForm = FormService.createAppointmentForm( this.appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeStart( TIME_6 );
        LocalDate dateOfModification = Constants.DATE_7;
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );

        AppointmentFormDTO appointmentForm3 = FormServiceTest.buildAppointmentForm( );
        appointmentForm3.setIdForm( nIdForm );
        appointmentForm3.setTimeStart( TIME_5 );
        LocalDate dateOfModification2 = Constants.DATE_8;
        FormService.updateAdvancedParameters( appointmentForm3, dateOfModification2 );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        assertEquals( Constants.STARTING_TIME_3, WeekDefinitionService.getMinStartingTimeOfAListOfWeekDefinition( listWeekDefinition ) );

    }

    /**
     * Return the min starting time of a week definition
     */
    public void testGetMinStartingTimeOfAWeekDefinition( )
    {
        // Build the form
        this.appointmentForm.setTimeStart( TIME_4 );
        int nIdForm = FormService.createAppointmentForm( this.appointmentForm );
        WeekDefinition weekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm ).get( 0 );
        assertEquals(Constants.STARTING_TIME_3, WeekDefinitionService.getMinStartingTimeOfAWeekDefinition( weekDefinition ) );
    }

    /**
     * Return the max ending time of a list of week definitions
     */
    public void testGetMaxEndingTimeOfAListOfWeekDefinition( )
    {

        // Build the form
        this.appointmentForm.setTimeEnd( TIME_1 );
        int nIdForm = FormService.createAppointmentForm( this.appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeEnd( TIME_2 );
        LocalDate dateOfModification = Constants.DATE_7;
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );

        AppointmentFormDTO appointmentForm3 = FormServiceTest.buildAppointmentForm( );
        appointmentForm3.setIdForm( nIdForm );
        appointmentForm3.setTimeEnd( TIME_7 );
        LocalDate dateOfModification2 = Constants.DATE_8;
        FormService.updateAdvancedParameters( appointmentForm3, dateOfModification2 );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        assertEquals( Constants.TIME_7, WeekDefinitionService.getMaxEndingTimeOfAListOfWeekDefinition( listWeekDefinition ) );


    }

    /**
     * Get the max ending time of a week definition
     */
    public void testGetMaxEndingTimeOfAWeekDefinition( )
    {
        // Build the form
        this.appointmentForm.setTimeEnd( TIME_2 );
        int nIdForm = FormService.createAppointmentForm( this.appointmentForm );
        WeekDefinition weekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm ).get( 0 );
        assertEquals( Constants.TIME_6, WeekDefinitionService.getMaxEndingTimeOfAWeekDefinition( weekDefinition ) );
    }

    /**
     * Get the min duration of a time slot of a week definition
     */
    public void testGetMinDurationTimeSlotOfAListOfWeekDefinition( )
    {
        // Build the form
        this.appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( this.appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setDurationAppointments( 20 );
        LocalDate dateOfModification = Constants.DATE_7;
        FormService.updateAdvancedParameters( appointmentForm2, dateOfModification );

        AppointmentFormDTO appointmentForm3 = FormServiceTest.buildAppointmentForm( );
        appointmentForm3.setIdForm( nIdForm );
        appointmentForm3.setDurationAppointments( 10 );
        LocalDate dateOfModification2 = Constants.DATE_8;
        FormService.updateAdvancedParameters( appointmentForm3, dateOfModification2 );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );

        assertEquals( 10, WeekDefinitionService.getMinDurationTimeSlotOfAListOfWeekDefinition( listWeekDefinition ) );

    }

    /**
     * Get the set of the open days of all the week definitons
     */
    public void testGetOpenDaysOfWeek( )
    {
        // Build the form
        this.appointmentForm.setIsOpenMonday( Boolean.TRUE );
        this.appointmentForm.setIsOpenTuesday( Boolean.TRUE );
        this.appointmentForm.setIsOpenWednesday( Boolean.TRUE );
        this.appointmentForm.setIsOpenThursday( Boolean.TRUE );
        this.appointmentForm.setIsOpenFriday( Boolean.TRUE );
        this.appointmentForm.setIsOpenSaturday( Boolean.FALSE );
        this.appointmentForm.setIsOpenSunday( Boolean.FALSE );
        int nIdForm = FormService.createAppointmentForm( this.appointmentForm );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );

        assertEquals( 5, WeekDefinitionService.getOpenDaysOfWeek( listWeekDefinition ).size( ) );

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.appointmentForm = FormServiceTest.buildAppointmentForm( );
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //delete all the forms left over from tests
        for (Form f : FormService.findAllForms()) {
            FormService.removeForm(f.getIdForm());
        }
        assertEquals(0, FormService.findAllForms().size());
        this.appointmentForm = null;
    }

}