package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;

/**
 * Service class of a user
 * 
 * @author Laurent Payen
 *
 */
public final class UserService
{

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private UserService( )
    {
    }

    /**
     * Save a user in database / A user is defined by its email (unique)
     * 
     * @param appointment
     *            the appointment DTO
     * @return the user saved
     */
    public static User saveUser( AppointmentDTO appointment )
    {
        String strFirstName = appointment.getFirstName( );
        String strLastName = appointment.getLastName( );
        String strEmail = appointment.getEmail( );
        User user = UserHome.findByFirstNameLastNameAndEmail( strFirstName, strLastName, strEmail );
        if ( user == null )
        {
            user = new User( );
            user.setGuid( appointment.getGuid( ) );
            user.setFirstName( strFirstName );
            user.setLastName( strLastName );
            user.setEmail( strEmail );
            user.setPhoneNumber( appointment.getPhoneNumber( ) );
            user = UserHome.create( user );
        }
        return user;
    }

    /**
     * Find a User by its primary key
     * 
     * @param nIdUser
     *            the primary key
     * @return the User found
     */
    public static User findUserById( int nIdUser )
    {
        return UserHome.findByPrimaryKey( nIdUser );
    }

    /**
     * Find a user by its first name, last name and email
     * 
     * @param strFirstName
     *            the first name
     * @param strLastName
     *            the last name
     * @param strEmail
     *            the email
     * @return the user
     */
    public static User findUserByFirstNameLastNameAndEmail( String strFirstName, String strLastName, String strEmail )
    {
        return UserHome.findByFirstNameLastNameAndEmail( strFirstName, strLastName, strEmail );
    }

}
