package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class for the User
 * 
 * @author Laurent Payen
 *
 */
public class UserTest extends LuteceTestCase {

	private final static int ID_LUTECE_USER_1 = 1;
	private final static int ID_LUTECE_USER_2 = 2;
	private final static String FIRST_NAME_1 = "firstName1";
	private final static String FIRST_NAME_2 = "firstName2";
	private final static String LAST_NAME_1 = "lastName1";
	private final static String LAST_NAME_2 = "lastName2";
	private final static String EMAIL_1 = "email1";
	private final static String EMAIL_2 = "email2";
	private final static String PHONE_NUMBER_1 = "0605040302";
	private final static String PHONE_NUMBER_2 = "0605040303";

	/**
	 * Test method for the User (CRUD)
	 */
	public void testUser() {
		// Initialize a User
		User user = buildUser();
		// Insert the User in database
		UserHome.create(user);
		// Find the user created in database
		User userStored = UserHome.findByPrimaryKey(user.getIdUser());
		// Check Asserts
		checkAsserts(userStored, user);

		// Update the user
		user.setIdLuteceUser(ID_LUTECE_USER_2);
		user.setFirstName(FIRST_NAME_2);
		user.setLastName(LAST_NAME_2);
		user.setEmail(EMAIL_2);
		user.setPhoneNumber(PHONE_NUMBER_2);
		// Update the user in database
		UserHome.update(user);
		// Find the user updated in database
		userStored = UserHome.findByPrimaryKey(user.getIdUser());
		// Check Asserts
		checkAsserts(userStored, user);

		// Delete the user
		UserHome.delete(user.getIdUser());
		userStored = UserHome.findByPrimaryKey(user.getIdUser());
		// Check the user has been removed from database
		assertNull(userStored);
	}

	/**
	 * Build a User Business Object
	 * 
	 * @return the User
	 */
	public static User buildUser() {
		User user = new User();
		user.setIdLuteceUser(ID_LUTECE_USER_1);
		user.setFirstName(FIRST_NAME_1);
		user.setLastName(LAST_NAME_1);
		user.setEmail(EMAIL_1);
		user.setPhoneNumber(PHONE_NUMBER_1);
		return user;
	}

	/**
	 * Check that all the asserts are true
	 * 
	 * @param userStored
	 *            the user stored
	 * @param user
	 *            the user created
	 */
	public void checkAsserts(User userStored, User user) {
		assertEquals(userStored.getIdLuteceUser(), user.getIdLuteceUser());
		assertEquals(userStored.getFirstName(), user.getFirstName());
		assertEquals(userStored.getLastName(), user.getLastName());
		assertEquals(userStored.getEmail(), user.getEmail());
		assertEquals(userStored.getPhoneNumber(), user.getPhoneNumber());
	}
}
