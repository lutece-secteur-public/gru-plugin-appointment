package fr.paris.lutece.plugins.appointment.business.user;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;

/**
 * Business class of the User
 * 
 * @author Laurent Payen
 *
 */
public class User implements Serializable
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -5088753000751258184L;

    /**
     * User Id
     */
    private int _nIdUser;

    /**
     * GUID
     */
    private String _strGuid;

    /**
     * First name of the User
     */
    @NotBlank( message = "appointment.validation.appointment.FirstName.notEmpty" )
    @Size( max = 255, message = "appointment.validation.appointment.FirstName.size" )
    private String _strFirstName;

    /**
     * Last name of the User
     */
    @NotBlank( message = "appointment.validation.appointment.LastName.notEmpty" )
    @Size( max = 255, message = "appointment.validation.appointment.LastName.size" )
    private String _strLastName;

    /**
     * Email of the User (RFC 2822)
     */
    @Size( max = 255, message = "appointment.validation.appointment.Email.size" )
    @Email( regexp = "^$|(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", message = "appointment.validation.appointment.Email.email" )
    private String _strEmail;

    /**
     * Phone number of the User
     */
    private String _strPhoneNumber;

    /**
     * Appointments of the User
     */
    private List<Appointment> _listAppointments;

    /**
     * Get the Id of the User
     * 
     * @return the Id of the User
     */
    public int getIdUser( )
    {
        return _nIdUser;
    }

    /**
     * Set the Id of the User
     * 
     * @param nIdUser
     *            the Id to set
     */
    public void setIdUser( int nIdUser )
    {
        this._nIdUser = nIdUser;
    }

    /**
     * Get the Guid of the User
     * 
     * @return the Guid
     */
    public String getGuid( )
    {
        return _strGuid;
    }

    /**
     * Set the Guid
     * 
     * @param strGuid
     *            the Guid
     */
    public void setGuid( String strGuid )
    {
        this._strGuid = strGuid;
    }

    /**
     * Get the first name of the User
     * 
     * @return the first name of the User
     */
    public String getFirstName( )
    {
        return _strFirstName;
    }

    /**
     * Set the User first name
     * 
     * @param strFirstName
     *            the first name to set
     */
    public void setFirstName( String strFirstName )
    {
        this._strFirstName = strFirstName;
    }

    /**
     * Get the last name of the User
     * 
     * @return the last name of the USer
     */
    public String getLastName( )
    {
        return _strLastName;
    }

    /**
     * Set the last name of the User
     * 
     * @param strLastName
     *            the last name to set
     */
    public void setLastName( String strLastName )
    {
        this._strLastName = strLastName;
    }

    /**
     * Get the email of the User
     * 
     * @return the email of the User
     */
    public String getEmail( )
    {
        return _strEmail;
    }

    /**
     * Set the email of the User
     * 
     * @param strEmail
     *            the email to set
     */
    public void setEmail( String strEmail )
    {
        this._strEmail = strEmail;
    }

    /**
     * Get the phone number of the USer
     * 
     * @return the phone number of the User
     */
    public String getPhoneNumber( )
    {
        return _strPhoneNumber;
    }

    /**
     * Set the phone number of the User
     * 
     * @param strPhoneNumber
     *            the phone number to set
     */
    public void setPhoneNumber( String strPhoneNumber )
    {
        this._strPhoneNumber = strPhoneNumber;
    }

    /**
     * Get the appointments of the User
     * 
     * @return the list of the User appointments
     */
    public List<Appointment> getAppointments( )
    {
        return _listAppointments;
    }

    /**
     * Set the appointments of the User
     * 
     * @param listAppointments
     *            the appointments to set
     */
    public void setAppointments( List<Appointment> listAppointments )
    {
        this._listAppointments = listAppointments;
    }

}
