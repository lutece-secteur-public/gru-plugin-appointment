package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.plugins.appointment.business.template.CalendarTemplate;
import fr.paris.lutece.plugins.appointment.business.template.CalendarTemplateHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the CalendarTemplate
 * 
 * @author Laurent Payen
 *
 */
public class CalendarTemplateTest extends LuteceTestCase {

	private static final String TITLE_1 = "Title1";
	private static final String TITLE_2 = "Title2";
	private static final String DESCRIPTION_1 = "Description1";
	private static final String DESCRIPTION_2 = "Description2";
	private static final String TEMPLATE_PATH_1 = "TemplatePath1";
	private static final String TEMPLATE_PATH_2 = "TemplatePath2";

	/**
	 * Test method for the CalendarTemplate (CRUD)
	 */
	public void testCalendarTemplate() {
		// Initialize a CalendarTemplate
		CalendarTemplate calendarTemplate = buildCalendarTemplate();
		// Create the CalendarTemplate in database
		CalendarTemplateHome.create(calendarTemplate);
		// Find the CalendarTemplate created in database
		CalendarTemplate calendarTemplateStored = CalendarTemplateHome
				.findByPrimaryKey(calendarTemplate.getIdCalendarTemplate());
		// Check Asserts
		checkAsserts(calendarTemplateStored, calendarTemplate);

		// Update the CalendarTemplate
		calendarTemplate.setTitle(TITLE_2);
		calendarTemplate.setDescription(DESCRIPTION_2);
		calendarTemplate.setTemplatePath(TEMPLATE_PATH_2);
		// Update the CalendarTemplate in database
		CalendarTemplateHome.update(calendarTemplate);
		// Find the CalendarTemplate updated in database
		calendarTemplateStored = CalendarTemplateHome.findByPrimaryKey(calendarTemplate.getIdCalendarTemplate());
		// Check Asserts
		checkAsserts(calendarTemplateStored, calendarTemplate);

		// Delete the CalendarTemplate
		CalendarTemplateHome.delete(calendarTemplate.getIdCalendarTemplate());
		calendarTemplateStored = CalendarTemplateHome.findByPrimaryKey(calendarTemplate.getIdCalendarTemplate());
		// Check the CalendarTemplate has been removed from database
		assertNull(calendarTemplateStored);
	}

	/**
	 * Build a CalendarTemplate Business Object
	 * 
	 * @return the calendarTemplate
	 */
	public static CalendarTemplate buildCalendarTemplate() {
		CalendarTemplate calendarTemplate = new CalendarTemplate();
		calendarTemplate.setTitle(TITLE_1);
		calendarTemplate.setDescription(DESCRIPTION_1);
		calendarTemplate.setTemplatePath(TEMPLATE_PATH_1);

		return calendarTemplate;
	}

	/**
	 * Check that all the asserts are true
	 * 
	 * @param calendarTemplateStored
	 *            the CalendarTemplate stored
	 * @param calendarTemplate
	 *            the CalendarTemplate created
	 */
	public void checkAsserts(CalendarTemplate calendarTemplateStored, CalendarTemplate calendarTemplate) {
		assertEquals(calendarTemplateStored.getTitle(), calendarTemplate.getTitle());
		assertEquals(calendarTemplateStored.getDescription(), calendarTemplate.getDescription());
		assertEquals(calendarTemplateStored.getTemplatePath(), calendarTemplate.getTemplatePath());
	}

}
