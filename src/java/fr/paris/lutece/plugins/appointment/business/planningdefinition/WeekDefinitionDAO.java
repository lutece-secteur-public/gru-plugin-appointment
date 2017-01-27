package fr.paris.lutece.plugins.appointment.business.planningdefinition;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Week Definition objects
 * 
 * @author Laurent Payen
 *
 */
public class WeekDefinitionDAO implements IWeekDefinitionDAO {

	private static final String SQL_QUERY_NEW_PK = "SELECT max(id_week_definition) FROM appointment_week_definition";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_week_definition (id_week_definition, date_of_apply, id_form) VALUES (?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_week_definition SET date_of_apply = ?, id_form = ? WHERE id_week_definition = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_week_definition WHERE id_week_definition = ?";
	private static final String SQL_QUERY_SELECT = "SELECT id_week_definition, date_of_apply, id_form FROM appointment_week_definition WHERE id_week_definition = ?";

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
	public synchronized void insert(WeekDefinition weekDefinition, Plugin plugin) {
		weekDefinition.setIdWeekDefinition(getNewPrimaryKey(plugin));
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_INSERT, weekDefinition, plugin, true);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(WeekDefinition weekDefinition, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_UPDATE, weekDefinition, plugin, false);
		executeUpdate(daoUtil);
	}

	@Override
	public void delete(int nIdWeekDefinition, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin);
		daoUtil.setInt(1, nIdWeekDefinition);
		executeUpdate(daoUtil);
	}

	@Override
	public WeekDefinition select(int nIdWeekDefinition, Plugin plugin) {
		DAOUtil daoUtil = null;
		WeekDefinition weekDefinition = null;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT, plugin);
			daoUtil.setInt(1, nIdWeekDefinition);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				weekDefinition = buildWeekDefinition(daoUtil);
			}
		} finally {
			daoUtil.free();
		}
		return weekDefinition;
	}

	/**
	 * Build a WeekDefinition business object from the resultset
	 * 
	 * @param daoUtil
	 *            the prepare statement util object
	 * @return a new WeekDefinition with all its attributes assigned
	 */
	private WeekDefinition buildWeekDefinition(DAOUtil daoUtil) {
		int nIndex = 1;
		WeekDefinition weekDefinition = new WeekDefinition();
		weekDefinition.setIdWeekDefinition(daoUtil.getInt(nIndex++));
		weekDefinition.setDateOfApply(daoUtil.getDate(nIndex++));
		weekDefinition.setIdForm(daoUtil.getInt(nIndex));
		return weekDefinition;
	}

	/**
	 * Build a daoUtil object with the WeekDefinition business object
	 * 
	 * @param query
	 *            the query
	 * @param weekDefinition
	 *            the Week Definition
	 * @param plugin
	 *            the plugin
	 * @param isInsert
	 *            true if it is an insert query (in this case, need to set the
	 *            id). If false, it is an update, in this case, there is a where
	 *            parameter id to set
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtil(String query, WeekDefinition weekDefinition, Plugin plugin, boolean isInsert) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);
		if (isInsert) {
			daoUtil.setInt(nIndex++, weekDefinition.getIdWeekDefinition());
		}
		daoUtil.setDate(nIndex++, weekDefinition.getSqlDateOfApply());
		daoUtil.setInt(nIndex++, weekDefinition.getIdForm());
		if (!isInsert) {
			daoUtil.setInt(nIndex, weekDefinition.getIdWeekDefinition());
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
