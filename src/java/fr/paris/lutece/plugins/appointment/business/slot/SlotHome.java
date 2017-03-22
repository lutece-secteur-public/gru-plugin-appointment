package fr.paris.lutece.plugins.appointment.business.slot;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Slot objects
 * 
 * @author Laurent Payen
 *
 */
public class SlotHome {

	// Static variable pointed at the DAO instance
	private static ISlotDAO _dao = SpringContextService.getBean(ISlotDAO.BEAN_NAME);
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Private constructor - this class does not need to be instantiated
	 */
	private SlotHome() {
	}

	/**
	 * Create an instance of the Slot class
	 * 
	 * @param slot
	 *            The instance of the Slot which contains the informations to
	 *            store
	 * @return The instance of the Slot which has been created with its primary
	 *         key.
	 */
	public static Slot create(Slot slot) {
		_dao.insert(slot, _plugin);

		return slot;
	}

	/**
	 * Update of the Slot which is specified in parameter
	 * 
	 * @param slot
	 *            The instance of the Slot which contains the data to store
	 * @return The instance of the Slot which has been updated
	 */
	public static Slot update(Slot slot) {
		_dao.update(slot, _plugin);

		return slot;
	}

	/**
	 * Delete the Slot whose identifier is specified in parameter
	 * 
	 * @param nKey
	 *            The Slot Id
	 */
	public static void delete(int nKey) {
		_dao.delete(nKey, _plugin);
	}

	/**
	 * Returns an instance of the Slot whose identifier is specified in
	 * parameter
	 * 
	 * @param nKey
	 *            The Slot primary key
	 * @return an instance of the Slot
	 */
	public static Slot findByPrimaryKey(int nKey) {
		return _dao.select(nKey, _plugin);
	}

	/**
	 * Returns a list of slots for a date range
	 * 
	 * @param nIdForm
	 *            the Form Id
	 * @param startingDateTime
	 *            the starting Date
	 * @param endingDateTime
	 *            the ending Date
	 * @return a list of slots whose dates are included in the given period
	 */
	public static HashMap<LocalDateTime, Slot> findByIdFormAndDateRange(int nIdForm, LocalDateTime startingDateTime,
			LocalDateTime endingDateTime) {
		return _dao.findByIdFormAndDateRange(nIdForm, startingDateTime, endingDateTime, _plugin);
	}	

	/**
	 * Returns a list of open slots for a date range
	 * 
	 * @param nIdForm
	 *            the Form Id
	 * @param startingDateTime
	 *            the starting Date
	 * @param endingDateTime
	 *            the ending Date
	 * @return a list of open slots whose dates are included in the given period
	 */
	public static List<Slot> findOpenSlotsByIdFormAndDateRange(int nIdForm, LocalDateTime startingDateTime,
			LocalDateTime endingDateTime) {
		return _dao.findOpenSlotsByIdFormAndDateRange(nIdForm, startingDateTime, endingDateTime, _plugin);
	}

	/**
	 * Returns a list of open slots
	 * 
	 * @param nIdForm
	 *            the Form Id
	 * @return a list of open slots
	 */
	public static List<Slot> findOpenSlotsByIdForm(int nIdForm) {
		return _dao.findOpenSlotsByIdForm(nIdForm, _plugin);
	}

}
