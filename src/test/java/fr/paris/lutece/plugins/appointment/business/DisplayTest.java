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

	private static final boolean DISPLAY_TITLE_FO_1 = true;
	private static final boolean DISPLAY_TITLE_FO_2 = false;
	private static final byte[] BYTES_1 = "BlaBlaBla".getBytes();
	private static final byte[] BYTES_2 = "BloBloBlo".getBytes();
	private static final String ICON_FORM_MIME_TYPE_1 = "ICON_FORM_MIME_TYPE_1";
	private static final String ICON_FORM_MIME_TYPE_2 = "ICON_FORM_MIME_TYPE_2";
	private static final int NB_WEEKS_TO_DISPLAY_1 = 10;
	private static final int NB_WEEKS_TO_DISPLAY_2 = 20;

	/**
	 * Test method for the Display (CRUD)
	 */
	public void testDisplay() {
		// Initialize a Display
		Display display = buildDisplay();
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

		Form form = FormTest.buildForm();
		FormHome.create(form);
		display.setIdForm(form.getIdForm());

		CalendarTemplate calendarTemplate = CalendarTemplateTest.buildCalendarTemplate();
		CalendarTemplateHome.create(calendarTemplate);
		display.setIdCalendarTemplate(calendarTemplate.getIdCalendarTemplate());

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
