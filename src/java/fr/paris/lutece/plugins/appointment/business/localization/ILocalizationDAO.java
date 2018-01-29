package fr.paris.lutece.plugins.appointment.business.localization;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Localization DAO Interface
 * 
 * @author Laurent Payen
 *
 */
public interface ILocalizationDAO
{

    /**
     * The name of the bean of the DAO
     */
    static String BEAN_NAME = "appointment.localizationDAO";

    /**
     * Insert a new record in the table.
     * 
     * @param localization
     *            instance of the Localization object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( Localization localization, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param localization
     *            the reference of the Localization
     * @param plugin
     *            the Plugin
     */
    void update( Localization localization, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdLocalization
     *            identifier of the Localization to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nIdLocalization, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdLocalization
     *            The identifier of the Localization
     * @param plugin
     *            the Plugin
     * @return The instance of the Localization
     */
    Localization select( int nIdLocalization, Plugin plugin );

    /**
     * Returns the Localization of the given form
     * 
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the plugin
     * @return the form Localization
     */
    Localization findByIdForm( int nIdForm, Plugin plugin );
}
