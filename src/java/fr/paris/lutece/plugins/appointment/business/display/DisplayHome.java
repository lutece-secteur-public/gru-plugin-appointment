package fr.paris.lutece.plugins.appointment.business.display;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Display objects
 * 
 * @author Laurent Payen
 *
 */
public class DisplayHome {

	// Static variable pointed at the DAO instance
	private static IDisplayDAO _dao = SpringContextService.getBean(IDisplayDAO.BEAN_NAME);
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Private constructor - this class does not need to be instantiated
	 */
	private DisplayHome() {
	}

	/**
	 * Create an instance of the Display class
	 * 
	 * @param display
	 *            The instance of the Display which contains the informations to
	 *            store
	 * @return The instance of Display which has been created with its primary
	 *         key.
	 */
	public static Display create(Display display) {
		_dao.insert(display, _plugin);

		return display;
	}

	/**
	 * Update of the Display which is specified in parameter
	 * 
	 * @param display
	 *            The instance of the Display which contains the data to store
	 * @return The instance of the Display which has been updated
	 */
	public static Display update(Display display) {
		_dao.update(display, _plugin);

		return display;
	}

	/**
	 * Delete the Display whose identifier is specified in parameter
	 * 
	 * @param nKey
	 *            The Display Id
	 */
	public static void delete(int nKey) {
		_dao.delete(nKey, _plugin);
	}

	/**
	 * Returns an instance of the Display whose identifier is specified in
	 * parameter
	 * 
	 * @param nKey
	 *            The Display primary key
	 * @return an instance of the Display
	 */
	public static Display findByPrimaryKey(int nKey) {
		return _dao.select(nKey, _plugin);
	}
	
}
