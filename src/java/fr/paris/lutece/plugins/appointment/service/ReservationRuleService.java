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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlotHome;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDayHome;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.plugins.appointment.service.listeners.WeekDefinitionManagerListener;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * Service class for the reservation rule
 * 
 * @author Laurent Payen
 *
 */
public final class ReservationRuleService
{
    private static final String CONST_COPY_OF_WEEK = "Copy ";


    /**
     * Private constructor - this class does not need to be instantiated
     */
    private ReservationRuleService( )
    {
    }

    /**
     * Create in database a reservation rule object from an appointmentForm DTO
     * 
     * @param appointmentForm
     *            the appointmentForm DTO
     * @param nIdForm
     *            the form Id
     * @param dateOfApply
     *            the date of the reservation rule
     * @return the Reservation Rule object created
     */
    public static ReservationRule createReservationRule( AppointmentFormDTO appointmentForm, int nIdForm )
    {
        ReservationRule reservationRule = new ReservationRule( );
        fillInReservationRule( reservationRule, appointmentForm, nIdForm );
        ReservationRuleHome.create( reservationRule );
        return reservationRule;
    }
    
    /**
     * Create in database a reservation rule object from an appointmentForm DTO
     * 
     * @param appointmentForm
     *            the appointmentForm DTO
    
     * @return the Reservation Rule id
     */
    public static int createAdvancedParameters( AppointmentFormDTO appointmentForm )
    {
       
        int nIdForm = appointmentForm.getIdForm( );
        ReservationRule reservationRule = createReservationRule( appointmentForm, nIdForm );
        int nMaxCapacity = reservationRule.getMaxCapacityPerSlot( );
        LocalTime startingTime = LocalTime.parse( appointmentForm.getTimeStart( ) );
        LocalTime endingTime = LocalTime.parse( appointmentForm.getTimeEnd( ) );
        int nDuration = appointmentForm.getDurationAppointments( );
        for ( DayOfWeek dayOfWeek : WorkingDayService.getOpenDays( appointmentForm ) )
        {
            WorkingDayService.generateWorkingDayAndListTimeSlot( reservationRule.getIdReservationRule( ), dayOfWeek, startingTime, endingTime, nDuration, nMaxCapacity );
        }
        return reservationRule.getIdReservationRule( );
    }
    /**
     * Make a copy of typical week, with all its values
     * 
     * @param nIdReservationRule
     *            the reservationRule Id to copy
     * @return the id of the reservation rule created
     */
    
    public static int copyReservationRule( int nIdReservationRule )
    {       
    	ReservationRule reservationRule= findReservationRuleById( nIdReservationRule ); 
    	if( reservationRule != null ) {
    		
    		reservationRule.setName( CONST_COPY_OF_WEEK + reservationRule.getName() );
            TransactionManager.beginTransaction( AppointmentPlugin.getPlugin( ) );
	        try {
	            ReservationRuleHome.create( reservationRule );	
		        for ( WorkingDay workingDay : reservationRule.getListWorkingDay( ) )
		        {
		        	workingDay.setIdReservationRule( reservationRule.getIdReservationRule( ) );
		        	WorkingDayHome.create( workingDay );
		        	for( TimeSlot timeSlotTemp: workingDay.getListTimeSlot( )) {
		        		
		        		timeSlotTemp.setIdWorkingDay(workingDay.getIdWorkingDay( ));
		        		TimeSlotHome.create( timeSlotTemp );
		        	}
		        }
		        TransactionManager.commitTransaction( AppointmentPlugin.getPlugin( ) );
		        return reservationRule.getIdReservationRule( );
	       } catch( Exception e )
	       {
	            TransactionManager.rollBack( AppointmentPlugin.getPlugin( ) );
	            AppLogService.error( "Error copy typical week" + e.getMessage( ), e );
	            return 0;
	       }      
    	}
    	
    	return 0;
    }
    /**
     * Update a form with the new values of an appointmentForm DTO Advanced Parameters (with a date of application) --> new Typical Week
     * 
     * @param appointmentForm
     *            the appointmentForm DTO
     */
    public static void updateAdvancedParameters( AppointmentFormDTO appointmentForm  )
    {
        int nIdForm = appointmentForm.getIdForm( );
        ReservationRule reservationRule = updateReservationRule( appointmentForm, nIdForm );
        int nMaxCapacity = reservationRule.getMaxCapacityPerSlot( );
        List<WorkingDay> listWorkingDay = WorkingDayService.findListWorkingDayByWeekDefinitionRule( reservationRule.getIdReservationRule( ) );

        if ( CollectionUtils.isNotEmpty( listWorkingDay ) )
        {
        	for ( WorkingDay workingDay : listWorkingDay )
            {
            	TimeSlotHome.deleteByIdWorkingDay(workingDay.getIdWorkingDay( ));
            }
        	WorkingDayHome.deleteByIdReservationRule(reservationRule.getIdReservationRule( ));
        }
        LocalTime startingHour = LocalTime.parse( appointmentForm.getTimeStart( ) );
        LocalTime endingHour = LocalTime.parse( appointmentForm.getTimeEnd( ) );
        int nDuration = appointmentForm.getDurationAppointments( );
        for ( DayOfWeek dayOfWeek : WorkingDayService.getOpenDays( appointmentForm ) )
        {
            WorkingDayService.generateWorkingDayAndListTimeSlot( reservationRule.getIdReservationRule( ), dayOfWeek, startingHour, endingHour, nDuration, nMaxCapacity );
        }
        if(CollectionUtils.isNotEmpty( WeekDefinitionService.findByReservationRule( appointmentForm.getIdReservationRule( )))) {
        	
        	WeekDefinitionManagerListener.notifyListenersListWeekDefinitionChanged( appointmentForm.getIdForm( ) );
        }
    }

    /**
     * Delete a reservation rule by its id
     * 
     * @param reservationRule
     *            the reservation rule to delete
     */
    public static void removeReservationRule( int nIdReservationRule )
    {
        ReservationRule rule= findReservationRuleById( nIdReservationRule );
        for( WorkingDay day: rule.getListWorkingDay( ) ) {
        	
            TimeSlotHome.deleteByIdWorkingDay( day.getIdWorkingDay( ) );
            WorkingDayHome.delete(day.getIdWorkingDay( ));

        }
        ReservationRuleHome.delete( rule.getIdReservationRule( ) );
        
    }

    /**
     * save a reservation rule
     * 
     * @param reservationRule
     *            the reservation rule to save
     */
    public static void saveReservationRule( ReservationRule reservationRule )
    {
        ReservationRuleHome.create( reservationRule );
    }

    /**
     * Update in database a reservation rule with the values of an appointmentForm DTO
     * 
     * @param appointmentForm
     *            the appointmentForm DTO
     * @param nIdForm
     *            the form Id
     * @return the reservation rule object updated
     */
    public static ReservationRule updateReservationRule( AppointmentFormDTO appointmentForm, int nIdForm )
    {
        ReservationRule reservationRule = ReservationRuleService.findReservationRuleById( appointmentForm.getIdReservationRule( )  );
        if ( reservationRule == null )
        {
            reservationRule = createReservationRule( appointmentForm, nIdForm );
        }
        else
        {
            fillInReservationRule( reservationRule, appointmentForm, nIdForm );
            ReservationRuleHome.update( reservationRule );
        }
        return reservationRule;
    }

    /**
     * Fill the reservation rule object with the corresponding values of an appointmentForm DTO
     * 
     * @param reservationRule
     *            the reservation rule object to fill in
     * @param appointmentForm
     *            the appointmentForm DTO
     * @param nIdForm
     *            the form Id
     */
    public static void fillInReservationRule( ReservationRule reservationRule, AppointmentFormDTO appointmentForm, int nIdForm )
    {
        reservationRule.setMaxCapacityPerSlot( appointmentForm.getMaxCapacityPerSlot( ) );
        reservationRule.setMaxPeoplePerAppointment( appointmentForm.getMaxPeoplePerAppointment( ) );
        reservationRule.setName( appointmentForm.getName( ));
        reservationRule.setDescriptionRule( appointmentForm.getDescriptionRule( ));
        reservationRule.setColor( appointmentForm.getColor( ));
        reservationRule.setIdForm( nIdForm );
    }

    /**
     * Find in database a reservation rule of a form closest to a date
     * 
     * @param nIdForm
     *            the form Id
     * @param dateOfApply
     *            the date
     * @return the reservation rule to apply at this date
     */
   public static ReservationRule findReservationRuleByIdFormAndClosestToDateOfApply( int nIdForm, LocalDate dateOfApply )
    {
	   ReservationRule reservationRule= ReservationRuleHome.findReservationRuleByIdFormAndClosestToDateOfApply( nIdForm, dateOfApply);
      if( reservationRule != null ) {
    	   
   	   	reservationRule.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinitionRule( reservationRule.getIdReservationRule( ) ) );
     }
	   return reservationRule;
    }

    /**
     * Find the reservation rule of a form on a specific date
     * 
     * @param nIdForm
     *            the form Id
     * @param dateOfApply
     *            the date of the reservation rule
     * @return the reservation rule object
     */
    public static ReservationRule findReservationRuleByIdFormAndDateOfApply( int nIdForm, LocalDate dateOfApply )
    {
    	
    	ReservationRule reservationRule = ReservationRuleHome.findByIdFormAndDateOfApply( nIdForm, dateOfApply );  
    	if( reservationRule != null ) {
    		   
    		reservationRule.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinitionRule( reservationRule.getIdReservationRule( ) ) );
    	}
        return reservationRule;
    }

    /**
     * Find a reservation rule with its primary key
     * 
     * @param nIdReservationRule
     *            the reservation rule Id
     * @return the Reservation Rule Object
     */
    public static ReservationRule findReservationRuleById( int nIdReservationRule )
    {
         ReservationRule reservationRule= ReservationRuleHome.findByPrimaryKey( nIdReservationRule );
         if( reservationRule != null ) {
  		   
     		reservationRule.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinitionRule( reservationRule.getIdReservationRule( ) ) );
     	 }
         return reservationRule;
    }

    /**
     * Build a reference list of all reservation rules of a form
     * 
     * @param nIdForm
     *            the form Id
     * @return the reference list (id reservation rule / date of apply of the reservation rule)
     */
    public static ReferenceList findAllReservationRule( int nIdForm )
    {
        ReferenceList listRule = new ReferenceList( );
        List<ReservationRule> listReservationRule = ReservationRuleHome.findByIdForm( nIdForm );
        for ( ReservationRule reservationRule : listReservationRule )
        {
        	listRule.addItem( reservationRule.getIdReservationRule( ), reservationRule.getName( ) );
        }
        return listRule;
    }
    /**
     * Find all the reservation rule of a form
     * 
     * @param nIdForm
     *            the form Id
     * @return an HashMap with the date of apply in key and the reservation rule in value
     */
    public static Map<WeekDefinition, ReservationRule> findAllReservationRule( int nIdForm,  Collection<WeekDefinition> listWeekDefinition )
    {
    	
        Map<WeekDefinition, ReservationRule> mapReservationRule = new HashMap<>( );
        List<ReservationRule> listReservationRule= ReservationRuleHome.findByIdForm( nIdForm );
        for ( WeekDefinition weekDefinition : listWeekDefinition )
        {
        	 	ReservationRule reservationRule = listReservationRule.stream().filter( p ->p.getIdReservationRule() == weekDefinition.getIdReservationRule( )).findAny().orElse( null );        	 	
        	    reservationRule.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinitionRule( reservationRule.getIdReservationRule( ) ) );
        		mapReservationRule.put(weekDefinition , reservationRule);
        	
        }
        return mapReservationRule;
    }

    /**
     * Returns a list of the reservation rules of a form
     * 
     * @param nIdForm
     *            the form id
     * @return a list of reservation rules of the form
     */
    public static List<ReservationRule> findListReservationRule( int nIdForm )
    {
    	
    	List<ReservationRule> listReservationRule= ReservationRuleHome.findByIdForm( nIdForm );
        for ( ReservationRule reservationRule : listReservationRule )
        {
        	reservationRule.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinitionRule( reservationRule.getIdReservationRule( ) ) );
        }
        return listReservationRule;
    }
    /**
     * Returns a list of the reservation rules of a form
     * 
     * @param nIdForm
     *            the form id
     * @param listWeekDefinition
     * 			  the week definition list
     * @return a list of reservation rules of the form
     */
    public static List<ReservationRule> findListReservationRule( int nIdForm, Collection<WeekDefinition> listWeekDefinition )
    {
    	
    	List<ReservationRule> listReservationRule= new ArrayList<>( );
        for ( ReservationRule reservationRule : ReservationRuleHome.findByIdForm( nIdForm ) )
        {
        	if( listWeekDefinition.stream().anyMatch( p -> p.getIdReservationRule() == reservationRule.getIdReservationRule( ))) {
        		
        		reservationRule.setListWorkingDay( WorkingDayService.findListWorkingDayByWeekDefinitionRule( reservationRule.getIdReservationRule( ) ) );
        		listReservationRule.add(reservationRule);
        	}
        }
        return listReservationRule;
    }
    
    

}
