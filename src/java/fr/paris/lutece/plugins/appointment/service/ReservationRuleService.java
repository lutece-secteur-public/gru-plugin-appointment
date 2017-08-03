package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRuleHome;
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
    public static ReservationRule createReservationRule( AppointmentForm appointmentForm, int nIdForm, LocalDate dateOfApply )
    {
        ReservationRule reservationRule = new ReservationRule( );
        fillInReservationRule( reservationRule, appointmentForm, nIdForm, dateOfApply );
        ReservationRuleHome.create( reservationRule );
        return reservationRule;
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
    public static ReservationRule updateReservationRule( AppointmentForm appointmentForm, int nIdForm, LocalDate dateOfApply )
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
    public static void fillInReservationRule( ReservationRule reservationRule, AppointmentForm appointmentForm, int nIdForm, LocalDate dateOfApply )
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
        ReservationRule reservationRule = ReservationRuleHome.findByIdFormAndClosestToDateOfApply( nIdForm, dateOfApply );
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
            listDate.addItem( reservationRule.getIdReservationRule( ), reservationRule.getDateOfApply( ).format( Utilities.getFormatter( ) ) );
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
