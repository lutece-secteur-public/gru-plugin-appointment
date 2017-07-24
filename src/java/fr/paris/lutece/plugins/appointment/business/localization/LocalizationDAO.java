package fr.paris.lutece.plugins.appointment.business.localization;

import fr.paris.lutece.plugins.appointment.business.UtilDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public final class LocalizationDAO extends UtilDAO implements ILocalizationDAO
{

    private static final String SQL_QUERY_NEW_PK = "SELECT max(id_localization) FROM appointment_localization";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_localization (id_localization, longitude, latitude, address, id_form) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_localization SET longitude = ?, latitude = ?, address = ?, id_form = ? WHERE id_localization = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_localization WHERE id_localization = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_localization, longitude, latitude, address, id_form FROM appointment_localization";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_localization = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";

    @Override
    public synchronized void insert( Localization localization, Plugin plugin )
    {
        localization.setIdLocalization( getNewPrimaryKey( SQL_QUERY_NEW_PK, plugin ) );
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, localization, plugin, true );
        executeUpdate( daoUtil );
    }

    @Override
    public void update( Localization localization, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, localization, plugin, false );
        executeUpdate( daoUtil );
    }

    @Override
    public void delete( int nIdLocalization, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdLocalization );
        executeUpdate( daoUtil );
    }

    @Override
    public Localization select( int nIdLocalization, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        Localization localization = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
            daoUtil.setInt( 1, nIdLocalization );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                localization = buildLocalization( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return localization;
    }

    @Override
    public Localization findByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        Localization localization = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                localization = buildLocalization( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return localization;
    }

    /**
     * Build a Localization business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new Localization business object with all its attributes assigned
     */
    private Localization buildLocalization( DAOUtil daoUtil )
    {
        int nIndex = 1;
        Localization localization = new Localization( );
        localization.setIdLocalization( daoUtil.getInt( nIndex++ ) );
        Float fLongitude = ( (Float) daoUtil.getObject( nIndex++ ) );
        if ( fLongitude != null )
        {
            localization.setLongitude( fLongitude.doubleValue( ) );
        }
        Float fLatitude = ( (Float) daoUtil.getObject( nIndex++ ) );
        if ( fLatitude != null )
        {
            localization.setLatitude( fLatitude.doubleValue( ) );
        }
        localization.setAddress( daoUtil.getString( nIndex++ ) );
        localization.setIdForm( daoUtil.getInt( nIndex ) );
        return localization;
    }

    /**
     * Build a daoUtil object with the localization business object for insert query
     * 
     * @param query
     *            the query
     * @param localization
     *            the localization
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, Localization localization, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( query, plugin );
        if ( isInsert )
        {
            daoUtil.setInt( nIndex++, localization.getIdLocalization( ) );
        }
        if ( localization.getLongitude( ) != null )
        {
            daoUtil.setDouble( nIndex++, localization.getLongitude( ) );
        }
        else
        {
            daoUtil.setDoubleNull( nIndex++ );
        }
        if ( localization.getLatitude( ) != null )
        {
            daoUtil.setDouble( nIndex++, localization.getLatitude( ) );
        }
        else
        {
            daoUtil.setDoubleNull( nIndex++ );
        }
        daoUtil.setString( nIndex++, localization.getAddress( ) );
        daoUtil.setInt( nIndex++, localization.getIdForm( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, localization.getIdLocalization( ) );
        }
        return daoUtil;
    }

    /**
     * Execute a safe update (Free the connection in case of error when execute the query)
     * 
     * @param daoUtil
     *            the daoUtil
     */
    private void executeUpdate( DAOUtil daoUtil )
    {
        try
        {
            daoUtil.executeUpdate( );
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
    }

}
