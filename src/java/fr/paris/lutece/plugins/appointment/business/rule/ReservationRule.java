package fr.paris.lutece.plugins.appointment.business.rule;

import java.io.Serializable;

import javax.validation.constraints.Min;

import fr.paris.lutece.plugins.appointment.business.AbstractDateConversion;

/**
 * Business class of the rules of the reservation
 * 
 * @author Laurent Payen
 *
 */
public class ReservationRule extends AbstractDateConversion implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -5154752950203822668L;

    /**
     * Id of the reservation rule.
     */
    private int _nIdReservationRule;

    /**
     * Maximum capacity for a slot
     */
    @Min( value = 1, message = "#i18n{portal.validation.message.notEmpty}" )
    private int _nMaxCapacityPerSlot = 1;

    /**
     * Maximum number of people authorized for an appointment
     */
    @Min( value = 1, message = "#i18n{portal.validation.message.notEmpty}" )
    private int _nMaxPeoplePerAppointment = 1;

    /**
     * The Form Id the Reservation Rule belongs to (foreign key)
     */
    private int _nIdForm;

    /**
     * Get the id of the rule of the reservation
     * 
     * @return the id of the rule of the reservation
     */
    public int getIdReservationRule( )
    {
        return _nIdReservationRule;
    }

    /**
     * Set the id of the rule of the reservation
     * 
     * @param nIdReservationRule
     *            the id to set
     */
    public void setIdReservationRule( int nIdReservationRule )
    {
        this._nIdReservationRule = nIdReservationRule;
    }

    /**
     * Get the maximum capacity for a slot
     * 
     * @return the maximum capacity for a slot
     */
    public int getMaxCapacityPerSlot( )
    {
        return _nMaxCapacityPerSlot;
    }

    /**
     * Set the maximum capacity for a slot
     * 
     * @param nMaxCapacityPerSlot
     *            the maximum capacity for a slot
     */
    public void setMaxCapacityPerSlot( int nMaxCapacityPerSlot )
    {
        this._nMaxCapacityPerSlot = nMaxCapacityPerSlot;
    }

    /**
     * Get the maximum number of people authorized for an appointment
     * 
     * @return the maximum number of people authorized for an appointment
     */
    public int getMaxPeoplePerAppointment( )
    {
        return _nMaxPeoplePerAppointment;
    }

    /**
     * Set the maximum number of people authorized for an appointment
     * 
     * @param nMaxPeoplePerAppointment
     *            the maximum of people to set
     */
    public void setMaxPeoplePerAppointment( int nMaxPeoplePerAppointment )
    {
        this._nMaxPeoplePerAppointment = nMaxPeoplePerAppointment;
    }

    /**
     * Get the Form Id the Reservation Rule belongs to
     * 
     * @return the Form Id
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the Form Id the Reservation Rule belongs to
     * 
     * @param nIdForm
     *            the Form Id tp set
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

}
