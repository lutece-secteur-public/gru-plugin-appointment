/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
import fr.paris.lutece.plugins.appointment.service.listeners.FormListenerManager;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.util.ReferenceList;

/**
 * Service class for the reservation rule
 * 
 * @author Laurent Payen
 *
 */
public final class ReservationRuleService
{

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
    public static ReservationRule createReservationRule( AppointmentFormDTO appointmentForm, int nIdForm, LocalDate dateOfApply )
    {
        ReservationRule reservationRule = new ReservationRule( );
        fillInReservationRule( reservationRule, appointmentForm, nIdForm, dateOfApply );
        ReservationRuleHome.create( reservationRule );
        return reservationRule;
    }

    /**
     * Delete a reservation rule by its id
     * 
     * @param reservationRule
     *            the reservation rule to delete
     */
    public static void removeReservationRule( ReservationRule reservationRule )
    {
        FormListenerManager.notifyListenersFormChange( reservationRule.getIdForm( ) );
        ReservationRuleHome.delete( reservationRule.getIdReservationRule( ) );
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
     * @param dateOfApply
     *            the date of the update
     * @return the reservation rule object updated
     */
    public static ReservationRule updateReservationRule( AppointmentFormDTO appointmentForm, int nIdForm, LocalDate dateOfApply )
    {
        ReservationRule reservationRule = ReservationRuleService.findReservationRuleByIdFormAndDateOfApply( nIdForm, dateOfApply );
        if ( reservationRule == null )
        {
            reservationRule = createReservationRule( appointmentForm, nIdForm, dateOfApply );
        }
        else
        {
            fillInReservationRule( reservationRule, appointmentForm, nIdForm, dateOfApply );
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
     * @param dateOfApply
     *            the date of the reservation rule
     */
    public static void fillInReservationRule( ReservationRule reservationRule, AppointmentFormDTO appointmentForm, int nIdForm, LocalDate dateOfApply )
    {
        reservationRule.setDateOfApply( dateOfApply );
        reservationRule.setMaxCapacityPerSlot( appointmentForm.getMaxCapacityPerSlot( ) );
        reservationRule.setMaxPeoplePerAppointment( appointmentForm.getMaxPeoplePerAppointment( ) );
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
        // Get all the reservation rules
        List<ReservationRule> listReservationRule = ReservationRuleHome.findByIdForm( nIdForm );
        List<LocalDate> listDate = new ArrayList<>( );
        for ( ReservationRule reservationRule : listReservationRule )
        {
            listDate.add( reservationRule.getDateOfApply( ) );
        }
        // Try to get the closest date in past of the dateOfApply
        LocalDate closestDate = Utilities.getClosestDateInPast( listDate, dateOfApply );
        ReservationRule reservationRule = null;
        // If there is no closest date in past
        if ( closestDate == null )
        {
            // if the list of reservation rules is not empty
            if ( CollectionUtils.isNotEmpty( listReservationRule ) )
            {
                // Get the next week definition in future
                reservationRule = listReservationRule.stream( ).min( ( w1, w2 ) -> w1.getDateOfApply( ).compareTo( w2.getDateOfApply( ) ) ).get( );
            }
        }
        else
        {
            // The closest date in past is not null
            if ( CollectionUtils.isNotEmpty( listReservationRule ) )
            {
                // Get the corresponding reservation rule
                reservationRule = listReservationRule.stream( ).filter( x -> closestDate.isEqual( x.getDateOfApply( ) ) ).findAny( ).orElse( null );
            }
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
        ReservationRule reservationRule = ReservationRuleHome.findByPrimaryKey( nIdReservationRule );
        return reservationRule;
    }

    /**
     * Build a reference list of all the dates of all the reservation rules of a form
     * 
     * @param nIdForm
     *            the form Id
     * @return the reference list (id reservation rule / date of apply of the reservation rule)
     */
    public static ReferenceList findAllDateOfReservationRule( int nIdForm )
    {
        ReferenceList listDate = new ReferenceList( );
        List<ReservationRule> listReservationRule = ReservationRuleHome.findByIdForm( nIdForm );
        for ( ReservationRule reservationRule : listReservationRule )
        {
            listDate.addItem( reservationRule.getIdReservationRule( ), reservationRule.getDateOfApply( ).format( Utilities.getDateFormatter( ) ) );
        }
        return listDate;
    }

    /**
     * Find all the reservation rule of a form
     * 
     * @param nIdForm
     *            the form Id
     * @return an HashMap with the date of apply in key and the reservation rule in value
     */
    public static HashMap<LocalDate, ReservationRule> findAllReservationRule( int nIdForm )
    {
        HashMap<LocalDate, ReservationRule> mapReservationRule = new HashMap<>( );
        List<ReservationRule> listReservationRule = ReservationRuleHome.findByIdForm( nIdForm );
        for ( ReservationRule reservationRule : listReservationRule )
        {
            mapReservationRule.put( reservationRule.getDateOfApply( ), reservationRule );
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
        return ReservationRuleHome.findByIdForm( nIdForm );
    }

}
