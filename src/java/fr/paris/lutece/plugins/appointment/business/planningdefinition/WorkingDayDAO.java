package fr.paris.lutece.plugins.appointment.business.planningdefinition;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Working Day objects
 * 
 * @author Laurent Payen
 *
 */
public class WorkingDayDAO implements IWorkingDayDAO {

	private static final String SQL_QUERY_NEW_PK = "SELECT max(id_working_day) FROM appointment_working_day";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_working_day (id_working_day, day_of_week, id_week_definition) VALUES (?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_working_day SET day_of_week = ?, id_week_definition = ? WHERE id_working_day = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_working_day WHERE id_working_day = ? ";
	private static final String SQL_QUERY_SELECT = "SELECT id_working_day, day_of_week, id_week_definition FROM appointment_working_day WHERE id_working_day = ?";

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
	public synchronized void insert(WorkingDay workingDay, Plugin plugin) {
		workingDay.setIdWorkingDay(getNewPrimaryKey(plugin));
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_INSERT, workingDay, plugin, true);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(WorkingDay workingDay, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_UPDATE, workingDay, plugin, false);
		executeUpdate(daoUtil);
	}

	@Override
	public void delete(int nIdWorkingDay, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin);
		daoUtil.setInt(1, nIdWorkingDay);
		executeUpdate(daoUtil);
	}

	@Override
	public WorkingDay select(int nIdWorkingDay, Plugin plugin) {
		DAOUtil daoUtil = null;
		WorkingDay workingDay = null;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT, plugin);
			daoUtil.setInt(1, nIdWorkingDay);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				workingDay = buildWorkingDay(daoUtil);
			}
		} finally {
			daoUtil.free();
		}
		return workingDay;
	}

	/**
	 * Build a WorkingDay business object from the resultset
	 * 
	 * @param daoUtil
	 *            the prepare statement util object
	 * @return a new WorkingDay with all its attributes assigned
	 */
	private WorkingDay buildWorkingDay(DAOUtil daoUtil) {
		int nIndex = 1;
		WorkingDay workingDay = new WorkingDay();
		workingDay.setIdWorkingDay(daoUtil.getInt(nIndex++));
		workingDay.setDayOfWeek(daoUtil.getInt(nIndex++));
		workingDay.setIdWeekDefinition(daoUtil.getInt(nIndex));
		return workingDay;
	}

	/**
	 * Build a daoUtil object with the working day business object
	 * 
	 * @param query
	 *            the query
	 * @param workingDay
	 *            the WorkingDay
	 * @param plugin
	 *            the plugin
	 * @param isInsert
	 *            true if it is an insert query (in this case, need to set the
	 *            id). If false, it is an update, in this case, there is a where
	 *            parameter id to set
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtil(String query, WorkingDay workingDay, Plugin plugin, boolean isInsert) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);
		if (isInsert) {
			daoUtil.setInt(nIndex++, workingDay.getIdWorkingDay());
		}
		daoUtil.setInt(nIndex++, workingDay.getDayOfWeek());
		daoUtil.setInt(nIndex++, workingDay.getIdWeekDefinition());
		if (!isInsert) {
			daoUtil.setInt(nIndex, workingDay.getIdWorkingDay());
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
