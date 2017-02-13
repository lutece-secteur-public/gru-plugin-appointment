package fr.paris.lutece.plugins.appointment.business.planning;

import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Time Slot objects
 * 
 * @author Laurent Payen
 *
 */
public class TimeSlotHome {

	// Static variable pointed at the DAO instance
	private static ITimeSlotDAO _dao = SpringContextService.getBean(ITimeSlotDAO.BEAN_NAME);
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Private constructor - this class does not need to be instantiated
	 */
	private TimeSlotHome() {
	}

	/**
	 * Create an instance of the TimeSlot class
	 * 
	 * @param timeSlot
	 *            The instance of the TimeSlot which contains the informations
	 *            to store
	 * @return The instance of the TimeSlot which has been created with its
	 *         primary key.
	 */
	public static TimeSlot create(TimeSlot timeSlot) {
		_dao.insert(timeSlot, _plugin);

		return timeSlot;
	}

	/**
	 * Update of the TimeSlot which is specified in parameter
	 * 
	 * @param timeSlot
	 *            The instance of the TimeSlot which contains the data to store
	 * @return The instance of the TimeSlot which has been updated
	 */
	public static TimeSlot update(TimeSlot timeSlot) {
		_dao.update(timeSlot, _plugin);

		return timeSlot;
	}

	/**
	 * Delete the TimeSlot whose identifier is specified in parameter
	 * 
	 * @param nKey
	 *            The TimeSlot Id
	 */
	public static void delete(int nKey) {
		_dao.delete(nKey, _plugin);
	}

	/**
	 * Returns an instance of the TimeSlot whose identifier is specified in
	 * parameter
	 * 
	 * @param nKey
	 *            The TimeSlot primary key
	 * @return an instance of the TimeSlot
	 */
	public static TimeSlot findByPrimaryKey(int nKey) {
		return _dao.select(nKey, _plugin);
	}

	/**
	 * Get all the time slots of the working day given
	 * 
	 * @param nIdWorkingDay
	 *            the working day id
	 * @return the list of all the time slots of the working day
	 */
	public static List<TimeSlot> findByIdWorkingDay(int nIdWorkingDay) {
		return _dao.findByIdWorkingDay(nIdWorkingDay, _plugin);
	}

}
