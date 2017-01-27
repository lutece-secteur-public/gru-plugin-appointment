package fr.paris.lutece.plugins.appointment.business.rule;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Form Rule objects
 * 
 * @author Laurent Payen
 *
 */
public class FormRuleHome {

	// Static variable pointed at the DAO instance
	private static IFormRuleDAO _dao = SpringContextService.getBean(IFormRuleDAO.BEAN_NAME);
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Private constructor - this class does not need to be instantiated
	 */
	private FormRuleHome() {
	}

	/**
	 * Create an instance of the FormRule class
	 * 
	 * @param formRule
	 *            The instance of the FormRule which contains the informations
	 *            to store
	 * @return The instance of the FormRule which has been created with its
	 *         primary key.
	 */
	public static FormRule create(FormRule formRule) {
		_dao.insert(formRule, _plugin);

		return formRule;
	}

	/**
	 * Update of the FormRule which is specified in parameter
	 * 
	 * @param formRule
	 *            The instance of the FormRule which contains the data to store
	 * @return The instance of the FormRule which has been updated
	 */
	public static FormRule update(FormRule formRule) {
		_dao.update(formRule, _plugin);

		return formRule;
	}

	/**
	 * Delete the FormRule whose identifier is specified in parameter
	 * 
	 * @param nKey
	 *            The FormRule Id
	 */
	public static void delete(int nKey) {
		_dao.delete(nKey, _plugin);
	}

	/**
	 * Returns an instance of the FormRule whose identifier is specified in
	 * parameter
	 * 
	 * @param nKey
	 *            The FormRule primary key
	 * @return an instance of the FormRule
	 */
	public static FormRule findByPrimaryKey(int nKey) {
		return _dao.select(nKey, _plugin);
	}

}
