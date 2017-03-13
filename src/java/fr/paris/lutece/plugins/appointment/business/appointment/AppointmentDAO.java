package fr.paris.lutece.plugins.appointment.business.appointment;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Appointment objects
 * 
 * @author Laurent Payen
 *
 */
public final class AppointmentDAO implements IAppointmentDAO {

	private static final String SQL_QUERY_NEW_PK = "SELECT max(id_appointment) FROM appointment_appointment";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_appointment (id_appointment, id_user, id_slot) VALUES (?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_appointment SET id_user = ?, id_slot = ? WHERE id_appointment = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_appointment WHERE id_appointment = ?";
	private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT appointment.id_appointment, appointment.id_user, appointment.id_slot FROM appointment_appointment appointment";
	private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_appointment = ?";
	private static final String SQL_QUERY_SELECT_BY_ID_USER = SQL_QUERY_SELECT_COLUMNS + " WHERE id_user = ?";
	private static final String SQL_QUERY_SELECT_BY_ID_SLOT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_slot = ?";
	private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_AFTER_A_DATE = SQL_QUERY_SELECT_COLUMNS
			+ " INNER JOIN appointment_slot slot ON appointment.id_slot = slot.id_slot WHERE slot.id_form = ? AND slot.starting_date_time >= ?";

	@Override
	public int getNewPrimaryKey(Plugin plugin) {
		DAOUtil daoUtil = null;
		int nKey = 1;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_NEW_PK, plugin);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				nKey = daoUtil.getInt(1) + 1;
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return nKey;
	}

	@Override
	public synchronized void insert(Appointment appointment, Plugin plugin) {
		appointment.setIdAppointment(getNewPrimaryKey(plugin));
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_INSERT, appointment, plugin, true);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(Appointment appointment, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_UPDATE, appointment, plugin, false);
		executeUpdate(daoUtil);
	}

	@Override
	public void delete(int nIdAppointment, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin);
		daoUtil.setInt(1, nIdAppointment);
		executeUpdate(daoUtil);
	}

	@Override
	public Appointment select(int nIdAppointment, Plugin plugin) {
		DAOUtil daoUtil = null;
		Appointment appointment = null;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT, plugin);
			daoUtil.setInt(1, nIdAppointment);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				appointment = buildAppointment(daoUtil);
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return appointment;
	}

	@Override
	public List<Appointment> findByIdUser(int nIdUser, Plugin plugin) {
		DAOUtil daoUtil = null;
		List<Appointment> listAppointment = new ArrayList<>();
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT_BY_ID_USER, plugin);
			daoUtil.setInt(1, nIdUser);
			daoUtil.executeQuery();
			while (daoUtil.next()) {
				listAppointment.add(buildAppointment(daoUtil));
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return listAppointment;
	}

	@Override
	public List<Appointment> findByIdSlot(int nIdSlot, Plugin plugin) {
		DAOUtil daoUtil = null;
		List<Appointment> listAppointment = new ArrayList<>();
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT_BY_ID_SLOT, plugin);
			daoUtil.setInt(1, nIdSlot);
			daoUtil.executeQuery();
			while (daoUtil.next()) {
				listAppointment.add(buildAppointment(daoUtil));
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return listAppointment;
	}

	@Override
	public List<Appointment> findByIdFormAndAfterADateTime(int nIdForm,
			LocalDateTime startingDateTime, Plugin plugin) {
		DAOUtil daoUtil = null;
		List<Appointment> listAppointment = new ArrayList<>();
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT_BY_ID_FORM_AND_AFTER_A_DATE, plugin);
			daoUtil.setInt(1, nIdForm);
			daoUtil.setTimestamp(2, Timestamp.valueOf(startingDateTime));
			daoUtil.executeQuery();
			while (daoUtil.next()) {
				listAppointment.add(buildAppointment(daoUtil));
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return listAppointment;
	}

	/**
	 * Build an Appointment business object from the resultset
	 * 
	 * @param daoUtil
	 *            the prepare statement util object
	 * @return a new Appointment business object with all its attributes
	 *         assigned
	 */
	private Appointment buildAppointment(DAOUtil daoUtil) {
		int nIndex = 1;
		Appointment appointment = new Appointment();
		appointment.setIdAppointment(daoUtil.getInt(nIndex++));
		appointment.setIdUser(daoUtil.getInt(nIndex++));
		appointment.setIdSlot(daoUtil.getInt(nIndex));
		return appointment;
	}

	/**
	 * Build a daoUtil object with the query and all the attributes of the
	 * Appointment
	 * 
	 * @param suery
	 *            the query
	 * @param appointment
	 *            the Appointment
	 * @param plugin
	 *            the plugin
	 * @param isInsert
	 *            true if it is an insert query (in this case, need to set the
	 *            id). If false, it is an update, in this case, there is a where
	 *            parameter id to set
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtil(String query, Appointment appointment, Plugin plugin, boolean isInsert) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);
		if (isInsert) {
			daoUtil.setInt(nIndex++, appointment.getIdAppointment());
		}
		daoUtil.setInt(nIndex++, appointment.getIdUser());
		daoUtil.setInt(nIndex++, appointment.getIdSlot());
		if (!isInsert) {
			daoUtil.setInt(nIndex, appointment.getIdAppointment());
		}
		return daoUtil;
	}

	/**
	 * Execute a safe update (Free the connection in case of error when execute
	 * the query)
	 * 
	 * @param daoUtil
	 *            the daoUtil
	 */
	private void executeUpdate(DAOUtil daoUtil) {
		try {
			daoUtil.executeUpdate();
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
	}

}
