package fr.paris.lutece.plugins.appointment.business.appointment;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Appointment Response DAO
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentResponseDAO implements IAppointmentResponseDAO {

	private static final String SQL_QUERY_REMOVE_FROM_ID_RESPONSE = "DELETE FROM appointment_appointment_response WHERE id_response = ?";
	private static final String SQL_QUERY_SELECT_APPOINTMENT_RESPONSE_LIST = "SELECT id_response FROM appointment_appointment_response WHERE id_appointment = ?";

	@Override
	public void removeAppointmentResponsesByIdResponse(int nIdResponse, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_REMOVE_FROM_ID_RESPONSE, plugin);
		daoUtil.setInt(1, nIdResponse);
		daoUtil.executeUpdate();
		daoUtil.free();
	}

	@Override
	public List<Integer> findListIdResponse(int nIdAppointment, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_SELECT_APPOINTMENT_RESPONSE_LIST, plugin);
		daoUtil.setInt(1, nIdAppointment);
		daoUtil.executeQuery();
		List<Integer> listIdResponse = new ArrayList<Integer>();
		while (daoUtil.next()) {
			listIdResponse.add(daoUtil.getInt(1));
		}
		daoUtil.free();
		return listIdResponse;
	}

}
