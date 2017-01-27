package fr.paris.lutece.plugins.appointment.business.message;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Form Message objects
 */
public final class FormMessageHome {

	// Static variable pointed at the DAO instance
	private static IFormMessageDAO _dao = SpringContextService.getBean(IFormMessageDAO.BEAN_NAME);
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Private constructor - this class need not be instantiated
	 */
	private FormMessageHome() {
	}

	/**
	 * Create a form message
	 * 
	 * @param formMessage
	 *            The instance of the form message to create
	 */
	public static void create(FormMessage formMessage) {
		_dao.insert(formMessage, _plugin);
	}

	/**
	 * Update a form message
	 * 
	 * @param formMessage
	 *            The form message to update
	 */
	public static void update(FormMessage formMessage) {
		_dao.update(formMessage, _plugin);
	}

	/**
	 * Delete a form message from its primary key
	 * 
	 * @param nFormMessageId
	 *            The id of the form message
	 */
	public static void delete(int nFormMessageId) {
		_dao.delete(nFormMessageId, _plugin);
	}

	/**
	 * Get a form message from its primary key
	 * 
	 * @param nFormMessageId
	 *            The id of the form message
	 * @return The form message, or null if no form message has the given
	 *         primary key
	 */
	public static FormMessage findByPrimaryKey(int nFormMessageId) {
		return _dao.select(nFormMessageId, _plugin);
	}
}
