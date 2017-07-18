package fr.paris.lutece.plugins.appointment.business.form;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Form DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface IFormDAO
{

    /**
     * The name of the bean of the DAO
     */
    static String BEAN_NAME = "appointment.formDAO";

    /**
     * Generate a new primary key
     * 
     * @param plugin
     *            the Plugin
     * @return the new primary key
     */
    int getNewPrimaryKey( Plugin plugin );

    /**
     * 
     * Insert a new record in the table
     * 
     * @param form
     *            instance of the form object to insert
     * @param plugin
     *            the plugin
     */
    void insert( Form form, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param form
     *            the reference of the form
     * @param plugin
     *            the plugin
     */
    void update( Form form, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdFrom
     *            identifier of the form to delete
     * @param plugin
     *            the plugin
     */
    void delete( int nIdForm, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdForm
     *            the identifier of the form
     * @param plugin
     *            the plugin
     * @return the instance of the Form
     */
    Form select( int nIdForm, Plugin plugin );

    /**
     * Get all the forms that are active
     * 
     * @param plugin
     *            the plugin
     * @return all the active forms
     */
    List<Form> findActiveForms( Plugin plugin );

    /**
     * Get all the forms
     * 
     * @param plugin
     *            the plugin
     * @return all the forms
     */
    List<Form> findAllForms( Plugin plugin );

}
