package fr.paris.lutece.plugins.appointment.business.planning;

import java.time.LocalDate;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * ClosingDay DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IClosingDayDAO {

	/**
	 * The name of the bean of the DAO
	 */
	static String BEAN_NAME = "appointment.closingDayDAO";

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
	 * @param closingDay
	 *            instance of the Closing Day object to insert
	 * @param plugin
	 *            the Plugin
	 */
	void insert(ClosingDay closingDay, Plugin plugin);

	/**
	 * Update the record in the table
	 * 
	 * @param closingDay
	 *            the reference of the Closing Day
	 * @param plugin
	 *            the Plugin
	 */
	void update(ClosingDay closingDay, Plugin plugin);

	/**
	 * Delete a record from the table
	 * 
	 * @param nIdClosingDay
	 *            int identifier of the Closing Day to delete
	 * @param plugin
	 *            the Plugin
	 */
	void delete(int nIdClosingDay, Plugin plugin);

	/**
	 * Load the data from the table
	 * 
	 * @param nIdClosingDay
	 *            The identifier of the Closing Day
	 * @param plugin
	 *            the Plugin
	 * @return The instance of the Closing Day
	 */
	ClosingDay select(int nIdClosingDay, Plugin plugin);

	/**
	 * Return the closing day if exists
	 * 
	 * @param nIdForm
	 *            the Form Id
	 * @param dateOfCLosingDay
	 *            the date of the closing day
	 * @param plugin
	 *            the plugin
	 * @return the closing day if exists
	 */
	ClosingDay findByIdFormAndDateOfClosingDay(int nIdForm, LocalDate dateOfCLosingDay, Plugin plugin);

}
