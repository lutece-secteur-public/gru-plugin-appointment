package fr.paris.lutece.plugins.appointment.business.display;

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Display objects
 * 
 * @author Laurent Payen
 *
 */
public class DisplayDAO implements IDisplayDAO {

	private static final String SQL_QUERY_NEW_PK = "SELECT max(id_display) FROM appointment_display";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_display (id_display, display_title_fo, icon_form_content, icon_form_mime_type, nb_weeks_to_display, id_calendar_template, id_form) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_display SET display_title_fo = ?, icon_form_content = ?, icon_form_mime_type = ?, nb_weeks_to_display = ?, id_calendar_template = ?, id_form = ? WHERE id_display = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_display WHERE id_display = ?";
	private static final String SQL_QUERY_SELECT = "SELECT id_display, display_title_fo, icon_form_content, icon_form_mime_type, nb_weeks_to_display, id_calendar_template, id_form FROM appointment_display WHERE id_display = ?";

	@Override
	public int getNewPrimaryKey(Plugin plugin) {
		DAOUtil daoUtil = null;
		int nKey = 1;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_NEW_PK, plugin);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				nKey = daoUtil.getInt(1) + 1;
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return nKey;
	}

	@Override
	public synchronized void insert(Display display, Plugin plugin) {
		display.setIdDisplay(getNewPrimaryKey(plugin));
		DAOUtil daoUtil = buildDaoUtilFromDisplay(SQL_QUERY_INSERT, display, plugin);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(Display display, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtilFromDisplay(SQL_QUERY_UPDATE, display, plugin);
		executeUpdate(daoUtil);
	}

	@Override
	public void delete(int nIdDisplay, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin);
		daoUtil.setInt(1, nIdDisplay);
		executeUpdate(daoUtil);
	}

	@Override
	public Display select(int nIdDisplay, Plugin plugin) {
		DAOUtil daoUtil = null;
		Display display = null;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT, plugin);
			daoUtil.setInt(1, nIdDisplay);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				display = buildDisplayFromDaoUtil(daoUtil);
			}
		} finally {
			daoUtil.free();
		}
		return display;
	}

	/**
	 * Build a Display business object from the resultset
	 * 
	 * @param daoUtil
	 *            the prepare statement util object
	 * @return a new display business object with all its attributes assigned
	 */
	private Display buildDisplayFromDaoUtil(DAOUtil daoUtil) {
		int nIndex = 1;
		Display display = new Display();
		display.setIdDisplay(daoUtil.getInt(nIndex++));
		display.setDisplayTitleFo(daoUtil.getBoolean(nIndex++));
		display.setIcon(buildIcon(daoUtil.getBytes(nIndex++), daoUtil.getString(nIndex++)));
		display.setNbWeeksToDisplay(daoUtil.getInt(nIndex++));
		display.setIdCalendarTemplate(daoUtil.getInt(nIndex++));
		display.setIdForm(daoUtil.getInt(nIndex));
		return display;
	}

	/**
	 * Build a daoUtil object with the display business object
	 * 
	 * @param query
	 *            the query
	 * @param display
	 *            the display
	 * @param plugin
	 *            the plugin
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtilFromDisplay(String query, Display display, Plugin plugin) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);
		daoUtil.setInt(nIndex++, display.getIdDisplay());
		daoUtil.setBoolean(nIndex++, display.isDisplayTitleFo());
		daoUtil.setBytes(nIndex++, display.getIcon().getImage());
		daoUtil.setString(nIndex++, display.getIcon().getMimeType());
		daoUtil.setInt(nIndex++, display.getIdCalendarTemplate());
		daoUtil.setInt(nIndex, display.getIdForm());
		return daoUtil;
	}

	/**
	 * Execute a safe update (Free the connection in case of error when execute
	 * the query)
	 * 
	 * @param daoUtil
	 *            the daoUtil
	 */
	private void executeUpdate(DAOUtil daoUtil) {
		try {
			daoUtil.executeUpdate();
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
	}

	/**
	 * Build an icon (imageResource)
	 * 
	 * @param strImage
	 *            the icon form content
	 * @param strMimeType
	 *            the icon form mime type
	 * @return the icon (imageResource) built
	 */
	private ImageResource buildIcon(byte[] strImage, String strMimeType) {
		ImageResource imageResource = new ImageResource();
		imageResource.setImage(strImage);
		imageResource.setMimeType(strMimeType);
		return imageResource;
	}

}
