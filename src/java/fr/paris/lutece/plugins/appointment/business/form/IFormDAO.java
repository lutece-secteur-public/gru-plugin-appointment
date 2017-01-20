package fr.paris.lutece.plugins.appointment.business.form;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Form DAO Interface
 * @author Laurent Payen
 *
 */
public interface IFormDAO {

	/**
     * Generate a new primary key
     * 
     * @param plugin
     *            the Plugin
     * @return the new primary key
     */
    int getNewPrimaryKey(Plugin plugin);
    
	/**
	 * 
	 * Insert a new record in the table
	 * 
	 * @param form
	 *            instance of the form object to insert
	 * @param plugin
	 *            the plugin
	 */
	void insert(Form form, Plugin plugin);

	/**
	 * Update the record in the table
	 * 
	 * @param form
	 *            the reference of the form
	 * @param plugin
	 *            the plugin
	 */
	void update(Form form, Plugin plugin);

	/**
	 * Delete a record from the table
	 * 
	 * @param nIdFrom
	 *            identifier of the form to delete
	 * @param plugin
	 *            the plugin
	 */
	void delete(int nIdForm, Plugin plugin);

	/**
	 * Load the data from the table
	 * 
	 * @param nIdForm
	 *            the identifier of the form
	 * @param plugin
	 *            the plugin
	 * @return the instance of the Form
	 */
	Form select(int nIdForm, Plugin plugin);

}
