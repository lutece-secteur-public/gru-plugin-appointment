package fr.paris.lutece.plugins.appointment.business.display;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Display DAO Interface
 * @author Laurent Payen
 *
 */
public interface IDisplayDAO {
	
	/**
     * Generate a new primary key
     * 
     * @param plugin
     *            the Plugin
     * @return the new primary key
     */
    int getNewPrimaryKey(Plugin plugin);
    
	/**
	 * Insert a new record in the table.
	 * 
	 * @param display
	 *            instance of the Display object to insert
	 * @param plugin
	 *            the Plugin
	 */
	void insert(Display display, Plugin plugin);

	/**
	 * Update the record in the table	
	 * 
	 * @param display
	 *            the reference of the Display
	 * @param plugin
	 *            the Plugin
	 */
	void update(Display display, Plugin plugin);

	/**
	 * Delete a record from the table
	 * 
	 * @param nIdDisplay
	 *            identifier of the Display to delete
	 * @param plugin
	 *            the Plugin
	 */
	void delete(int nIdDisplay, Plugin plugin);

	/**
	 * Load the data from the table
	 * 
	 * @param nIdDisplay
	 *            The identifier of the Display
	 * @param plugin
	 *            the Plugin
	 * @return The instance of the Display
	 */
	Display select(int nIdDisplay, Plugin plugin);
}
