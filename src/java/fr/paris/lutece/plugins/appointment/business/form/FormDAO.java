package fr.paris.lutece.plugins.appointment.business.form;

import java.sql.Date;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form objects
 * @author Laurent Payen
 *
 */
public class FormDAO implements IFormDAO {

	private static final String SQL_QUERY_NEW_PK = "SELECT max(id_form) FROM appointment_form";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_form (id_form, title, description, reference, category, starting_validity_date, ending_validity_date, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_form SET title = ?, description = ?, reference = ?, category = ?, starting_validity_date = ?, ending_validity_date = ?, is_active = ? WHERE id_form = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_form WHERE id_form = ? ";
	private static final String SQL_QUERY_SELECT = "SELECT id_form, title, description, reference, category, starting_validity_date, ending_validity_date, is_active FROM appointment_form WHERE id_form = ?";

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
		DAOUtil daoUtil = buildDaoUtilFromForm(SQL_QUERY_INSERT, form, plugin);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(Form form, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtilFromForm(SQL_QUERY_UPDATE, form, plugin);
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
				form = buildFormFromDaoUtil(daoUtil);
			}
		} finally {
			daoUtil.free();
		}
		return form;
	}

	/**
	 * Build a Form business object from the resultset 
	 * @param daoUtil the prepare statement util object
	 * @return a new Form with all its attributes assigned
	 */
	private Form buildFormFromDaoUtil(DAOUtil daoUtil) {
		int nIndex = 1;
		Form form = new Form();
		form.setIdForm(daoUtil.getInt(nIndex++));
		form.setTitle(daoUtil.getString(nIndex++));
		form.setDescription(daoUtil.getString(nIndex++));
		form.setReference(daoUtil.getString(nIndex++));
		form.setCategory(daoUtil.getString(nIndex++));
		form.setStartingValidityDate(daoUtil.getDate(nIndex++));
		form.setEndingValidityDate(daoUtil.getDate(nIndex++));
		form.setIsActive(daoUtil.getBoolean(nIndex++));		
		return form;
	}

	/**
	 * Build a daoUtil object with the form
	 * @param query the query 
	 * @param form the form
	 * @param plugin the plugin
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtilFromForm(String query, Form form, Plugin plugin) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);		
		daoUtil.setInt(nIndex++, form.getIdForm());
		daoUtil.setString(nIndex++, form.getTitle());
		daoUtil.setString(nIndex++, form.getDescription());
		daoUtil.setString(nIndex++, form.getReference());
		daoUtil.setString(nIndex++, form.getCategory());
		daoUtil.setDate(nIndex++, form.getStartingValiditySqlDate());
		daoUtil.setDate(nIndex++, form.getEndingValiditySqlDate());		
		daoUtil.setBoolean(nIndex++, form.isActive());
		return daoUtil;
	}

	/**
	 * Execute a safe update 
	 * (Free the connection in case of error when execute the query) 
	 * @param daoUtil the daoUtil
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
