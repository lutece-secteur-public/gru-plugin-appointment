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
package fr.paris.lutece.plugins.appointment.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.RemovalListenerService;
import fr.paris.lutece.util.ReferenceList;

/**
 * Service to manage entries
 */
public class EntryService extends RemovalListenerService implements Serializable {
	/**
	 * Name of the bean of this service
	 */
	public static final String BEAN_NAME = "appointment.entryService";
	private static final long serialVersionUID = -5378918040356139703L;

	private static final String MARK_ENTRY_LIST = "entry_list";
	private static final String MARK_ENTRY_TYPE_LIST = "entry_type_list";
	private static final String MARK_GROUP_ENTRY_LIST = "entry_group_list";
	private static final String MARK_LIST_ORDER_FIRST_LEVEL = "listOrderFirstLevel";
	
	/**
	 * Get an instance of the service
	 * 
	 * @return An instance of the service
	 */
	public static EntryService getService() {
		return SpringContextService.getBean(BEAN_NAME);
	}

	/**
	 * Change the attribute's order to a greater one (move down in the list)
	 * 
	 * @param nOrderToSet
	 *            the new order for the attribute
	 * @param entryToChangeOrder
	 *            the attribute which will change
	 */
	public void moveDownEntryOrder(int nOrderToSet, Entry entryToChangeOrder) {
		if (entryToChangeOrder.getParent() == null) {
			int nNbChild = 0;
			int nNewOrder = 0;

			EntryFilter filter = new EntryFilter();
			filter.setIdResource(entryToChangeOrder.getIdResource());
			filter.setResourceType(Form.RESOURCE_TYPE);
			filter.setEntryParentNull(EntryFilter.FILTER_TRUE);
			filter.setFieldDependNull(EntryFilter.FILTER_TRUE);

			List<Entry> listEntryFirstLevel = EntryHome.findEntriesWithoutParent(entryToChangeOrder.getIdResource(),
					entryToChangeOrder.getResourceType());

			List<Integer> orderFirstLevel = new ArrayList<Integer>();
			initOrderFirstLevel(listEntryFirstLevel, orderFirstLevel);

			Integer nbChildEntryToChangeOrder = 0;

			if (entryToChangeOrder.getChildren() != null) {
				nbChildEntryToChangeOrder = entryToChangeOrder.getChildren().size();
			}

			for (Entry entry : listEntryFirstLevel) {
				for (int i = 0; i < orderFirstLevel.size(); i++) {
					if ((orderFirstLevel.get(i) == entry.getPosition())
							&& (entry.getPosition() > entryToChangeOrder.getPosition())
							&& (entry.getPosition() <= nOrderToSet)) {
						if (nNbChild == 0) {
							nNewOrder = orderFirstLevel.get(i - 1);

							if (orderFirstLevel.get(i - 1) != entryToChangeOrder.getPosition()) {
								nNewOrder -= nbChildEntryToChangeOrder;
							}
						} else {
							nNewOrder += (nNbChild + 1);
						}

						entry.setPosition(nNewOrder);
						EntryHome.update(entry);
						nNbChild = 0;

						if (entry.getChildren() != null) {
							for (Entry child : entry.getChildren()) {
								nNbChild++;
								child.setPosition(nNewOrder + nNbChild);
								EntryHome.update(child);
							}
						}
					}
				}
			}

			entryToChangeOrder.setPosition(nNewOrder + nNbChild + 1);
			EntryHome.update(entryToChangeOrder);
			nNbChild = 0;

			for (Entry child : entryToChangeOrder.getChildren()) {
				nNbChild++;
				child.setPosition(entryToChangeOrder.getPosition() + nNbChild);
				EntryHome.update(child);
			}
		} else {
			EntryFilter filter = new EntryFilter();
			filter.setIdResource(entryToChangeOrder.getIdResource());
			filter.setResourceType(Form.RESOURCE_TYPE);
			filter.setFieldDependNull(EntryFilter.FILTER_TRUE);

			List<Entry> listAllEntry = EntryHome.getEntryList(filter);

			for (Entry entry : listAllEntry) {
				if ((entry.getPosition() > entryToChangeOrder.getPosition()) && (entry.getPosition() <= nOrderToSet)) {
					entry.setPosition(entry.getPosition() - 1);
					EntryHome.update(entry);
				}
			}

			entryToChangeOrder.setPosition(nOrderToSet);
			EntryHome.update(entryToChangeOrder);
		}
	}

	/**
	 * Change the attribute's order to a lower one (move up in the list)
	 * 
	 * @param nOrderToSet
	 *            the new order for the attribute
	 * @param entryToChangeOrder
	 *            the attribute which will change
	 */
	public void moveUpEntryOrder(int nOrderToSet, Entry entryToChangeOrder) {
		EntryFilter filter = new EntryFilter();
		filter.setIdResource(entryToChangeOrder.getIdResource());
		filter.setResourceType(Form.RESOURCE_TYPE);
		filter.setFieldDependNull(EntryFilter.FILTER_TRUE);

		if (entryToChangeOrder.getParent() == null) {
			filter.setEntryParentNull(EntryFilter.FILTER_TRUE);

			List<Integer> orderFirstLevel = new ArrayList<Integer>();

			int nNbChild = 0;
			int nNewOrder = nOrderToSet;
			int nEntryToMoveOrder = entryToChangeOrder.getPosition();

			List<Entry> listEntryFirstLevel = EntryHome.findEntriesWithoutParent(entryToChangeOrder.getIdResource(),
					entryToChangeOrder.getResourceType());
			// the list of all the orders in the first level
			initOrderFirstLevel(listEntryFirstLevel, orderFirstLevel);

			for (Entry entry : listEntryFirstLevel) {
				Integer entryInitialPosition = entry.getPosition();

				for (int i = 0; i < orderFirstLevel.size(); i++) {
					if ((orderFirstLevel.get(i) == entryInitialPosition) && (entryInitialPosition < nEntryToMoveOrder)
							&& (entryInitialPosition >= nOrderToSet)) {
						if (entryToChangeOrder.getPosition() == nEntryToMoveOrder) {
							entryToChangeOrder.setPosition(nNewOrder);
							EntryHome.update(entryToChangeOrder);

							for (Entry child : entryToChangeOrder.getChildren()) {
								nNbChild++;
								child.setPosition(entryToChangeOrder.getPosition() + nNbChild);
								EntryHome.update(child);
							}
						}

						nNewOrder = nNewOrder + nNbChild + 1;
						entry.setPosition(nNewOrder);
						EntryHome.update(entry);
						nNbChild = 0;

						for (Entry child : entry.getChildren()) {
							nNbChild++;
							child.setPosition(nNewOrder + nNbChild);
							EntryHome.update(child);
						}
					}
				}
			}
		} else {
			List<Entry> listAllEntry = EntryHome.getEntryList(filter);

			for (Entry entry : listAllEntry) {
				if ((entry.getPosition() < entryToChangeOrder.getPosition()) && (entry.getPosition() >= nOrderToSet)) {
					entry.setPosition(entry.getPosition() + 1);
					EntryHome.update(entry);
				}
			}

			entryToChangeOrder.setPosition(nOrderToSet);
			EntryHome.update(entryToChangeOrder);
		}
	}

	/**
	 * Move EntryToMove into entryGroup
	 * 
	 * @param entryToMove
	 *            the entry which will be moved
	 * @param entryGroup
	 *            the entry group
	 */
	public void moveEntryIntoGroup(Entry entryToMove, Entry entryGroup) {
		if ((entryToMove != null) && (entryGroup != null)) {
			// If the entry already has a parent, we must remove it before
			// adding it to a new one
			if (entryToMove.getParent() != null) {
				moveOutEntryFromGroup(entryToMove);
			}

			int nPosition;

			if (entryToMove.getPosition() < entryGroup.getPosition()) {
				nPosition = entryGroup.getPosition();
				moveDownEntryOrder(nPosition, entryToMove);
			} else {
				nPosition = entryGroup.getPosition() + entryGroup.getChildren().size() + 1;
				moveUpEntryOrder(nPosition, entryToMove);
			}

			entryToMove.setParent(entryGroup);
			EntryHome.update(entryToMove);
		}
	}

	/**
	 * Remove an entry from a group
	 * 
	 * @param entryToMove
	 *            the entry to remove from a group
	 */
	public void moveOutEntryFromGroup(Entry entryToMove) {
		Entry parent = EntryHome.findByPrimaryKey(entryToMove.getParent().getIdEntry());

		// The new position of the entry is the position of the group plus the
		// number of entries in the group (including this entry)
		moveDownEntryOrder(parent.getPosition() + parent.getChildren().size(), entryToMove);
		entryToMove.setParent(null);
		EntryHome.update(entryToMove);
	}

	/**
	 * Init the list of the attribute's orders (first level only)
	 * 
	 * @param listEntryFirstLevel
	 *            the list of all the attributes of the first level
	 * @param orderFirstLevel
	 *            the list to set
	 */
	private void initOrderFirstLevel(List<Entry> listEntryFirstLevel, List<Integer> orderFirstLevel) {
		for (Entry entry : listEntryFirstLevel) {
			orderFirstLevel.add(entry.getPosition());
		}
	}

	/**
	 * Remove every entries associated with a given appointment form
	 * 
	 * @param nIdForm
	 *            The id of the appointment to remove entries of
	 */
	public void removeEntriesByIdAppointmentForm(int nIdForm) {
		EntryFilter entryFilter = new EntryFilter();
		entryFilter.setIdResource(nIdForm);
		entryFilter.setResourceType(Form.RESOURCE_TYPE);
		entryFilter.setEntryParentNull(EntryFilter.FILTER_TRUE);
		entryFilter.setFieldDependNull(EntryFilter.FILTER_TRUE);

		List<Entry> listEntry = EntryHome.getEntryList(entryFilter);

		for (Entry entry : listEntry) {
			EntryHome.remove(entry.getIdEntry());
		}
	}

	public static void addListEntryToModel(int nIdForm, Map<String, Object> model){
		EntryFilter entryFilter = new EntryFilter();
		entryFilter.setIdResource(nIdForm);
		entryFilter.setResourceType(AppointmentForm.RESOURCE_TYPE);
		entryFilter.setEntryParentNull(EntryFilter.FILTER_TRUE);
		entryFilter.setFieldDependNull(EntryFilter.FILTER_TRUE);
		List<Entry> listEntryFirstLevel = EntryHome.getEntryList(entryFilter);
		List<Entry> listEntry = new ArrayList<Entry>(listEntryFirstLevel.size());
		List<Integer> listOrderFirstLevel = new ArrayList<Integer>(listEntryFirstLevel.size());
		for (Entry entry : listEntryFirstLevel) {
			listEntry.add(entry);
			listOrderFirstLevel.add(listEntry.size());
			if (entry.getEntryType().getGroup()) {
				entryFilter = new EntryFilter();
				entryFilter.setIdResource(nIdForm);
				entryFilter.setResourceType(AppointmentForm.RESOURCE_TYPE);
				entryFilter.setFieldDependNull(EntryFilter.FILTER_TRUE);
				entryFilter.setIdEntryParent(entry.getIdEntry());
				List<Entry> listEntryGroup = EntryHome.getEntryList(entryFilter);
				entry.setChildren(listEntryGroup);
				listEntry.addAll(listEntryGroup);
			}
		}
		model.put(MARK_GROUP_ENTRY_LIST, getRefListGroups(nIdForm));
		model.put(MARK_ENTRY_TYPE_LIST, EntryTypeService.getInstance().getEntryTypeReferenceList());
		model.put(MARK_ENTRY_LIST, listEntry);
		model.put(MARK_LIST_ORDER_FIRST_LEVEL, listOrderFirstLevel);
	}
	
	/**
	 * Get the reference list of groups
	 * 
	 * @param nIdForm
	 *            the id of the appointment form
	 * @return The reference list of groups of the given form
	 */
	private static ReferenceList getRefListGroups(int nIdForm) {
		EntryFilter entryFilter = new EntryFilter();
		entryFilter.setIdResource(nIdForm);
		entryFilter.setResourceType(AppointmentForm.RESOURCE_TYPE);
		entryFilter.setIdIsGroup(1);
		List<Entry> listEntry = EntryHome.getEntryList(entryFilter);
		ReferenceList refListGroups = new ReferenceList();
		for (Entry entry : listEntry) {
			refListGroups.addItem(entry.getIdEntry(), entry.getTitle());
		}
		return refListGroups;
	}
	
}
