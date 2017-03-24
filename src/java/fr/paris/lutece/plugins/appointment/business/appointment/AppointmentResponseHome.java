package fr.paris.lutece.plugins.appointment.business.appointment;

import java.util.List;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * Appointment Response Home
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentResponseHome {

	// Static variable pointed at the DAO instance
	private static IAppointmentResponseDAO _dao = SpringContextService.getBean("appointment.appointmentResponseDAO");
	private static Plugin _plugin = PluginService.getPlugin(AppointmentPlugin.PLUGIN_NAME);

	/**
	 * Remove every appointment responses associated with a given entry.
	 * 
	 * @param nIdEntry
	 *            The id of the entry
	 */
	public static void removeResponsesByIdEntry(int nIdEntry) {
		ResponseFilter filter = new ResponseFilter();
		filter.setIdEntry(nIdEntry);
		List<Response> listResponses = ResponseHome.getResponseList(filter);
		for (Response response : listResponses) {
			_dao.removeAppointmentResponsesByIdResponse(response.getIdResponse(), _plugin);
			ResponseHome.remove(response.getIdResponse());
		}
	}

}
