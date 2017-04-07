package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;

/**
 * Service class of a user
 * 
 * @author Laurent Payen
 *
 */
public class UserService {

	/**
	 * Save a user in database / A user is defined by its email (unique)
	 * 
	 * @param appointment
	 *            the appointment DTO
	 * @return the user saved
	 */
	public static User saveUser(AppointmentDTO appointment) {	
		User user = new User();
		user.setFirstName(appointment.getFirstName());
		user.setLastName(appointment.getLastName());
		user.setEmail(appointment.getEmail());
		User userFromDb = UserHome.findByEmail(user.getEmail()); 
		if (userFromDb == null){
			user = UserHome.create(user);
		} else {
			// TODO need to update the users infos ?
			userFromDb.setFirstName(appointment.getFirstName());
			userFromDb.setLastName(appointment.getLastName());
			user = UserHome.update(userFromDb);					
		}		
		return user;
	}

	/**
	 * Find a User by its primary key
	 * @param nIdUser the primary key
	 * @return the User found
	 */
	public static User findUserById(int nIdUser){
		return UserHome.findByPrimaryKey(nIdUser);
	}
	/**
	 * Find a user with its email
	 * 
	 * @param strEmail
	 *            the email of the user
	 * @return the user
	 */
	public static User findUserByEmail(String strEmail) {
		return UserHome.findByEmail(strEmail);
	}

}
