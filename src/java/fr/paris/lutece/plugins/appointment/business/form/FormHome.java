package fr.paris.lutece.plugins.appointment.business.form;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Form objects
 * 
 * @author Laurent Payen
 *
 */
public class FormHome {

	// Static variable pointed at the DAO instance
	private static IFormDAO _dao = SpringContextService.getBean(IFormDAO.BEAN_NAME);
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Private constructor - this class does not need to be instantiated
	 */
	private FormHome() {
	}

	/**
	 * Create an instance of the Form class
	 * 
	 * @param form
	 *            The instance of the Form which contains the informations to
	 *            store
	 * @return The instance of the Form which has been created with its primary key.
	 */
	public static Form create(Form form) {
		_dao.insert(form, _plugin);

		return form;
	}

	/**
	 * Update of the Form which is specified in parameter
	 * 
	 * @param form
	 *            The instance of the Form which contains the data to store
	 * @return The instance of the Form which has been updated
	 */
	public static Form update(Form form) {
		_dao.update(form, _plugin);

		return form;
	}

	/**
	 * Delete the Form whose identifier is specified in parameter
	 * 
	 * @param nKey
	 *            The Form Id
	 */
	public static void delete(int nKey) {
		_dao.delete(nKey, _plugin);
	}

	/**
	 * Returns an instance of the Form whose identifier is specified in
	 * parameter
	 * 
	 * @param nKey
	 *            The Form primary key
	 * @return an instance of the Form
	 */
	public static Form findByPrimaryKey(int nKey) {
		return _dao.select(nKey, _plugin);
	}

}
