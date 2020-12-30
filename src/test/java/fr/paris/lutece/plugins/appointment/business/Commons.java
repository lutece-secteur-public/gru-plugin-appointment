package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;

public class Commons {
	/**
     * Build a Reservation Rule
     * 
     * @return a Reservation Rule
     */
    public static ReservationRule buildReservationRule( int nIdForm )
    {
    	ReservationRule reservationRule = new ReservationRule( );
    	reservationRule.setName( "ReservationRule" );
    	reservationRule.setDescriptionRule( "A built reservation Rule" );
    	reservationRule.setIdForm( nIdForm );
    	return reservationRule;
    }
}
