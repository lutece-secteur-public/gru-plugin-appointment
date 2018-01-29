package fr.paris.lutece.plugins.appointment.business.category;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.UtilDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Category objects
 * 
 * @author Laurent Payen
 *
 */
public final class CategoryDAO extends UtilDAO implements ICategoryDAO
{

    private static final String SQL_QUERY_NEW_PK = "SELECT max(id_category) FROM appointment_category";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_category (id_category, label) VALUES (?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_category SET label = ? WHERE id_category = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_category WHERE id_category = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_category, label FROM appointment_category";
    private static final String SQL_QUERY_SELECT_ALL = SQL_QUERY_SELECT_COLUMNS;
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_category = ?";
    private static final String SQL_QUERY_SELECT_BY_LABEL = SQL_QUERY_SELECT_COLUMNS + " WHERE label = ?";

    @Override
    public synchronized void insert( Category category, Plugin plugin )
    {
        category.setIdCategory( getNewPrimaryKey( SQL_QUERY_NEW_PK, plugin ) );
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, category, plugin, true );
        executeUpdate( daoUtil );
    }

    @Override
    public void update( Category category, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, category, plugin, false );
        executeUpdate( daoUtil );
    }

    @Override
    public void delete( int nIdCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdCategory );
        executeUpdate( daoUtil );
    }

    @Override
    public Category select( int nIdCategory, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        Category category = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
            daoUtil.setInt( 1, nIdCategory );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                category = buildCategory( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return category;
    }

    @Override
    public List<Category> findAllCategories( Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<Category> listCategory = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listCategory.add( buildCategory( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listCategory;
    }

    @Override
    public Category findByLabel( String strLabel, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        Category category = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_LABEL, plugin );
            daoUtil.setString( 1, strLabel );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                category = buildCategory( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return category;
    }

    /**
     * Build a Category business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new display business object with all its attributes assigned
     */
    private Category buildCategory( DAOUtil daoUtil )
    {
        int nIndex = 1;
        Category category = new Category( );
        category.setIdCategory( daoUtil.getInt( nIndex++ ) );
        category.setLabel( daoUtil.getString( nIndex++ ) );
        return category;
    }

    /**
     * Build a daoUtil object with the display business object for insert query
     * 
     * @param query
     *            the query
     * @param category
     *            the category
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, Category category, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( query, plugin );
        if ( isInsert )
        {
            daoUtil.setInt( nIndex++, category.getIdCategory( ) );
        }
        daoUtil.setString( nIndex++, category.getLabel( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, category.getIdCategory( ) );
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
