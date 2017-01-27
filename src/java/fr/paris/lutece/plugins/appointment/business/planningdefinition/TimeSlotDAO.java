package fr.paris.lutece.plugins.appointment.business.planningdefinition;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Time Slot objects
 * 
 * @author Laurent Payen
 *
 */
public class TimeSlotDAO implements ITimeSlotDAO {

	private static final String SQL_QUERY_NEW_PK = "SELECT max(id_time_slot) FROM appointment_time_slot";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_time_slot (id_time_slot, starting_hour, ending_hour, is_open, id_working_day) VALUES (?, ?, ?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_time_slot SET starting_hour = ?, ending_hour = ?, is_open = ?, id_working_day = ? WHERE id_time_slot = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_time_slot WHERE id_time_slot = ?";
	private static final String SQL_QUERY_SELECT = "SELECT id_time_slot, starting_hour, ending_hour, is_open, id_working_day FROM appointment_time_slot WHERE id_time_slot = ?";

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
	public synchronized void insert(TimeSlot timeSlot, Plugin plugin) {
		timeSlot.setIdTimeSlot(getNewPrimaryKey(plugin));
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_INSERT, timeSlot, plugin, true);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(TimeSlot timeSlot, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_UPDATE, timeSlot, plugin, false);
		executeUpdate(daoUtil);
	}

	@Override
	public void delete(int nIdTimeSlot, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin);
		daoUtil.setInt(1, nIdTimeSlot);
		executeUpdate(daoUtil);
	}

	@Override
	public TimeSlot select(int nIdTimeSlot, Plugin plugin) {
		DAOUtil daoUtil = null;
		TimeSlot timeSlot = null;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT, plugin);
			daoUtil.setInt(1, nIdTimeSlot);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				timeSlot = buildTimeSlot(daoUtil);
			}
		} finally {
			daoUtil.free();
		}
		return timeSlot;
	}

	/**
	 * Build a time slot business object from the resultset
	 * 
	 * @param daoUtil
	 *            the prepare statement util object
	 * @return a new time slot with all its attributes assigned
	 */
	private TimeSlot buildTimeSlot(DAOUtil daoUtil) {
		int nIndex = 1;
		TimeSlot timeSlot = new TimeSlot();
		timeSlot.setIdTimeSlot(daoUtil.getInt(nIndex++));
		timeSlot.setStartingHour(daoUtil.getTime(nIndex++));
		timeSlot.setEndingHour(daoUtil.getTime(nIndex++));
		timeSlot.setIsOpen(daoUtil.getBoolean(nIndex++));
		timeSlot.setIdWorkingDay(daoUtil.getInt(nIndex));
		return timeSlot;
	}

	/**
	 * Build a daoUtil object with time slot business object
	 * 
	 * @param query
	 *            the query
	 * @param timeSlot
	 *            the time slot
	 * @param plugin
	 *            the plugin
	 * @param isInsert
	 *            true if it is an insert query (in this case, need to set the
	 *            id). If false, it is an update, in this case, there is a where
	 *            parameter id to set
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtil(String query, TimeSlot timeSlot, Plugin plugin, boolean isInsert) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);
		if (isInsert) {
			daoUtil.setInt(nIndex++, timeSlot.getIdTimeSlot());
		}
		daoUtil.setTime(nIndex++, timeSlot.getStartingHourSqlTime());
		daoUtil.setTime(nIndex++, timeSlot.getEndingHourSqlTime());
		daoUtil.setBoolean(nIndex++, timeSlot.isOpen());
		daoUtil.setInt(nIndex++, timeSlot.getIdWorkingDay());
		if (!isInsert) {
			daoUtil.setInt(nIndex, timeSlot.getIdTimeSlot());
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
