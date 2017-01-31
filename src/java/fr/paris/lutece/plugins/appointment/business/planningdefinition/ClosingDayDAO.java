package fr.paris.lutece.plugins.appointment.business.planningdefinition;

import java.sql.Date;
import java.time.LocalDate;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Closing Day objects
 * 
 * @author Laurent Payen
 *
 */
public class ClosingDayDAO implements IClosingDayDAO {

	private static final String SQL_QUERY_NEW_PK = "SELECT max(id_closing_day) FROM appointment_closing_day";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_closing_day (id_closing_day, date_of_closing_day, id_form) VALUES ( ?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_closing_day SET date_of_closing_day = ?, id_form = ? WHERE id_closing_day = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_closing_day WHERE id_closing_day = ?";
	private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_closing_day, date_of_closing_day, id_form FROM appointment_closing_day";
	private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_closing_day = ?";
	private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_OF_CLOSING_DAY = SQL_QUERY_SELECT_COLUMNS
			+ " WHERE id_form = ? AND date_of_closing_day = ?";

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
	public synchronized void insert(ClosingDay closingDay, Plugin plugin) {
		closingDay.setIdClosingDay(getNewPrimaryKey(plugin));
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_INSERT, closingDay, plugin, true);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(ClosingDay closingDay, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_UPDATE, closingDay, plugin, false);
		executeUpdate(daoUtil);
	}

	@Override
	public void delete(int nIdClosingDay, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin);
		daoUtil.setInt(1, nIdClosingDay);
		executeUpdate(daoUtil);
	}

	@Override
	public ClosingDay select(int nIdClosingDay, Plugin plugin) {
		DAOUtil daoUtil = null;
		ClosingDay closingDay = null;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT, plugin);
			daoUtil.setInt(1, nIdClosingDay);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				closingDay = buildClosingDay(daoUtil);
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return closingDay;
	}

	@Override
	public ClosingDay findByIdFormAndDateOfClosingDay(int nIdForm, LocalDate dateOfCLosingDay, Plugin plugin) {
		DAOUtil daoUtil = null;
		ClosingDay closingDay = null;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_OF_CLOSING_DAY, plugin);
			daoUtil.setInt(1, nIdForm);
			daoUtil.setDate(2, Date.valueOf(dateOfCLosingDay));
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				closingDay = buildClosingDay(daoUtil);
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return closingDay;
	}

	/**
	 * Build a Closing Day business object from the resultset
	 * 
	 * @param daoUtil
	 *            the prepare statement util object
	 * @return a new Closing Day with all its attributes assigned
	 */
	private ClosingDay buildClosingDay(DAOUtil daoUtil) {
		int nIndex = 1;
		ClosingDay closingDay = new ClosingDay();
		closingDay.setIdClosingDay(daoUtil.getInt(nIndex++));
		closingDay.setDateOfClosingDay(daoUtil.getDate(nIndex++));
		closingDay.setIdForm(daoUtil.getInt(nIndex));
		return closingDay;
	}

	/**
	 * Build a daoUtil object with the CLosingDay business object
	 * 
	 * @param query
	 *            the query
	 * @param closingDay
	 *            the closingDay
	 * @param plugin
	 *            the plugin
	 * @param isInsert
	 *            true if it is an insert query (in this case, need to set the
	 *            id). If false, it is an update, in this case, there is a where
	 *            parameter id to set
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtil(String query, ClosingDay closingDay, Plugin plugin, boolean isInsert) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);
		if (isInsert) {
			daoUtil.setInt(nIndex++, closingDay.getIdClosingDay());
		}
		daoUtil.setDate(nIndex++, closingDay.getSqlDateOfClosingDay());
		daoUtil.setInt(nIndex++, closingDay.getIdForm());
		if (!isInsert) {
			daoUtil.setInt(nIndex, closingDay.getIdClosingDay());
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
