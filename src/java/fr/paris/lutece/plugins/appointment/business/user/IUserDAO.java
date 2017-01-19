package fr.paris.lutece.plugins.appointment.business.user;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface IUserDAO {

	/**
	 * 
	 * Insert a new record in the table.
	 * 
	 * @param user
	 *            instance of the user object to insert
	 * @param plugin
	 *            the Plugin
	 */
	void insert(User user, Plugin plugin);

	/**
	 * Update the record in the table
	 * 
	 * @param user
	 *            the reference of the user
	 * @param plugin
	 *            the Plugin
	 */
	void update(User user, Plugin plugin);

	/**
	 * Delete a record from the table
	 * 
	 * @param nIdUser
	 *            int identifier of the user to delete
	 * @param plugin
	 *            the Plugin
	 */
	void delete(int nIdUser, Plugin plugin);

	/**
	 * Load the data from the table
	 * 
	 * @param nIdUser
	 *            The identifier of the user
	 * @param plugin
	 *            the Plugin
	 * @return The instance of the appoinusertment
	 */
	User select(int nIdUser, Plugin plugin);

}
