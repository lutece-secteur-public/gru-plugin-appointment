package fr.paris.lutece.plugins.appointment.business.planning;

import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Closing Day objects
 * 
 * @author Laurent Payen
 *
 */
public class ClosingDayHome {

	// Static variable pointed at the DAO instance
	private static IClosingDayDAO _dao = SpringContextService.getBean(IClosingDayDAO.BEAN_NAME);
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Private constructor - this class does not need to be instantiated
	 */
	private ClosingDayHome() {
	}

	/**
	 * Create an instance of the Form class
	 * 
	 * @param closingDay
	 *            The instance of the ClosingDay which contains the informations
	 *            to store
	 * @return The instance of the ClosingDay which has been created with its
	 *         primary key.
	 */
	public static ClosingDay create(ClosingDay closingDay) {
		_dao.insert(closingDay, _plugin);

		return closingDay;
	}

	/**
	 * Update of the ClosingDay which is specified in parameter
	 * 
	 * @param closingDay
	 *            The instance of the ClosingDay which contains the data to
	 *            store
	 * @return The instance of the ClosingDay which has been updated
	 */
	public static ClosingDay update(ClosingDay closingDay) {
		_dao.update(closingDay, _plugin);

		return closingDay;
	}

	/**
	 * Delete the ClosingDay whose identifier is specified in parameter
	 * 
	 * @param nKey
	 *            The ClosingDay Id
	 */
	public static void delete(int nKey) {
		_dao.delete(nKey, _plugin);
	}

	/**
	 * Returns an instance of the ClosingDay whose identifier is specified in
	 * parameter
	 * 
	 * @param nKey
	 *            The ClosingDay primary key
	 * @return an instance of the ClosingDay
	 */
	public static ClosingDay findByPrimaryKey(int nKey) {
		return _dao.select(nKey, _plugin);
	}

	public static List<ClosingDay> findByIdForm(int nIdForm) {
		return _dao.findByIdForm(nIdForm, _plugin);
	}

	public static List<ClosingDay> findByIdFormAndDateRange(int nIdForm, LocalDate startingDate, LocalDate endingDate) {
		return _dao.findByIdFormAndDateRange(nIdForm, startingDate, endingDate, _plugin);
	}

	/**
	 * Returns the closing day
	 * 
	 * @param nIdForm
	 *            the Form Id
	 * @param dateOfClosingDay
	 *            the date of the closing day
	 * @return an instance of the ClosingDay if it exists, null otherwise
	 */
	public static ClosingDay findByIdFormAndDateOfCLosingDay(int nIdForm, LocalDate dateOfClosingDay) {
		return _dao.findByIdFormAndDateOfClosingDay(nIdForm, dateOfClosingDay, _plugin);
	}
}
