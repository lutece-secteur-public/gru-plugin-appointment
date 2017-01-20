package fr.paris.lutece.plugins.appointment.business.planningdefinition;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * TimeSlot DAO Interface
 * @author Laurent Payen
 *
 */
public interface ITimeSlotDAO {
	
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
	 * @param timeSlot
	 *            instance of the timeSlot object to insert
	 * @param plugin
	 *            the plugin
	 */
	void insert(TimeSlot timeSlot, Plugin plugin);

	/**
	 * Update the record in the table
	 * 
	 * @param timeSlot
	 *            the reference of the timeSlot
	 * @param plugin
	 *            the plugin
	 */
	void update(TimeSlot timeSlot, Plugin plugin);

	/**
	 * Delete a record from the table
	 * 
	 * @param nIdTimeSlot
	 *            identifier of the timeSlot to delete
	 * @param plugin
	 *            the plugin
	 */
	void delete(int nIdTimeSlot, Plugin plugin);

	/**
	 * Load the data from the table
	 * 
	 * @param nIdTimeSlot
	 *            the identifier of the timeSlot
	 * @param plugin
	 *            the plugin
	 * @return the instance of the timeSlot
	 */
	TimeSlot select(int nIdTimeSlot, Plugin plugin);
}
