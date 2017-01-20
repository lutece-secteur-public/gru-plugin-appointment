package fr.paris.lutece.plugins.appointment.business.slot;

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
	private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_slot (id_slot, starting_date, ending_date, is_open, max_capacity, id_form) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE appointment_slot SET starting_date = ?, ending_date = ?, is_open = ?, max_capacity = ?, id_form = ? WHERE id_slot = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_slot WHERE id_slot = ?";
	private static final String SQL_QUERY_SELECT = "SELECT id_slot, starting_date, ending_date, is_open, max_capacity, id_form FROM appointment_slot WHERE id_slot = ?";

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
		DAOUtil daoUtil = buildDaoUtilFromSlot(SQL_QUERY_INSERT, slot, plugin);
		executeUpdate(daoUtil);
	}

	@Override
	public void update(Slot slot, Plugin plugin) {
		DAOUtil daoUtil = buildDaoUtilFromSlot(SQL_QUERY_UPDATE, slot, plugin);
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
				slot = buildSlotFromDaoUtil(daoUtil);
			}
		} finally {
			daoUtil.free();
		}
		return slot;
	}

	/**
	 * Build a Slot business object from the resultset
	 * 
	 * @param daoUtil
	 *            the prepare statement util object
	 * @return a new Slot with all its attributes assigned
	 */
	private Slot buildSlotFromDaoUtil(DAOUtil daoUtil) {
		int nIndex = 1;
		Slot slot = new Slot();
		slot.setIdSlot(daoUtil.getInt(nIndex++));
		slot.setStartingDate(daoUtil.getTimestamp(nIndex++));
		slot.setEndingDate(daoUtil.getTimestamp(nIndex++));
		slot.setIsOpen(daoUtil.getBoolean(nIndex++));
		slot.setMaxCapacityPerSlot(daoUtil.getInt(nIndex++));
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
	 * @return a new daoUtil with all its values assigned
	 */
	private DAOUtil buildDaoUtilFromSlot(String query, Slot slot, Plugin plugin) {
		int nIndex = 1;
		DAOUtil daoUtil = new DAOUtil(query, plugin);
		daoUtil.setInt(nIndex++, slot.getIdSlot());
		daoUtil.setTimestamp(nIndex++, slot.getStartingTimestampDate());
		daoUtil.setTimestamp(nIndex++, slot.getEndingTimestampDate());
		daoUtil.setBoolean(nIndex++, slot.isOpen());
		daoUtil.setInt(nIndex++, slot.getMaxCapacityPerSlot());
		daoUtil.setInt(nIndex, slot.getIdForm());
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
