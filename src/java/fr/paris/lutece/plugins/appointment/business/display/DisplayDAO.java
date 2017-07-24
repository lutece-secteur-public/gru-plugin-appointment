package fr.paris.lutece.plugins.appointment.business.display;

import fr.paris.lutece.plugins.appointment.business.UtilDAO;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Display objects
 * 
 * @author Laurent Payen
 *
 */
public final class DisplayDAO extends UtilDAO implements IDisplayDAO
{

    private static final String SQL_QUERY_NEW_PK = "SELECT max(id_display) FROM appointment_display";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_display (id_display, display_title_fo, icon_form_content, icon_form_mime_type, nb_weeks_to_display, id_calendar_template, id_form) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_display SET display_title_fo = ?, icon_form_content = ?, icon_form_mime_type = ?, nb_weeks_to_display = ?, id_calendar_template = ?, id_form = ? WHERE id_display = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_display WHERE id_display = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_display, display_title_fo, icon_form_content, icon_form_mime_type, nb_weeks_to_display, id_calendar_template, id_form FROM appointment_display";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_display = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";   

    @Override
    public synchronized void insert( Display display, Plugin plugin )
    {
        display.setIdDisplay( getNewPrimaryKey( SQL_QUERY_NEW_PK, plugin ) );
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, display, plugin, true );
        executeUpdate( daoUtil );
    }

    @Override
    public void update( Display display, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, display, plugin, false );
        executeUpdate( daoUtil );
    }

    @Override
    public void delete( int nIdDisplay, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdDisplay );
        executeUpdate( daoUtil );
    }

    @Override
    public Display select( int nIdDisplay, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        Display display = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
            daoUtil.setInt( 1, nIdDisplay );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                display = buildDisplay( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return display;
    }

    @Override
    public Display findByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        Display display = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                display = buildDisplay( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return display;
    }

    /**
     * Build a Display business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new display business object with all its attributes assigned
     */
    private Display buildDisplay( DAOUtil daoUtil )
    {
        int nIndex = 1;
        Display display = new Display( );
        display.setIdDisplay( daoUtil.getInt( nIndex++ ) );
        display.setDisplayTitleFo( daoUtil.getBoolean( nIndex++ ) );
        display.setIcon( buildIcon( daoUtil.getBytes( nIndex++ ), daoUtil.getString( nIndex++ ) ) );
        display.setNbWeeksToDisplay( daoUtil.getInt( nIndex++ ) );
        display.setIdCalendarTemplate( daoUtil.getInt( nIndex++ ) );
        display.setIdForm( daoUtil.getInt( nIndex ) );
        return display;
    }

    /**
     * Build a daoUtil object with the display business object for insert query
     * 
     * @param query
     *            the query
     * @param display
     *            the display
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, Display display, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( query, plugin );
        if ( isInsert )
        {
            daoUtil.setInt( nIndex++, display.getIdDisplay( ) );
        }
        daoUtil.setBoolean( nIndex++, display.isDisplayTitleFo( ) );
        daoUtil.setBytes( nIndex++, display.getIcon( ).getImage( ) );
        daoUtil.setString( nIndex++, display.getIcon( ).getMimeType( ) );
        daoUtil.setInt( nIndex++, display.getNbWeeksToDisplay( ) );
        daoUtil.setInt( nIndex++, display.getIdCalendarTemplate( ) );
        daoUtil.setInt( nIndex++, display.getIdForm( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, display.getIdDisplay( ) );
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

    /**
     * Build an icon (imageResource)
     * 
     * @param strImage
     *            the icon form content
     * @param strMimeType
     *            the icon form mime type
     * @return the icon (imageResource) built
     */
    private ImageResource buildIcon( byte [ ] strImage, String strMimeType )
    {
        ImageResource imageResource = new ImageResource( );
        imageResource.setImage( strImage );
        imageResource.setMimeType( strMimeType );
        return imageResource;
    }

}
