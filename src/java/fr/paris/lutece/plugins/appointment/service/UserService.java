package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.AppointmentFrontDTO;
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
	 * Save a user in database
	 * 
	 * @param appointment
	 *            the appointment DTO
	 * @return the user saved
	 */
	public static User saveUser(AppointmentFrontDTO appointment) {
		User user = new User();
		user.setFirstName(appointment.getFirstName());
		user.setLastName(appointment.getLastName());
		user.setEmail(appointment.getEmail());
		user = UserHome.create(user);
		return user;
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
