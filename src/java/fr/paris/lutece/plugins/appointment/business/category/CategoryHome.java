package fr.paris.lutece.plugins.appointment.business.category;

import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Category objects
 * 
 * @author Laurent Payen
 *
 */
public class CategoryHome {

	// Static variable pointed at the DAO instance
	private static ICategoryDAO _dao = SpringContextService.getBean(ICategoryDAO.BEAN_NAME);
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Private constructor - this class does not need to be instantiated
	 */
	private CategoryHome() {
	}

	/**
	 * Create an instance of the Category class
	 * 
	 * @param category
	 *            The instance of the Category which contains the informations
	 *            to store
	 * @return The instance of Category which has been created with its primary
	 *         key.
	 */
	public static Category create(Category category) {
		_dao.insert(category, _plugin);

		return category;
	}

	/**
	 * Update of the Category which is specified in parameter
	 * 
	 * @param category
	 *            The instance of the Category which contains the data to store
	 * @return The instance of the Category which has been updated
	 */
	public static Category update(Category category) {
		_dao.update(category, _plugin);

		return category;
	}

	/**
	 * Delete the Category whose identifier is specified in parameter
	 * 
	 * @param nKey
	 *            The Category Id
	 */
	public static void delete(int nKey) {
		_dao.delete(nKey, _plugin);
	}

	/**
	 * Returns an instance of the Category whose identifier is specified in
	 * parameter
	 * 
	 * @param nKey
	 *            The Category primary key
	 * @return an instance of the Category
	 */
	public static Category findByPrimaryKey(int nKey) {
		return _dao.select(nKey, _plugin);
	}

	/**
	 * Returns all the Categories parameter
	 * 
	 * @return a list of all the categories
	 */
	public static List<Category> findAllCategories() {
		return _dao.findAllCategories(_plugin);
	}

	/**
	 * Find a category by its Label
	 * 
	 * @param strLabel
	 *            the label
	 * @return an instance of the category
	 */
	public static Category findByLabel(String strLabel) {
		return _dao.findByLabel(strLabel, _plugin);
	}

}
