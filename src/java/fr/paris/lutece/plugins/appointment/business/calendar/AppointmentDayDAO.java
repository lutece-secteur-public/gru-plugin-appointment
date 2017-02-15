/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.business.calendar;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * DAO form appointment days
 */
public class AppointmentDayDAO implements IAppointmentDayDAO {
	private static final String NEW_PRIMARY_KEY = "SELECT MAX(id_day) FROM appointment_day";
	private static final String SQL_QUERY_CREATE_DAY = "INSERT INTO appointment_day (id_day, id_form, is_open, date_day, opening_hour, opening_minute, closing_hour, closing_minute, appointment_duration, max_capacity_per_slot, free_places) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_QUERY_UPDATE_DAY = "UPDATE appointment_day SET is_open = ?, date_day = ?, opening_hour = ?, opening_minute = ?, closing_hour = ?, closing_minute = ?, appointment_duration = ?, max_capacity_per_slot = ?, free_places = ? WHERE id_day = ?";
	private static final String SQL_QUERY_UPDATE_DAY_FREE_PLACES = "UPDATE appointment_day SET free_places = ? WHERE id_day = ?";
	private static final String SQL_QUERY_REMOVE_DAY_BY_PRIMARY_KEY = "DELETE FROM appointment_day WHERE id_day = ?";
	private static final String SQL_QUERY_REMOVE_DAY_BY_ID_DAY = "DELETE FROM appointment_day WHERE id_form = ?";
	private static final String SQL_QUERY_REMOVE_LONELY_DAYS = " DELETE FROM appointment_day WHERE date_day < ? AND id_day NOT IN (SELECT DISTINCT(id_day) FROM appointment_slot) ";
	private static final String SQL_QUERY_SELECT_DAY = "SELECT id_day, id_form, is_open, date_day, opening_hour, opening_minute, closing_hour, closing_minute, appointment_duration, max_capacity_per_slot, free_places FROM appointment_day ";
	private static final String SQL_QUERY_SELECT_DAY_BY_PRIMARY_KEY = SQL_QUERY_SELECT_DAY + " WHERE id_day = ?";
	private static final String SQL_QUERY_SELECT_DAY_BETWEEN = SQL_QUERY_SELECT_DAY
			+ " WHERE id_form = ? AND date_day >= ? AND date_day <= ? ORDER BY date_day ASC ";
	private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_DAY
			+ " WHERE id_form = ? ORDER BY date_day ASC";

	/**
	 * Get a new primary key for a day
	 * 
	 * @param plugin
	 *            The plugin
	 * @return The new value of the primary key
	 */
	private int newPrimaryKey(Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(NEW_PRIMARY_KEY, plugin);
		daoUtil.executeQuery();

		int nKey = 1;

		if (daoUtil.next()) {
			nKey = daoUtil.getInt(1) + 1;
		}

		daoUtil.free();

		return nKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void create(AppointmentDay day, Plugin plugin) {
		day.setIdDay(newPrimaryKey(plugin));

		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_CREATE_DAY, plugin);
		int nIndex = 1;
		daoUtil.setInt(nIndex++, day.getIdDay());
		daoUtil.setInt(nIndex++, day.getIdForm());
		daoUtil.setBoolean(nIndex++, day.getIsOpen());
		daoUtil.setDate(nIndex++, day.getDate());
		daoUtil.setInt(nIndex++, day.getOpeningHour());
		daoUtil.setInt(nIndex++, day.getOpeningMinutes());
		daoUtil.setInt(nIndex++, day.getClosingHour());
		daoUtil.setInt(nIndex++, day.getClosingMinutes());
		daoUtil.setInt(nIndex++, day.getAppointmentDuration());
		daoUtil.setInt(nIndex++, day.getPeoplePerAppointment());
		daoUtil.setInt(nIndex, day.getFreePlaces());

		daoUtil.executeUpdate();
		daoUtil.free();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(AppointmentDay day, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_UPDATE_DAY, plugin);
		int nIndex = 1;
		daoUtil.setBoolean(nIndex++, day.getIsOpen());
		daoUtil.setDate(nIndex++, day.getDate());
		daoUtil.setInt(nIndex++, day.getOpeningHour());
		daoUtil.setInt(nIndex++, day.getOpeningMinutes());
		daoUtil.setInt(nIndex++, day.getClosingHour());
		daoUtil.setInt(nIndex++, day.getClosingMinutes());
		daoUtil.setInt(nIndex++, day.getAppointmentDuration());
		daoUtil.setInt(nIndex++, day.getPeoplePerAppointment());
		daoUtil.setInt(nIndex++, day.getFreePlaces());
		daoUtil.setInt(nIndex, day.getIdDay());

		daoUtil.executeUpdate();
		daoUtil.free();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(int nIdDay, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_REMOVE_DAY_BY_PRIMARY_KEY, plugin);
		daoUtil.setInt(1, nIdDay);
		daoUtil.executeUpdate();
		daoUtil.free();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeByIdForm(int nIdForm, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_REMOVE_DAY_BY_ID_DAY, plugin);
		daoUtil.setInt(1, nIdForm);
		daoUtil.executeUpdate();
		daoUtil.free();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeLonelyDays(Date dateMonday, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_REMOVE_LONELY_DAYS, plugin);
		daoUtil.setDate(1, dateMonday);
		daoUtil.executeUpdate();
		daoUtil.free();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AppointmentDay findByPrimaryKey(int nKey, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_SELECT_DAY_BY_PRIMARY_KEY, plugin);
		daoUtil.setInt(1, nKey);
		daoUtil.executeQuery();

		AppointmentDay day = null;

		if (daoUtil.next()) {
			day = getDayFromDAO(daoUtil);
		}

		daoUtil.free();

		return day;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AppointmentDay> getDaysBetween(int nIdForm, Date dateMin, Date dateMax, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_SELECT_DAY_BETWEEN, plugin);
		daoUtil.setInt(1, nIdForm);
		daoUtil.setDate(2, dateMin);
		daoUtil.setDate(3, dateMax);
		daoUtil.executeQuery();

		List<AppointmentDay> listDays = new ArrayList<AppointmentDay>();

		while (daoUtil.next()) {
			listDays.add(getDayFromDAO(daoUtil));
		}

		daoUtil.free();

		return listDays;
	}

	/**
	 * Get data of a day from a DAO. The DAO will NOT be freed by this method,
	 * and the method {@link DAOUtil#next()} must have been called before this
	 * method.
	 * 
	 * @param daoUtil
	 *            The DAOUtil to read data from
	 * @return The appointment day found.
	 */
	private AppointmentDay getDayFromDAO(DAOUtil daoUtil) {
		AppointmentDay day = new AppointmentDay();
		int nIndex = 1;
		day.setIdDay(daoUtil.getInt(nIndex++));
		day.setIdForm(daoUtil.getInt(nIndex++));
		day.setIsOpen(daoUtil.getBoolean(nIndex++));
		day.setDate(daoUtil.getDate(nIndex++));
		day.setOpeningHour(daoUtil.getInt(nIndex++));
		day.setOpeningMinutes(daoUtil.getInt(nIndex++));
		day.setClosingHour(daoUtil.getInt(nIndex++));
		day.setClosingMinutes(daoUtil.getInt(nIndex++));
		day.setAppointmentDuration(daoUtil.getInt(nIndex++));
		day.setPeoplePerAppointment(daoUtil.getInt(nIndex++));
		day.setFreePlaces(daoUtil.getInt(nIndex));

		return day;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateDayFreePlaces(AppointmentDay day, boolean bIncrement, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_UPDATE_DAY_FREE_PLACES, plugin);

		if (bIncrement) {
			day.setFreePlaces(day.getFreePlaces() + 1);
		} else {
			if (day.getFreePlaces() > 0) {
				day.setFreePlaces(day.getFreePlaces() - 1);
			}
		}

		daoUtil.setInt(1, day.getFreePlaces());
		daoUtil.setInt(2, day.getIdDay());
		daoUtil.executeUpdate();
		daoUtil.free();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AppointmentDay> findByIdForm(int nIdForm, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_SELECT_BY_ID_FORM, plugin);
		daoUtil.setInt(1, nIdForm);
		daoUtil.executeQuery();

		List<AppointmentDay> listDay = new ArrayList<AppointmentDay>();

		while (daoUtil.next()) {
			listDay.add(getDayFromDAO(daoUtil));
		}

		daoUtil.free();

		return listDay;
	}
}
