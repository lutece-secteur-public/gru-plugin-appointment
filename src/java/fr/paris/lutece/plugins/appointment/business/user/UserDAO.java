package fr.paris.lutece.plugins.appointment.business.user;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.UtilDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for User objects
 * 
 * @author Laurent Payen
 *
 */
public final class UserDAO extends UtilDAO implements IUserDAO
{

    private static final String SQL_QUERY_NEW_PK = "SELECT max(id_user) FROM appointment_user";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_user (id_user, guid, first_name, last_name, email, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_user SET guid = ?, first_name = ?, last_name = ?, email = ?, phone_number = ? WHERE id_user = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_user WHERE id_user = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_user, guid, first_name, last_name, email, phone_number FROM appointment_user";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_user = ?";
    private static final String SQL_QUERY_SELECT_BY_EMAIL = SQL_QUERY_SELECT_COLUMNS + " WHERE email = ?";
    private static final String SQL_QUERY_SELECT_BY_FIRSTNAME_LASTNAME_AND_EMAIL = SQL_QUERY_SELECT_COLUMNS
            + " WHERE UPPER(first_name) = ? and UPPER(last_name) = ? and UPPER(email) = ?";

    @Override
    public synchronized void insert( User user, Plugin plugin )
    {
        user.setIdUser( getNewPrimaryKey( SQL_QUERY_NEW_PK, plugin ) );
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, user, plugin, true );
        executeUpdate( daoUtil );
    }

    @Override
    public void update( User user, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, user, plugin, false );
        executeUpdate( daoUtil );
    }

    @Override
    public void delete( int nIdUser, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdUser );
        executeUpdate( daoUtil );
    }

    @Override
    public User select( int nIdUser, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        User user = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
            daoUtil.setInt( 1, nIdUser );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                user = buildUser( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return user;
    }

    @Override
    public List<User> findByEmail( String strEmail, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<User> listUsers = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_EMAIL, plugin );
            daoUtil.setString( 1, strEmail );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
            	listUsers.add( buildUser( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listUsers;
    }

    @Override
    public User findByFirstNameLastNameAndEmail( String strFirstName, String strLastName, String strEmail, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        User user = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_FIRSTNAME_LASTNAME_AND_EMAIL, plugin );
            daoUtil.setString( 1, strFirstName.toUpperCase( ) );
            daoUtil.setString( 2, strLastName.toUpperCase( ) );
            daoUtil.setString( 3, strEmail.toUpperCase( ) );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                user = buildUser( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return user;
    }

    /**
     * Build a User business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new User with all its attributes assigned
     */
    private User buildUser( DAOUtil daoUtil )
    {
        int nIndex = 1;
        User user = new User( );
        user.setIdUser( daoUtil.getInt( nIndex++ ) );
        user.setGuid( daoUtil.getString( nIndex++ ) );
        user.setFirstName( daoUtil.getString( nIndex++ ) );
        user.setLastName( daoUtil.getString( nIndex++ ) );
        user.setEmail( daoUtil.getString( nIndex++ ) );
        user.setPhoneNumber( daoUtil.getString( nIndex ) );
        return user;
    }

    /**
     * Build a daoUtil object with the User business object
     * 
     * @param query
     *            the query
     * @param user
     *            the User
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, User user, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( query, plugin );
        if ( isInsert )
        {
            daoUtil.setInt( nIndex++, user.getIdUser( ) );
        }
        daoUtil.setString( nIndex++, user.getGuid( ) );
        daoUtil.setString( nIndex++, user.getFirstName( ) );
        daoUtil.setString( nIndex++, user.getLastName( ) );
        daoUtil.setString( nIndex++, user.getEmail( ) );
        daoUtil.setString( nIndex++, user.getPhoneNumber( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, user.getIdUser( ) );
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
