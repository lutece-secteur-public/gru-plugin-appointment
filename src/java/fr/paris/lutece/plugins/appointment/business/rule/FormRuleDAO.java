package fr.paris.lutece.plugins.appointment.business.rule;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form Rule objects
 * 
 * @author Laurent Payen
 *
 */
public class FormRuleDAO implements IFormRuleDAO {

	private static final String SQL_QUERY_NEW_PK = "SELECT max(id_form_rule) FROM appointment_form_rule";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_form_rule (id_form_rule, is_captcha_enabled, is_mandatory_email_enabled, id_form) VALUES (?, ?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_form_rule SET is_captcha_enabled = ?, is_mandatory_email_enabled = ?, id_form = ? WHERE id_form_rule = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_form_rule WHERE id_form_rule = ?";
	private static final String SQL_QUERY_SELECT = "SELECT id_form_rule, is_captcha_enabled, is_mandatory_email_enabled, id_form FROM appointment_form_rule WHERE id_form_rulet = ?";

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
	public synchronized void insert(FormRule formRule, Plugin plugin) {
		formRule.setIdFormRule(getNewPrimaryKey(plugin));
		DAOUtil daoUtil = buildDaoUtilFromFormRule(SQL_QUERY_INSERT, formRule, plugin);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(FormRule formRule, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtilFromFormRule(SQL_QUERY_UPDATE, formRule, plugin);
		executeUpdate(daoUtil);
	}

	@Override
	public void delete(int nIdFormRule, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin);
		daoUtil.setInt(1, nIdFormRule);
		executeUpdate(daoUtil);
	}

	@Override
	public FormRule select(int nIdFormRule, Plugin plugin) {
		DAOUtil daoUtil = null;
		FormRule formRule = null;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT, plugin);
			daoUtil.setInt(1, nIdFormRule);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				formRule = buildFormRuleFromDaoUtil(daoUtil);
			}
		} finally {
			daoUtil.free();
		}
		return formRule;
	}

	/**
	 * Build a Form rule business object from the resultset
	 * 
	 * @param daoUtil
	 *            the prepare statement util object
	 * @return a new formRule with all its attributes assigned
	 */
	private FormRule buildFormRuleFromDaoUtil(DAOUtil daoUtil) {
		int nIndex = 1;
		FormRule formRule = new FormRule();
		formRule.setIdFormRule(daoUtil.getInt(nIndex++));
		formRule.setIsCaptchaEnabled(daoUtil.getBoolean(nIndex++));
		formRule.setIsMandatoryEmailEnabled(daoUtil.getBoolean(nIndex++));
		formRule.setIdForm(daoUtil.getInt(nIndex));
		return formRule;
	}

	/**
	 * Build a daoUtil object with the FormRule business object
	 * 
	 * @param query
	 *            the query
	 * @param formRule
	 *            the FormRule
	 * @param plugin
	 *            the plugin
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtilFromFormRule(String query, FormRule formRule, Plugin plugin) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);
		daoUtil.setInt(nIndex++, formRule.getIdFormRule());
		daoUtil.setBoolean(nIndex++, formRule.isCaptchaEnabled());
		daoUtil.setBoolean(nIndex++, formRule.isMandatoryEmailEnabled());
		daoUtil.setInt(nIndex, formRule.getIdForm());
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