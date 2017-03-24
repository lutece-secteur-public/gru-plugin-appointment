package fr.paris.lutece.plugins.appointment.business.appointment;

import java.time.LocalDateTime;
import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Appointment objects
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentHome {

	// Static variable pointed at the DAO instance
	private static IAppointmentDAO _dao = SpringContextService.getBean(IAppointmentDAO.BEAN_NAME);
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Private constructor - this class does not need to be instantiated
	 */
	private AppointmentHome() {
	}

	/**
	 * Create an instance of the Appointment class
	 * 
	 * @param appointment
	 *            The instance of the Appointment which contains the
	 *            informations to store
	 * @return The instance of the Appointment which has been created with its
	 *         primary key.
	 */
	public static Appointment create(Appointment appointment) {
		_dao.insert(appointment, _plugin);

		return appointment;
	}

	/**
	 * Update of the Appointment which is specified in parameter
	 * 
	 * @param appointment
	 *            The instance of the Appointment which contains the data to
	 *            store
	 * @return The instance of the Appointment which has been updated
	 */
	public static Appointment update(Appointment appointment) {
		_dao.update(appointment, _plugin);

		return appointment;
	}

	/**
	 * Delete the Appointment whose identifier is specified in parameter
	 * 
	 * @param nKey
	 *            The appointment Id
	 */
	public static void delete(int nKey) {
		_dao.delete(nKey, _plugin);
	}

	/**
	 * Return an instance of the Appointment whose identifier is specified in
	 * parameter
	 * 
	 * @param nKey
	 *            The Appointment primary key
	 * @return an instance of the Appointment
	 */
	public static Appointment findByPrimaryKey(int nKey) {
		return _dao.select(nKey, _plugin);
	}

	/**
	 * Return the appointments of a user
	 * 
	 * @param nIdUser
	 *            the User Id
	 * @return a list of the user appointments
	 */
	public static List<Appointment> findByIdUser(int nIdUser) {
		return _dao.findByIdUser(nIdUser, _plugin);
	}

	/**
	 * Return the appointments of a slot
	 * 
	 * @param nIdSlot
	 * @return a list of the appointments of the slot
	 */
	public static List<Appointment> findByIdSlot(int nIdSlot) {
		return _dao.findByIdSlot(nIdSlot, _plugin);
	}

	/**
	 * Return a list of appointment whose take place after a date
	 * 
	 * @param nIdForm
	 *            the form id
	 * @param startingDateTime
	 *            the date which the appointment must begin after
	 * @return the list of the appointments
	 */
	public static List<Appointment> findByIdFormAndAfterADateTime(int nIdForm, LocalDateTime startingDateTime) {
		return _dao.findByIdFormAndAfterADateTime(nIdForm, startingDateTime, _plugin);
	}

}
