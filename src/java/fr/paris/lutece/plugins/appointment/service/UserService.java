package fr.paris.lutece.plugins.appointment.service;

import fr.paris.lutece.plugins.appointment.business.AppointmentFrontDTO;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;

public class UserService {

	public static User saveUser(AppointmentFrontDTO appointment) {
		User user = new User();
		user.setFirstName(appointment.getFirstName());
		user.setLastName(appointment.getLastName());
		user.setEmail(appointment.getEmail());
		user = UserHome.create(user);
		return user;
	}
	
	public static User findUserByEmail(String strEmail) {
		return UserHome.findByEmail(strEmail);
	}

}
