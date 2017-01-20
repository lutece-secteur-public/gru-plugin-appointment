package fr.paris.lutece.plugins.appointment.business.user;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for User objects
 * 
 * @author Laurent Payen
 *
 */
public class UserDAO implements IUserDAO {

	private static final String SQL_QUERY_NEW_PK = "SELECT max (id_user) FROM appointment_user";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_user (id_user, id_lutece_user, first_name, last_name, email, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_user SET id_lutece_user = ?, first_name = ?, last_name = ?, email = ?, phone_numer = ? WHERE id_user = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_user WHERE id_user = ?";
	private static final String SQL_QUERY_SELECT = "SELECT id_user, id_lutece_user, first_name, last_name, email, phone_number FROM appointment_user WHERE id_user = ?";

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
	public synchronized void insert(User user, Plugin plugin) {
		user.setIdUser(getNewPrimaryKey(plugin));
		DAOUtil daoUtil = buildDaoUtilFromUser(SQL_QUERY_INSERT, user, plugin);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(User user, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtilFromUser(SQL_QUERY_UPDATE, user, plugin);
		executeUpdate(daoUtil);
	}

	@Override
	public void delete(int nIdUser, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin);
		daoUtil.setInt(1, nIdUser);
		executeUpdate(daoUtil);
	}

	@Override
	public User select(int nIdUser, Plugin plugin) {
		DAOUtil daoUtil = null;
		User user = null;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT, plugin);
			daoUtil.setInt(1, nIdUser);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				user = buildUserFromDaoUtil(daoUtil);
			}
		} finally {
			daoUtil.free();
		}
		return user;
	}

	/**
	 * Build a User business object from the resultset
	 * 
	 * @param daoUtil
	 *            the prepare statement util object
	 * @return a new User with all its attributes assigned
	 */
	private User buildUserFromDaoUtil(DAOUtil daoUtil) {
		int nIndex = 1;
		User user = new User();
		user.setIdUser(daoUtil.getInt(nIndex++));
		user.setIdLuteceUser(daoUtil.getInt(nIndex++));
		user.setFirstName(daoUtil.getString(nIndex++));
		user.setLastName(daoUtil.getString(nIndex++));
		user.setEmail(daoUtil.getString(nIndex++));
		user.setPhoneNumber(daoUtil.getString(nIndex++));
		return user;
	}

	/**
	 * Build a daoUtil object with the User business object
	 * 
	 * @param query
	 *            the query
	 * @param user
	 *            the User
	 * @param plugin
	 *            the plugin
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtilFromUser(String query, User user, Plugin plugin) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);
		daoUtil.setInt(nIndex++, user.getIdUser());
		daoUtil.setInt(nIndex++, user.getIdLuteceUser());
		daoUtil.setString(nIndex++, user.getFirstName());
		daoUtil.setString(nIndex++, user.getLastName());
		daoUtil.setString(nIndex++, user.getEmail());
		daoUtil.setString(nIndex, user.getPhoneNumber());
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
