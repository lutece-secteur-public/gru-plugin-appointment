package fr.paris.lutece.plugins.appointment.business.localization;

import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Localization objects
 * 
 * @author Laurent Payen
 *
 */
public final class LocalizationHome
{

    // Static variable pointed at the DAO instance
    private static ILocalizationDAO _dao = SpringContextService.getBean( ILocalizationDAO.BEAN_NAME );
    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private LocalizationHome( )
    {
    }

    /**
     * Create an instance of the Localization class
     * 
     * @param localization
     *            The instance of the Localization which contains the informations to store
     * @return The instance of Localization which has been created with its primary key.
     */
    public static Localization create( Localization localization )
    {
        _dao.insert( localization, _plugin );

        return localization;
    }

    /**
     * Update of the Localization which is specified in parameter
     * 
     * @param localization
     *            The instance of the Localization which contains the data to store
     * @return The instance of the Localization which has been updated
     */
    public static Localization update( Localization localization )
    {
        _dao.update( localization, _plugin );

        return localization;
    }

    /**
     * Delete the Localization whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Localization Id
     */
    public static void delete( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of the Localization whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Localization primary key
     * @return an instance of the Localization
     */
    public static Localization findByPrimaryKey( int nKey )
    {
        return _dao.select( nKey, _plugin );
    }

    /**
     * Returns the form Localization
     * 
     * @param nIdForm
     *            the form id
     * @return the form Localization
     */
    public static Localization findByIdForm( int nIdForm )
    {
        return _dao.findByIdForm( nIdForm, _plugin );
    }

}
