package fr.paris.lutece.plugins.appointment.business.appointment;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Appointment DAO Interface 
 * 
 * @author Laurent Payen
 *
 */
public interface IAppointmentDAO {

	/**
     * The name of the bean of the DAO
     */
    static String BEAN_NAME = "appointment.appointmentDAO";
    
	/**
     * Generate a new primary key
     * 
     * @param plugin
     *            the Plugin
     * @return the new primary key
     */
    int getNewPrimaryKey(Plugin plugin);
	
	/**
	 * Insert a new record in the table
	 * 
	 * @param appointment
	 *            instance of the appointment object to insert
	 * @param plugin
	 *            the plugin
	 */
	void insert(Appointment appointment, Plugin plugin);

	/**
	 * Update the record in the table
	 * 
	 * @param appointment
	 *            the reference of the appointment
	 * @param plugin
	 *            the plugin
	 */
	void update(Appointment appointment, Plugin plugin);

	/**
	 * Delete a record from the table
	 * 
	 * @param nIdAppointment
	 *            int identifier of the appointment to delete
	 * @param plugin
	 *            the plugin
	 */
	void delete(int nIdAppointment, Plugin plugin);

	/**
	 * Load the data from the table
	 * 
	 * @param nIdAppointment
	 *            the identifier of the appointment
	 * @param plugin
	 *            the plugin
	 * @return the instance of the appointment
	 */
	Appointment select(int nIdAppointment, Plugin plugin);
	
	/**
	 * Returns all the appointments of a user
	 * @param nIdUser the User Id
	 * @param plugin the Plugin
	 * @return a list of the appointments of the user
	 */
	List<Appointment> findByIdUser(int nIdUser, Plugin plugin);
		
	/**
	 * Returns the appointments of a slot
	 * @param nIdSlot the Slot Id
	 * @param plugin the plugin
	 * @return a list of the appointments
	 */
	List<Appointment> findByIdSlot(int nIdSlot, Plugin plugin);
}
