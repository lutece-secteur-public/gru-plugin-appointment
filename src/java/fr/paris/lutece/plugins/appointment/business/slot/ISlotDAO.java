package fr.paris.lutece.plugins.appointment.business.slot;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Slot DAO Interface
 * @author Laurent Payen
 *
 */
public interface ISlotDAO {
	
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
	 * @param slot
	 *            instance of the Slot object to insert
	 * @param plugin
	 *            the plugin
	 */
	void insert(Slot slot, Plugin plugin);

	/**
	 * Update the record in the table
	 * 
	 * @param slot
	 *            the reference of the Slot
	 * @param plugin
	 *            the plugin
	 */
	void update(Slot slot, Plugin plugin);

	/**
	 * Delete a record from the table
	 * 
	 * @param nIdSlot
	 *            identifier of the Slot to delete
	 * @param plugin
	 *            the plugin
	 */
	void delete(int nIdSlot, Plugin plugin);

	/**
	 * Load the data from the table
	 * 
	 * @param nIdSlot
	 *            the identifier of the Slot
	 * @param plugin
	 *            the plugin
	 * @return the instance of the Slot
	 */
	Slot select(int nIdSlot, Plugin plugin);
}
