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
package fr.paris.lutece.plugins.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDayHome;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.test.LuteceTestCase;

public class WorkingDayServiceTest extends LuteceTestCase
{

    /**
     * Get the max ending time of a list of working days
     */
    @SuppressWarnings("null")
	public void testGetMaxEndingTimeOfAListOfWorkingDay( )
    {
    	List<WorkingDay> listWorkingDay = new ArrayList<>();
    	
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setTimeEnd( "18:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeEnd( "20:00" );
        appointmentForm2.setName("appointment_form");
        LocalDate dateOfModification = LocalDate.parse( "2018-06-20" );
        FormService.updateGlobalParameters( appointmentForm2 );

        List<WeekDefinition> listWeekDefinition = WeekDefinitionService.findListWeekDefinition( nIdForm );
        for(WeekDefinition weekDefinition : listWeekDefinition ) {
        	listWorkingDay.addAll(WorkingDayHome.findByIdReservationRule( weekDefinition.getIdWeekDefinition( ) ) );
        }
        

        assertEquals( LocalTime.parse( "20:00" ), WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay( listWorkingDay ) );
        FormServiceTest.cleanForm( nIdForm );
    }

    /**
     * Get the max ending time of a working day
     */
    public void testGetMaxEndingTimeOfAWorkingDay( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setTimeEnd( "18:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        List<ReservationRule> listReservationRule = ReservationRuleHome.findByIdForm( nIdForm );
        List<WorkingDay> listWorkingDay = new ArrayList<>( );
        for ( ReservationRule appointmentFormElement : listReservationRule )
        {
            listWorkingDay.addAll( WorkingDayService.findListWorkingDayByWeekDefinitionRule( appointmentFormElement.getIdReservationRule( ) ) );
        }

        WorkingDay workingDayMonday = listWorkingDay.stream( ).filter( w -> w.getDayOfWeek( ) == DayOfWeek.MONDAY.getValue( ) ).findFirst( ).get( );

        assertEquals( LocalTime.parse( "18:00" ), WorkingDayService.getMaxEndingTimeOfAWorkingDay( workingDayMonday ) );
        FormServiceTest.cleanForm( nIdForm );
    }

    /**
     * Get the min duration slot of a list of working days
     */
    public void testGetMinDurationTimeSlotOfAListOfWorkingDay( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setDurationAppointments( 30 );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setName("appointment_form");
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setDurationAppointments( 10 );
        LocalDate dateOfModification = LocalDate.parse( "2028-06-20" );
        FormService.updateGlobalParameters( appointmentForm2 );

        List<ReservationRule> listReservationRule = ReservationRuleHome.findByIdForm( nIdForm );
        List<WorkingDay> listWorkingDay = new ArrayList<>( );
        for ( ReservationRule appointmentFormElement : listReservationRule )
        {
            listWorkingDay.addAll( WorkingDayService.findListWorkingDayByWeekDefinitionRule( appointmentFormElement.getIdReservationRule( ) ) );
        }

        assertEquals( 10, WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay( listWorkingDay ) );
        FormServiceTest.cleanForm( nIdForm );
    }

    /**
     * Get the min duration slot of a working day
     */
    public void testGetMinDurationTimeSlotOfAWorkingDay( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setDurationAppointments( 30 );
        appointmentForm.setDescriptionRule( "appointment description");
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        List<ReservationRule> listReservationRule = ReservationRuleHome.findByIdForm( nIdForm );
        List<WorkingDay> listWorkingDay = new ArrayList<>( );
        for ( ReservationRule appointmentFormElement : listReservationRule )
        {
            listWorkingDay.addAll( WorkingDayService.findListWorkingDayByWeekDefinitionRule( appointmentFormElement.getIdReservationRule( ) ) );
        }
        WorkingDay workingDayMonday = listWorkingDay.stream( ).filter( w -> w.getDayOfWeek( ) == DayOfWeek.MONDAY.getValue( ) ).findFirst( ).get( );

        assertEquals( 30, WorkingDayService.getMinDurationTimeSlotOfAWorkingDay( workingDayMonday ) );
        FormServiceTest.cleanForm( nIdForm );
    }

    /**
     * Get the min starting time of a list of working days
     */
    public void testGetMinStartingTimeOfAListOfWorkingDay( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setTimeStart( "09:00" );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        AppointmentFormDTO appointmentForm2 = FormServiceTest.buildAppointmentForm( );
        appointmentForm2.setName("appointment_form");
        appointmentForm2.setIdForm( nIdForm );
        appointmentForm2.setTimeStart( "10:00" );
        LocalDate dateOfModification = LocalDate.parse( "2018-06-20" );
        FormService.updateGlobalParameters( appointmentForm2 );

        List<ReservationRule> listReservationRule = ReservationRuleHome.findByIdForm( nIdForm );
        List<WorkingDay> listWorkingDay = new ArrayList<>( );
        for ( ReservationRule appointmentFormElement : listReservationRule )
        {
            listWorkingDay.addAll( WorkingDayService.findListWorkingDayByWeekDefinitionRule( appointmentFormElement.getIdReservationRule( ) ) );
        }

        assertEquals( LocalTime.parse( "09:00" ), WorkingDayService.getMinStartingTimeOfAListOfWorkingDay( listWorkingDay ) );
        FormServiceTest.cleanForm( nIdForm );
    }

    /**
     * Get the min starting time of a working day
     */
    public void testGetMinStartingTimeOfAWorkingDay( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setTimeStart( "09:00" );
        appointmentForm.setName("appointment_form");
        int nIdForm = FormService.createAppointmentForm( appointmentForm );
        List<ReservationRule> listReservationRule = ReservationRuleHome.findByIdForm( nIdForm );
        List<WorkingDay> listWorkingDay = new ArrayList<>( );
        for ( ReservationRule appointmentFormElement : listReservationRule )
        {
            listWorkingDay.addAll( WorkingDayService.findListWorkingDayByWeekDefinitionRule( appointmentFormElement.getIdReservationRule( ) ) );
        }

        WorkingDay workingDayMonday = listWorkingDay.stream( ).filter( w -> w.getDayOfWeek( ) == DayOfWeek.MONDAY.getValue( ) ).findFirst( ).get( );

        assertEquals( LocalTime.parse( "09:00" ), WorkingDayService.getMinStartingTimeOfAWorkingDay( workingDayMonday ) );
        FormServiceTest.cleanForm( nIdForm );
    }

    /**
     * Get the open days of an appointmentForm DTO
     */
    public void testGetOpenDays( )
    {
        // Build the form
        AppointmentFormDTO appointmentForm = FormServiceTest.buildAppointmentForm( );
        appointmentForm.setName("appointment_form");
        appointmentForm.setIsOpenMonday( Boolean.TRUE );
        appointmentForm.setIsOpenTuesday( Boolean.TRUE );
        appointmentForm.setIsOpenWednesday( Boolean.TRUE );
        appointmentForm.setIsOpenThursday( Boolean.TRUE );
        appointmentForm.setIsOpenFriday( Boolean.TRUE );
        appointmentForm.setIsOpenSaturday( Boolean.FALSE );
        appointmentForm.setIsOpenSunday( Boolean.FALSE );
        int nIdForm = FormService.createAppointmentForm( appointmentForm );

        assertEquals( 5, WorkingDayService.getOpenDays( appointmentForm ).size( ) );
        FormServiceTest.cleanForm( nIdForm );
    }
}
