package fr.paris.lutece.plugins.appointment.business.rule;

import java.time.LocalDate;
import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Reservation Rule objects
 * 
 * @author Laurent Payen
 *
 */
public class ReservationRuleHome {

	// Static variable pointed at the DAO instance
	private static IReservationRuleDAO _dao = SpringContextService.getBean(IReservationRuleDAO.BEAN_NAME);
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Private constructor - this class does not need to be instantiated
	 */
	private ReservationRuleHome() {
	}

	/**
	 * Create an instance of the ReservationRule class
	 * 
	 * @param reservationRule
	 *            The instance of the ReservationRule which contains the
	 *            informations to store
	 * @return The instance of the ReservationRule which has been created with
	 *         its primary key.
	 */
	public static ReservationRule create(ReservationRule reservationRule) {
		_dao.insert(reservationRule, _plugin);

		return reservationRule;
	}

	/**
	 * Update of the ReservationRule which is specified in parameter
	 * 
	 * @param reservationRule
	 *            The instance of the ReservationRule which contains the data to
	 *            store
	 * @return The instance of the ReservationRule which has been updated
	 */
	public static ReservationRule update(ReservationRule reservationRule) {
		_dao.update(reservationRule, _plugin);

		return reservationRule;
	}

	/**
	 * Delete the ReservationRule whose identifier is specified in parameter
	 * 
	 * @param nKey
	 *            The ReservationRule Id
	 */
	public static void delete(int nKey) {
		_dao.delete(nKey, _plugin);
	}

	/**
	 * Returns an instance of the ReservationRule whose identifier is specified
	 * in parameter
	 * 
	 * @param nKey
	 *            The ReservationRule primary key
	 * @return an instance of the ReservationRule
	 */
	public static ReservationRule findByPrimaryKey(int nKey) {
		return _dao.select(nKey, _plugin);
	}

	/**
	 * Returns all the Reservation Rule of a form
	 * 
	 * @param nIdForm
	 *            the Form Id
	 * @return a list of ReservationRule of the form
	 */
	public static List<ReservationRule> findByIdForm(int nIdForm) {
		return _dao.findByIdForm(nIdForm, _plugin);
	}

	/**
	 * Returns the Reservation Rule with the given search parameters
	 * 
	 * @param nIdForm
	 *            the Form Id
	 * @param dateOfApply
	 *            the date of apply
	 * @return the ReservationRule
	 */
	public static ReservationRule findByIdFormAndDateOfApply(int nIdForm, LocalDate dateOfApply) {
		return _dao.findByIdFormAndDateOfApply(nIdForm, dateOfApply, _plugin);
	}
	
	/**
	 * Returns the Reservation Rule with the given search parameters
	 * 
	 * @param nIdForm
	 *            the Form Id
	 * @param dateOfApply
	 *            the date of apply
	 * @return the ReservationRule
	 */
	public static ReservationRule findByIdFormAndClosestToDateOfApply(int nIdForm, LocalDate dateOfApply) {
		return _dao.findByIdFormAndClosestToDateOfApply(nIdForm, dateOfApply, _plugin);
	}
	
}
