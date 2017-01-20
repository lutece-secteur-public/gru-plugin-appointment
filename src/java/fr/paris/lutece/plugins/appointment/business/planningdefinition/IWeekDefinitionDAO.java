package fr.paris.lutece.plugins.appointment.business.planningdefinition;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * WeekDefinition DAO Interface
 * @author Laurent Payen
 *
 */
public interface IWeekDefinitionDAO {
	
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
	 * @param appointment
	 *            instance of the WeekDefinition object to insert
	 * @param plugin
	 *            the plugin
	 */
	void insert(WeekDefinition weekDefinition, Plugin plugin);

	/**
	 * Update the record in the table
	 * 
	 * @param weekDefinition
	 *            the reference of the WeekDefinition
	 * @param plugin
	 *            the plugin
	 */
	void update(WeekDefinition weekDefinition, Plugin plugin);

	/**
	 * Delete a record from the table
	 * 
	 * @param nIdWeekDefinition
	 *            identifier of the WeekDefinition to delete
	 * @param plugin
	 *            the plugin
	 */
	void delete(int nIdWeekDefinition, Plugin plugin);

	/**
	 * Load the data from the table
	 * 
	 * @param nIdWeekDefinition
	 *            the identifier of the weekDefinition
	 * @param plugin
	 *            the plugin
	 * @return the instance of the weekDefinition
	 */
	WeekDefinition select(int nIdWeekDefinition, Plugin plugin);
}
