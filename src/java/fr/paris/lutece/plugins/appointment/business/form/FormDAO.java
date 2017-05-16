package fr.paris.lutece.plugins.appointment.business.form;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form objects
 * 
 * @author Laurent Payen
 *
 */
public class FormDAO implements IFormDAO {

	private static final String SQL_QUERY_NEW_PK = "SELECT max(id_form) FROM appointment_form";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_form (id_form, title, description, reference, id_category, starting_validity_date, ending_validity_date, is_active, id_workflow) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_form SET title = ?, description = ?, reference = ?, id_category = ?, starting_validity_date = ?, ending_validity_date = ?, is_active = ?, id_workflow = ? WHERE id_form = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_form WHERE id_form = ? ";
	private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_form, title, description, reference, id_category, starting_validity_date, ending_validity_date, is_active, id_workflow FROM appointment_form";
	private static final String SQL_QUERY_SELECT_ALL = SQL_QUERY_SELECT_COLUMNS;
	private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";
	private static final String SQL_QUERY_SELECT_ACTIVE_FORMS = SQL_QUERY_SELECT_COLUMNS + " WHERE is_active = 1";

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
	public synchronized void insert(Form form, Plugin plugin) {
		form.setIdForm(getNewPrimaryKey(plugin));
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_INSERT, form, plugin, true);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(Form form, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_UPDATE, form, plugin, false);
		executeUpdate(daoUtil);
	}

	@Override
	public void delete(int nIdForm, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin);
		daoUtil.setInt(1, nIdForm);
		executeUpdate(daoUtil);
	}

	@Override
	public Form select(int nIdForm, Plugin plugin) {
		DAOUtil daoUtil = null;
		Form form = null;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT, plugin);
			daoUtil.setInt(1, nIdForm);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				form = buildForm(daoUtil);
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return form;
	}

	@Override
	public List<Form> findActiveForms(Plugin plugin) {
		DAOUtil daoUtil = null;
		List<Form> listForms = new ArrayList<>();
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT_ACTIVE_FORMS, plugin);
			daoUtil.executeQuery();
			while (daoUtil.next()) {
				listForms.add(buildForm(daoUtil));
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return listForms;
	}

	@Override
	public List<Form> findAllForms(Plugin plugin) {
		DAOUtil daoUtil = null;
		List<Form> listForms = new ArrayList<>();
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT_ALL, plugin);
			daoUtil.executeQuery();
			while (daoUtil.next()) {
				listForms.add(buildForm(daoUtil));
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return listForms;
	}

	/**
	 * Build a Form business object from the resultset
	 * 
	 * @param daoUtil
	 *            the prepare statement util object
	 * @return a new Form with all its attributes assigned
	 */
	private Form buildForm(DAOUtil daoUtil) {
		int nIndex = 1;
		Form form = new Form();
		form.setIdForm(daoUtil.getInt(nIndex++));
		form.setTitle(daoUtil.getString(nIndex++));
		form.setDescription(daoUtil.getString(nIndex++));
		form.setReference(daoUtil.getString(nIndex++));
		form.setIdCategory(daoUtil.getInt(nIndex++));
		form.setStartingValiditySqlDate(daoUtil.getDate(nIndex++));
		form.setEndingValiditySqlDate(daoUtil.getDate(nIndex++));
		form.setIsActive(daoUtil.getBoolean(nIndex++));
		form.setIdWorkflow(daoUtil.getInt(nIndex));
		return form;
	}

	/**
	 * Build a daoUtil object with the form
	 * 
	 * @param query
	 *            the query
	 * @param form
	 *            the form
	 * @param plugin
	 *            the plugin
	 * @param isInsert
	 *            true if it is an insert query (in this case, need to set the
	 *            id). If false, it is an update, in this case, there is a where
	 *            parameter id to set
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtil(String query, Form form, Plugin plugin, boolean isInsert) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);
		if (isInsert) {
			daoUtil.setInt(nIndex++, form.getIdForm());
		}
		daoUtil.setString(nIndex++, form.getTitle());
		daoUtil.setString(nIndex++, form.getDescription());
		daoUtil.setString(nIndex++, form.getReference());
		daoUtil.setInt(nIndex++, form.getIdCategory());
		daoUtil.setDate(nIndex++, form.getStartingValiditySqlDate());
		daoUtil.setDate(nIndex++, form.getEndingValiditySqlDate());
		daoUtil.setBoolean(nIndex++, form.isActive());
		daoUtil.setInt(nIndex++, form.getIdWorkflow());
		if (!isInsert) {
			daoUtil.setInt(nIndex, form.getIdForm());
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
