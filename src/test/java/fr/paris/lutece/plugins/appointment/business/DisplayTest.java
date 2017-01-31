package fr.paris.lutece.plugins.appointment.business;

import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.display.DisplayHome;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.business.template.CalendarTemplate;
import fr.paris.lutece.plugins.appointment.business.template.CalendarTemplateHome;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test Class for the Display
 * 
 * @author Laurent Payen
 *
 */
public class DisplayTest extends LuteceTestCase {

	public static final boolean DISPLAY_TITLE_FO_1 = true;
	public static final boolean DISPLAY_TITLE_FO_2 = false;
	public static final byte[] BYTES_1 = "BlaBlaBla".getBytes();
	public static final byte[] BYTES_2 = "BloBloBlo".getBytes();
	public static final String ICON_FORM_MIME_TYPE_1 = "ICON_FORM_MIME_TYPE_1";
	public static final String ICON_FORM_MIME_TYPE_2 = "ICON_FORM_MIME_TYPE_2";
	public static final int NB_WEEKS_TO_DISPLAY_1 = 10;
	public static final int NB_WEEKS_TO_DISPLAY_2 = 20;

	/**
	 * Test method for the Display (CRUD)
	 */
	public void testDisplay() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		CalendarTemplate calendarTemplate = CalendarTemplateTest.buildCalendarTemplate();
		CalendarTemplateHome.create(calendarTemplate);

		// Initialize a Display
		Display display = buildDisplay();
		display.setIdForm(form.getIdForm());
		display.setIdCalendarTemplate(calendarTemplate.getIdCalendarTemplate());
		// Create the Display in database
		DisplayHome.create(display);
		// Find the Display created in database
		Display displayStored = DisplayHome.findByPrimaryKey(display.getIdDisplay());
		// Check Asserts
		checkAsserts(displayStored, display);

		// Update the Display
		display.setDisplayTitleFo(DISPLAY_TITLE_FO_2);
		ImageResource imageResource = new ImageResource();
		imageResource.setImage(BYTES_2);
		imageResource.setMimeType(ICON_FORM_MIME_TYPE_2);
		display.setIcon(imageResource);
		display.setNbWeeksToDisplay(NB_WEEKS_TO_DISPLAY_2);
		// Update the Display in database
		DisplayHome.update(display);
		// Find the Display updated in database
		displayStored = DisplayHome.findByPrimaryKey(display.getIdDisplay());
		// Check Asserts
		checkAsserts(displayStored, display);

		// Delete the Display
		DisplayHome.delete(display.getIdDisplay());
		displayStored = DisplayHome.findByPrimaryKey(display.getIdDisplay());
		// Check the Display has been removed from database
		assertNull(displayStored);

		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Test delete cascade
	 */
	public void testDeleteCascade() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		CalendarTemplate calendarTemplate = CalendarTemplateTest.buildCalendarTemplate();
		CalendarTemplateHome.create(calendarTemplate);

		// Initialize a Display
		Display display = buildDisplay();
		display.setIdForm(form.getIdForm());
		display.setIdCalendarTemplate(calendarTemplate.getIdCalendarTemplate());
		// Create the Display in database
		DisplayHome.create(display);
		// Find the Display created in database
		Display displayStored = DisplayHome.findByPrimaryKey(display.getIdDisplay());
		assertNotNull(displayStored);
		// Delete the Form and by cascade the Display
		FormHome.delete(form.getIdForm());
		displayStored = DisplayHome.findByPrimaryKey(display.getIdDisplay());
		// Check the Display has been removed from database
		assertNull(displayStored);
	}

	/**
	 * Test findByIdForm method
	 */
	public void testFindByIdForm() {
		Form form = FormTest.buildForm();
		FormHome.create(form);

		CalendarTemplate calendarTemplate = CalendarTemplateTest.buildCalendarTemplate();
		CalendarTemplateHome.create(calendarTemplate);

		// Initialize a Display
		Display display = buildDisplay();
		display.setIdForm(form.getIdForm());
		display.setIdCalendarTemplate(calendarTemplate.getIdCalendarTemplate());
		// Create the Display in database
		DisplayHome.create(display);
		// Find the Display created in database
		Display displayStored = DisplayHome.findByIdForm(form.getIdForm());
		// Check Asserts
		checkAsserts(displayStored, display);

		// Clean
		FormHome.delete(form.getIdForm());
	}

	/**
	 * Build a Display Business Object
	 * 
	 * @return the display
	 */
	public Display buildDisplay() {
		Display display = new Display();
		display.setDisplayTitleFo(DISPLAY_TITLE_FO_1);

		ImageResource img = new ImageResource();
		img.setImage(BYTES_1);
		img.setMimeType(ICON_FORM_MIME_TYPE_1);
		display.setIcon(img);

		display.setNbWeeksToDisplay(NB_WEEKS_TO_DISPLAY_1);
		return display;
	}

	/**
	 * Check that all the asserts are true
	 * 
	 * @param displayStored
	 *            the Display stored
	 * @param display
	 *            the Display created
	 */
	public void checkAsserts(Display displayStored, Display display) {
		assertEquals(displayStored.isDisplayTitleFo(), display.isDisplayTitleFo());
		assertEquals(displayStored.getIcon().getMimeType(), display.getIcon().getMimeType());
		assertEquals(displayStored.getIdCalendarTemplate(), display.getIdCalendarTemplate());
		assertEquals(displayStored.getIdForm(), display.getIdForm());
	}
}
