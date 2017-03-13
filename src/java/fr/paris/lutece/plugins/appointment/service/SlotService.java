package fr.paris.lutece.plugins.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.slot.SlotHome;

public class SlotService {

	public static HashMap<LocalDateTime, Slot> findListSlotByIdFormAndDateRange(int nIdForm,
			LocalDateTime startingDateTime, LocalDateTime endingDateTime) {
		return SlotHome.findByIdFormAndDateRange(nIdForm, startingDateTime, endingDateTime);
	}

	public static Slot findSlotById(int nIdSlot) {
		return SlotHome.findByPrimaryKey(nIdSlot);
	}

	public static List<Slot> buildListSlot(int nIdForm, HashMap<LocalDate, WeekDefinition> mapWeekDefinition,
			int nNbWeeksToDisplay) {
		List<Slot> listSlot = new ArrayList<>();
		// Get the date of today
		LocalDate dateNow = LocalDate.now();
		// Get the date of the monday of this week
		LocalDate dateOfMondayOrFirstDateToDisplay = dateNow.with(DayOfWeek.MONDAY);
		// Need to check if this date is not before the form date creation
		// Get all the reservation rules
		HashMap<LocalDate, ReservationRule> mapReservationRule = ReservationRuleService.findAllReservationRule(nIdForm);
		LocalDate firstDateOfReservationRule = new ArrayList<>(mapReservationRule.keySet()).stream().sorted()
				.findFirst().orElse(null);
		if (dateOfMondayOrFirstDateToDisplay.isBefore(firstDateOfReservationRule)) {
			dateOfMondayOrFirstDateToDisplay = firstDateOfReservationRule;
		}
		// Add the nb weeks to display to have the ending date (and get the last
		// day of the week : Sunday)
		LocalDate endingDateToDisplay = dateNow.plusWeeks(nNbWeeksToDisplay).with(DayOfWeek.SUNDAY);
		// Get all the closing day of this period
		List<LocalDate> listDateOfClosingDay = ClosingDayService.findListDateOfClosingDayByIdFormAndDateRange(nIdForm,
				dateOfMondayOrFirstDateToDisplay, endingDateToDisplay);
		// Get all the slot between these two dates
		HashMap<LocalDateTime, Slot> mapSlot = SlotService.findListSlotByIdFormAndDateRange(nIdForm,
				dateOfMondayOrFirstDateToDisplay.atStartOfDay(), endingDateToDisplay.atTime(LocalTime.MAX));

		// Get or build all the event for the period
		LocalDate dateTemp = dateOfMondayOrFirstDateToDisplay;
		while (dateTemp.isBefore(endingDateToDisplay) || !dateTemp.isAfter(endingDateToDisplay)) {
			final LocalDate dateToCompare = dateTemp;
			// Find the closest date of apply of week definition with the given
			// date
			LocalDate closestDateWeekDefinition = Utilities
					.getClosestDateInPast(new ArrayList<>(mapWeekDefinition.keySet()), dateToCompare);
			WeekDefinition weekDefinition = mapWeekDefinition.get(closestDateWeekDefinition);
			// Find the closest date of apply of reservation rule with the given
			// date
			LocalDate closestDateReservationRule = Utilities
					.getClosestDateInPast(new ArrayList<>(mapReservationRule.keySet()), dateToCompare);
			ReservationRule reservationRule = mapReservationRule.get(closestDateReservationRule);
			int nMaxCapacity = reservationRule.getMaxCapacityPerSlot();
			// Get the day of week of the date
			DayOfWeek dayOfWeek = dateTemp.getDayOfWeek();
			// Get the working day of this day of week
			WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek(weekDefinition.getListWorkingDay(),
					dayOfWeek);
			// if there is no working day, it's because it is not a working day,
			// so nothing to add in the lost of slots
			if (workingDay != null) {
				LocalTime minTimeForThisDay = WorkingDayService.getMinStartingTimeOfAWorkingDay(workingDay);
				LocalTime maxTimeForThisDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay(workingDay);
				// Check if this day is a closing day
				boolean isAClosingDay = listDateOfClosingDay.contains(dateTemp);
				if (isAClosingDay) {
					listSlot.add(buildSlot(nIdForm, dateTemp.atTime(minTimeForThisDay),
							dateTemp.atTime(maxTimeForThisDay), nMaxCapacity, nMaxCapacity, Boolean.FALSE));
				} else {
					LocalTime timeTemp = minTimeForThisDay;
					// For each slot of this day
					while (timeTemp.isBefore(maxTimeForThisDay) || !timeTemp.equals(maxTimeForThisDay)) {
						// Get the LocalDateTime
						LocalDateTime dateTimeTemp = dateTemp.atTime(timeTemp);
						Slot slotToAdd;
						// Search if there is a slot for this datetime
						if (mapSlot.containsKey(dateTimeTemp)) {
							slotToAdd = mapSlot.get(dateTimeTemp);
							timeTemp = slotToAdd.getEndingDateTime().toLocalTime();
							listSlot.add(slotToAdd);
						} else {
							// Search the timeslot
							TimeSlot timeSlot = TimeSlotService.getTimeSlotInListOfTimeSlotWithStartingTime(
									workingDay.getListTimeSlot(), timeTemp);
							if (timeSlot != null) {
								timeTemp = timeSlot.getEndingTime();
								slotToAdd = buildSlot(nIdForm, dateTimeTemp, dateTemp.atTime(timeTemp), nMaxCapacity,
										nMaxCapacity, timeSlot.getIsOpen());
								listSlot.add(slotToAdd);
							} else {
								break;
							}
						}
					}
				}
			}
			dateTemp = dateTemp.plusDays(1);
		}
		return listSlot;
	}

	public static Slot buildSlot(int nIdForm, LocalDateTime startingDateTime, LocalDateTime endingDateTime,
			int nMaxCapacity, int nNbRemainingPlaces, boolean bIsOpen) {
		Slot slot = new Slot();
		slot.setIdSlot(0);
		slot.setIdForm(nIdForm);
		slot.setStartingDateTime(startingDateTime);
		slot.setEndingDateTime(endingDateTime);
		slot.setMaxCapacity(nMaxCapacity);
		slot.setNbRemainingPlaces(nNbRemainingPlaces);
		slot.setIsOpen(bIsOpen);
		addDateAndTimeToSlot(slot);
		return slot;
	}

	public static void updateSlot(Slot slot, boolean bEndingTimeHasChanged, boolean bShifSlot) {
		List<Slot> listSlotToCreate = new ArrayList<>();
		if (bEndingTimeHasChanged) {
			LocalDate dateOfSlot = slot.getDate();
			if (!bShifSlot) {
				// Need to get all the slots until the new end of this slot
				List<Slot> listSlotToDelete = new ArrayList<>(SlotService
						.findListSlotByIdFormAndDateRange(slot.getIdForm(),
								slot.getStartingDateTime().plus(1, ChronoUnit.MINUTES), slot.getEndingDateTime())
						.values());
				deleteListSlot(listSlotToDelete);
				// Get the list of slot after the modified slot
				HashMap<LocalDateTime, Slot> mapNextSlot = SlotService.findListSlotByIdFormAndDateRange(
						slot.getIdForm(), slot.getEndingDateTime(), slot.getDate().atTime(LocalTime.MAX));
				List<LocalDateTime> listStartingDateTimeNextSlot = new ArrayList<>(mapNextSlot.keySet());
				// Get the next date time slot
				LocalDateTime nextStartingDateTime = null;
				if (!CollectionUtils.isEmpty(listStartingDateTimeNextSlot)) {
					nextStartingDateTime = Utilities.getClosestDateTimeInFuture(listStartingDateTimeNextSlot,
							slot.getEndingDateTime());
				} else {
					// No slot after this one.
					// Need to compute between the end of this slot and the next
					// time slot
					WeekDefinition weekDefinition = WeekDefinitionService
							.findWeekDefinitionByIdFormAndClosestToDateOfApply(slot.getIdForm(), dateOfSlot);
					WorkingDay workingDay = WorkingDayService
							.getWorkingDayOfDayOfWeek(weekDefinition.getListWorkingDay(), dateOfSlot.getDayOfWeek());
					List<TimeSlot> sortedListTimeSlotAfterThisSlot = TimeSlotService
							.getSortedListTimeSlotAfterALocalTime(workingDay.getListTimeSlot(), slot.getEndingTime());
					TimeSlot nextTimeSlot = sortedListTimeSlotAfterThisSlot.get(0);
					nextStartingDateTime = nextTimeSlot.getStartingTime().atDate(dateOfSlot);
				}
				// Need to create a slot between these two dateTime
				if (!slot.getEndingDateTime().isEqual(nextStartingDateTime)) {
					Slot slotToCreate = buildSlot(slot.getIdForm(), slot.getEndingDateTime(), nextStartingDateTime,
							slot.getMaxCapacity(), slot.getMaxCapacity(), Boolean.FALSE);
					listSlotToCreate.add(slotToCreate);
				}
			} else {
				// Need to delete the slot until the end of the day
				List<Slot> listSlotToDelete = new ArrayList<>(SlotService.findListSlotByIdFormAndDateRange(
						slot.getIdForm(), slot.getStartingDateTime().plus(1, ChronoUnit.MINUTES),
						slot.getDate().atTime(LocalTime.MAX)).values());
				deleteListSlot(listSlotToDelete);
				// Generated the new slots at the end of the modified slot
				listSlotToCreate.addAll(generateListSlotToCreateAfterASlot(slot));
			}
		}
		if (slot.getIdSlot() == 0) {
			SlotHome.create(slot);
		} else {
			SlotHome.update(slot);
		}
		createListSlot(listSlotToCreate);
	}

	private static List<Slot> generateListSlotToCreateAfterASlot(Slot slot) {
		List<Slot> listSlotToCreate = new ArrayList<>();
		LocalDate dateOfSlot = slot.getDate();
		ReservationRule reservationRule = ReservationRuleService
				.findReservationRuleByIdFormAndClosestToDateOfApply(slot.getIdForm(), dateOfSlot);
		int nMaxCapacity = reservationRule.getMaxCapacityPerSlot();
		WeekDefinition weekDefinition = WeekDefinitionService
				.findWeekDefinitionByIdFormAndClosestToDateOfApply(slot.getIdForm(), dateOfSlot);
		WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek(weekDefinition.getListWorkingDay(),
				dateOfSlot.getDayOfWeek());
		LocalTime endingTimeOfTheDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay(workingDay);
		LocalDateTime endingDateTimeOfTheDay = endingTimeOfTheDay.atDate(dateOfSlot);
		int nDurationSlot = WorkingDayService.getMinDurationTimeSlotOfAWorkingDay(workingDay);
		LocalDateTime startingDateTime = slot.getEndingDateTime();
		LocalDateTime endingDateTime = startingDateTime.plus(nDurationSlot, ChronoUnit.MINUTES);
		int nIdForm = slot.getIdForm();
		while (!endingDateTime.isAfter(endingDateTimeOfTheDay)) {
			Slot slotToCreate = buildSlot(nIdForm, startingDateTime, endingDateTime, nMaxCapacity, nMaxCapacity,
					Boolean.TRUE);
			startingDateTime = endingDateTime;
			endingDateTime = startingDateTime.plus(nDurationSlot, ChronoUnit.MINUTES);
			listSlotToCreate.add(slotToCreate);
		}
		return listSlotToCreate;
	}

	public static void addDateAndTimeToSlot(Slot slot) {
		slot.setDate(slot.getStartingDateTime().toLocalDate());
		slot.setStartingTime(slot.getStartingDateTime().toLocalTime());
		slot.setEndingTime(slot.getEndingDateTime().toLocalTime());
	}

	private static void createListSlot(List<Slot> listSlotToCreate) {
		for (Slot slotTemp : listSlotToCreate) {
			SlotHome.create(slotTemp);
		}
	}

	private static void deleteListSlot(List<Slot> listSlotToDelete) {
		for (Slot slotToDelete : listSlotToDelete) {
			SlotHome.delete(slotToDelete.getIdSlot());
		}
	}

	public static LocalDate findFirstDateOfFreeOpenSlot(int nIdForm, LocalDate startingDate, LocalDate endingDate) {
		boolean bFreeSlotFound = false;
		LocalDate localDateFound = null;
		LocalDate currentDateOfSearch = startingDate;
		List<LocalDate> listClosingDate = ClosingDayService.findListDateOfClosingDayByIdFormAndDateRange(nIdForm,
				startingDate, endingDate);
		// Get all the slot between these two dates
		HashMap<LocalDateTime, Slot> mapSlot = SlotService.findListSlotByIdFormAndDateRange(nIdForm,
				startingDate.atStartOfDay(), endingDate.atTime(LocalTime.MAX));
		while (!bFreeSlotFound && (currentDateOfSearch.isBefore(endingDate) || !currentDateOfSearch.equals(endingDate))) {
			if (!listClosingDate.contains(currentDateOfSearch)) {
				WeekDefinition weekDefinition = WeekDefinitionService
						.findWeekDefinitionByIdFormAndClosestToDateOfApply(nIdForm, currentDateOfSearch);
				WorkingDay workingDay = WorkingDayService.getWorkingDayOfDayOfWeek(weekDefinition.getListWorkingDay(),
						currentDateOfSearch.getDayOfWeek());
				if (workingDay != null) {
					LocalTime minTimeForThisDay = WorkingDayService.getMinStartingTimeOfAWorkingDay(workingDay);
					LocalTime maxTimeForThisDay = WorkingDayService.getMaxEndingTimeOfAWorkingDay(workingDay);
					LocalTime timeTemp = minTimeForThisDay;
					// For each slot of this day
					while (timeTemp.isBefore(maxTimeForThisDay) || !timeTemp.equals(maxTimeForThisDay)) {
						// Get the LocalDateTime
						LocalDateTime dateTimeTemp = currentDateOfSearch.atTime(timeTemp);
						// Search if there is a slot for this datetime
						if (mapSlot.containsKey(dateTimeTemp)) {
							Slot slot = mapSlot.get(dateTimeTemp);
							if (slot.getIsOpen() && slot.getNbRemainingPlaces() > 0) {
								bFreeSlotFound = true;
								localDateFound = currentDateOfSearch;
								break;
							} else {
								timeTemp = slot.getEndingDateTime().toLocalTime();
							}
						} else {
							// Search the timeslot
							TimeSlot timeSlot = TimeSlotService.getTimeSlotInListOfTimeSlotWithStartingTime(
									workingDay.getListTimeSlot(), timeTemp);
							if (timeSlot != null) {
								if (timeSlot.getIsOpen() && timeSlot.getMaxCapacity() > 0) {
									bFreeSlotFound = true;
									localDateFound = currentDateOfSearch;
									break;
								} else {
									timeTemp = timeSlot.getEndingTime();
								}
							} else {
								break;
							}
						}
					}
				}
			}
		}
		return localDateFound;
	}

}
