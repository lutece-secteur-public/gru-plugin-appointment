package fr.paris.lutece.plugins.appointment.business.user;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for User objects
 * 
 * @author Laurent Payen
 *
 */
public class UserHome {

	// Static variable pointed at the DAO instance
	private static IUserDAO _dao = SpringContextService.getBean(IUserDAO.BEAN_NAME);
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Private constructor - this class does not need to be instantiated
	 */
	private UserHome() {
	}

	/**
	 * Create an instance of the User class
	 * 
	 * @param user
	 *            The instance of the User which contains the informations to
	 *            store
	 * @return The instance of the User which has been created with its primary
	 *         key.
	 */
	public static User create(User user) {
		_dao.insert(user, _plugin);

		return user;
	}

	/**
	 * Update of the User which is specified in parameter
	 * 
	 * @param user
	 *            The instance of the User which contains the data to store
	 * @return The instance of the User which has been updated
	 */
	public static User update(User user) {
		_dao.update(user, _plugin);

		return user;
	}

	/**
	 * Delete the User whose identifier is specified in parameter
	 * 
	 * @param nKey
	 *            The User Id
	 */
	public static void delete(int nKey) {
		_dao.delete(nKey, _plugin);
	}

	/**
	 * Returns an instance of the User whose identifier is specified in
	 * parameter
	 * 
	 * @param nKey
	 *            The User primary key
	 * @return an instance of the User
	 */
	public static User findByPrimaryKey(int nKey) {
		return _dao.select(nKey, _plugin);
	}

	/**
	 * Return the list of the appointments of the User
	 */
	public List<Appointment> getListAppointments() {
		List<Appointment> appointments = new ArrayList<>();

		return appointments;
	}

}
