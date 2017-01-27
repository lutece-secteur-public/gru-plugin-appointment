package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.business.user.UserHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the Appointment
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentTest extends LuteceTestCase {

	/**
	 * Test method for the Appointment (CRUD)
	 */
	public void testAppointment() {
		// Initialize a Appointment
		Appointment appointment = buildAppointment();
		// Create the Appointment in database
		AppointmentHome.create(appointment);
		// Find the Appointment created in database
		Appointment appointmentStored = AppointmentHome.findByPrimaryKey(appointment.getIdAppointment());
		// Check Asserts
		checkAsserts(appointmentStored, appointment);

		// No possible update
		// An appointment is linked to a User and a Slot
		// It will be a nonsense to update the foreign keys (User or Slot).

		// Delete the Appointment
		AppointmentHome.delete(appointment.getIdAppointment());
		appointmentStored = AppointmentHome.findByPrimaryKey(appointment.getIdAppointment());
		// Check the Appointment has been removed from database
		assertNull(appointmentStored);
	}

	/**
	 * Build a Appointment Business Object
	 * 
	 * @return the appointment
	 */
	public Appointment buildAppointment() {
		Appointment appointment = new Appointment();

		User user = UserTest.buildUser();
		UserHome.create(user);
		appointment.setIdUser(user.getIdUser());

		Slot slot = SlotTest.buildSlot();
		SlotHome.create(slot);
		appointment.setIdSlot(slot.getIdSlot());

		return appointment;
	}

	/**
	 * Check that all the asserts are true
	 * 
	 * @param appointmentStored
	 *            the Appointment stored
	 * @param appointment
	 *            the Appointment created
	 */
	public void checkAsserts(Appointment appointmentStored, Appointment appointment) {
		assertEquals(appointmentStored.getIdSlot(), appointment.getIdSlot());
		assertEquals(appointmentStored.getIdUser(), appointment.getIdUser());
	}
}
