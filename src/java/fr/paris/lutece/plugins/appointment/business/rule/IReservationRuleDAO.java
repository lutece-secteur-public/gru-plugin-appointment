package fr.paris.lutece.plugins.appointment.business.rule;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * ReservationRule DAO Interface
 * @author Laurent Payen
 *
 */
public interface IReservationRuleDAO {
	
	/**
     * Generate a new primary key
     * 
     * @param plugin
     *            the Plugin
     * @return the new primary key
     */
    int getNewPrimaryKey(Plugin plugin);
    
	/**
	 * Insert a new record in the table
	 * 
	 * @param reservationRule
	 *            instance of the ReservationRule object to insert
	 * @param plugin
	 *            the plugin
	 */
	void insert(ReservationRule reservationRule, Plugin plugin);

	/**
	 * Update the record in the table
	 * 
	 * @param reservationRule
	 *            the reference of the ReservationRule
	 * @param plugin
	 *            the plugin
	 */
	void update(ReservationRule appointment, Plugin plugin);

	/**
	 * Delete a record from the table
	 * 
	 * @param nIdReservationRule
	 *            int identifier of the ReservationRule to delete
	 * @param plugin
	 *            the plugin
	 */
	void delete(int nIdReservationRule, Plugin plugin);

	/**
	 * Load the data from the table
	 * 
	 * @param nIdReservationRule
	 *            the identifier of the ReservationRule
	 * @param plugin
	 *            the plugin
	 * @return the instance of the ReservationRule
	 */
	ReservationRule select(int nIdReservationRule, Plugin plugin);
}
