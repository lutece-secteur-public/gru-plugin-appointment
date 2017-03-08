package fr.paris.lutece.plugins.appointment.business.slot;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Slot objects
 * 
 * @author Laurent Payen
 *
 */
public class SlotDAO implements ISlotDAO {

	private static final String SQL_QUERY_NEW_PK = "SELECT max(id_slot) FROM appointment_slot";
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_slot (id_slot, starting_date_time, ending_date_time, is_open, max_capacity, nb_remaining_places, id_form) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_slot SET starting_date_time = ?, ending_date_time = ?, is_open = ?, max_capacity = ?, nb_remaining_places = ?, id_form = ? WHERE id_slot = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_slot WHERE id_slot = ?";
	private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_slot, starting_date_time, ending_date_time, is_open, max_capacity, nb_remaining_places, id_form FROM appointment_slot";
	private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_slot = ?";
	private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_RANGE = SQL_QUERY_SELECT_COLUMNS
			+ " WHERE id_form = ? AND starting_date_time >= ? AND ending_date_time <= ?";
	private static final String SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM_AND_DATE_RANGE = SQL_QUERY_SELECT_COLUMNS
			+ " WHERE id_form = ? AND starting_date_time >= ? AND ending_date_time <= ? AND is_open = 1";
	private static final String SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS
			+ " WHERE id_form = ? AND is_open = 1";

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
	public synchronized void insert(Slot slot, Plugin plugin) {
		slot.setIdSlot(getNewPrimaryKey(plugin));
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_INSERT, slot, plugin, true);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(Slot slot, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtil(SQL_QUERY_UPDATE, slot, plugin, false);
		executeUpdate(daoUtil);
	}

	@Override
	public void delete(int nIdSlot, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin);
		daoUtil.setInt(1, nIdSlot);
		executeUpdate(daoUtil);
	}

	@Override
	public Slot select(int nIdSlot, Plugin plugin) {
		DAOUtil daoUtil = null;
		Slot slot = null;
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT, plugin);
			daoUtil.setInt(1, nIdSlot);
			daoUtil.executeQuery();
			if (daoUtil.next()) {
				slot = buildSlot(daoUtil);
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return slot;
	}

	@Override
	public HashMap<LocalDateTime, Slot> findByIdFormAndDateRange(int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime,
			Plugin plugin) {
		DAOUtil daoUtil = null;
		HashMap<LocalDateTime, Slot> mapSlot = new HashMap<>();
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_RANGE, plugin);
			daoUtil.setInt(1, nIdForm);
			daoUtil.setTimestamp(2, Timestamp.valueOf(startingDateTime));
			daoUtil.setTimestamp(3, Timestamp.valueOf(endingDateTime));
			daoUtil.executeQuery();
			Slot slotToPut;
			while (daoUtil.next()) {
				slotToPut = buildSlot(daoUtil);
				mapSlot.put(slotToPut.getStartingDateTime(), slotToPut);
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return mapSlot;
	}
	
	@Override
	public List<Slot> findOpenSlotsByIdFormAndDateRange(int nIdForm, LocalDateTime startingDateTime,
			LocalDateTime endingDateTime, Plugin plugin) {
		DAOUtil daoUtil = null;
		List<Slot> listSLot = new ArrayList<>();
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM_AND_DATE_RANGE, plugin);
			daoUtil.setInt(1, nIdForm);
			daoUtil.setTimestamp(2, Timestamp.valueOf(startingDateTime));
			daoUtil.setTimestamp(3, Timestamp.valueOf(endingDateTime));
			daoUtil.executeQuery();
			while (daoUtil.next()) {
				listSLot.add(buildSlot(daoUtil));
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return listSLot;
	}

	@Override
	public List<Slot> findOpenSlotsByIdForm(int nIdForm, Plugin plugin) {
		DAOUtil daoUtil = null;
		List<Slot> listSLot = new ArrayList<>();
		try {
			daoUtil = new DAOUtil(SQL_QUERY_SELECT_OPEN_SLOTS_BY_ID_FORM, plugin);
			daoUtil.setInt(1, nIdForm);
			daoUtil.executeQuery();
			while (daoUtil.next()) {
				listSLot.add(buildSlot(daoUtil));
			}
		} finally {
			if (daoUtil != null) {
				daoUtil.free();
			}
		}
		return listSLot;
	}

	/**
	 * Build a Slot business object from the resultset
	 * 
	 * @param daoUtil
	 *            the prepare statement util object
	 * @return a new Slot with all its attributes assigned
	 */
	private Slot buildSlot(DAOUtil daoUtil) {
		int nIndex = 1;
		Slot slot = new Slot();
		slot.setIdSlot(daoUtil.getInt(nIndex++));
		slot.setStartingTimeStampDate(daoUtil.getTimestamp(nIndex++));
		slot.setEndingTimeStampDate(daoUtil.getTimestamp(nIndex++));
		slot.setIsOpen(daoUtil.getBoolean(nIndex++));
		slot.setMaxCapacity(daoUtil.getInt(nIndex++));
		slot.setNbRemainingPlaces(daoUtil.getInt(nIndex++));
		slot.setIdForm(daoUtil.getInt(nIndex));
		return slot;
	}

	/**
	 * Build a daoUtil object with the Slot business object
	 * 
	 * @param query
	 *            the query
	 * @param slot
	 *            the SLot
	 * @param plugin
	 *            the plugin
	 * @param isInsert
	 *            true if it is an insert query (in this case, need to set the
	 *            id). If false, it is an update, in this case, there is a where
	 *            parameter id to set
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtil(String query, Slot slot, Plugin plugin, boolean isInsert) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);
		if (isInsert) {
			daoUtil.setInt(nIndex++, slot.getIdSlot());
		}
		daoUtil.setTimestamp(nIndex++, slot.getStartingTimestampDate());
		daoUtil.setTimestamp(nIndex++, slot.getEndingTimestampDate());
		daoUtil.setBoolean(nIndex++, slot.getIsOpen());
		daoUtil.setInt(nIndex++, slot.getMaxCapacity());
		daoUtil.setInt(nIndex++, slot.getNbRemainingPlaces());
		daoUtil.setInt(nIndex++, slot.getIdForm());
		if (!isInsert) {
			daoUtil.setInt(nIndex, slot.getIdSlot());
		}
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

}
