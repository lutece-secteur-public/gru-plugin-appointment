package fr.paris.lutece.plugins.appointment.business.appointment;

import java.util.ArrayList;
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
	private static IAppointmentResponseDAO _dao = SpringContextService.getBean(IAppointmentResponseDAO.BEAN_NAME);
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

	/**
	 * Get the list of responses associated with an appointment
	 * 
	 * @param nIdAppointment
	 *            the id of the appointment
	 * @return the list of responses, or an empty list if no response was found
	 */
	public static List<Response> findListResponse(int nIdAppointment) {
		List<Integer> listIdResponse = _dao.findListIdResponse(nIdAppointment, _plugin);
		List<Response> listResponse = new ArrayList<Response>(listIdResponse.size());
		for (Integer nIdResponse : listIdResponse) {
			listResponse.add(ResponseHome.findByPrimaryKey(nIdResponse));
		}
		return listResponse;
	}
	
	public static List<Integer> findListIdResponse(int nIdAppointment) {
		return _dao.findListIdResponse(nIdAppointment, _plugin);
	}

}
