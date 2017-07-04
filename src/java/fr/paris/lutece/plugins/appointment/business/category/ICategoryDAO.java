package fr.paris.lutece.plugins.appointment.business.category;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Category DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface ICategoryDAO {

	/**
	 * The name of the bean of the DAO
	 */
	static String BEAN_NAME = "appointment.categoryDAO";

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
	 * @param category
	 *            instance of the Category object to insert
	 * @param plugin
	 *            the Plugin
	 */
	void insert(Category category, Plugin plugin);

	/**
	 * Update the record in the table
	 * 
	 * @param category
	 *            the reference of the Category
	 * @param plugin
	 *            the Plugin
	 */
	void update(Category category, Plugin plugin);

	/**
	 * Delete a record from the table
	 * 
	 * @param nIdCategory
	 *            identifier of the Category to delete
	 * @param plugin
	 *            the Plugin
	 */
	void delete(int nIdCategory, Plugin plugin);

	/**
	 * Load the data from the table
	 * 
	 * @param nIdCategory
	 *            The identifier of the Category
	 * @param plugin
	 *            the Plugin
	 * @return The instance of the Category
	 */
	Category select(int nIdCategory, Plugin plugin);

	/**
	 * Find all the categories
	 * 
	 * @param plugin
	 *            the plugin
	 * @return a list of all the categories
	 */
	List<Category> findAllCategories(Plugin plugin);

	/**
	 * Find a category by its category label
	 * 
	 * @param strLabel
	 *            the label
	 * @param plugin
	 *            the plugin
	 * @return an instance of the category
	 */
	Category findByLabel(String strLabel, Plugin plugin);
}
