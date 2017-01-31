package fr.paris.lutece.plugins.appointment.business.rule;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * FormRule DAO Interface
 * @author Laurent Payen
 *
 */
public interface IFormRuleDAO {
	
	/**
     * The name of the bean of the DAO
     */
    static String BEAN_NAME = "appointment.formRuleDAO";
    
	/**
     * Generate a new primary key
     * 
     * @param plugin
     *            the Plugin
     * @return the new primary key
     */
    int getNewPrimaryKey(Plugin plugin);
    
	/**
	 * Insert a new record in the table
	 * 
	 * @param formRule
	 *            instance of the FormRule object to insert
	 * @param plugin
	 *            the plugin
	 */
	void insert(FormRule formRule, Plugin plugin);

	/**
	 * Update the record in the table
	 * 
	 * @param formRule
	 *            the reference of the FormRule
	 * @param plugin
	 *            the plugin
	 */
	void update(FormRule formRule, Plugin plugin);

	/**
	 * Delete a record from the table
	 * 
	 * @param nIdFormRule
	 *            identifier of the FormRule to delete
	 * @param plugin
	 *            the plugin
	 */
	void delete(int nIdFormRule, Plugin plugin);

	/**
	 * Load the data from the table
	 * 
	 * @param nIdFormRule
	 *            the identifier of the FormRule
	 * @param plugin
	 *            the plugin
	 * @return the instance of the FormRule
	 */
	FormRule select(int nIdFormRule, Plugin plugin);
	
	/**
	 * Returns the form rule of a form
	 * @param nIdForm the form id
	 * @param plugin the plugin
	 * @return the form rule of the form
	 */
	FormRule findByIdForm(int nIdForm, Plugin plugin);
}
