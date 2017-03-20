package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinitionHome;
import fr.paris.lutece.util.ReferenceList;

public class WeekDefinitionService {

	/**
	 * 
	 * @param nIdForm
	 * @param dateOfApply
	 * @return
	 */
	public static WeekDefinition createWeekDefinition(int nIdForm, LocalDate dateOfApply) {
		WeekDefinition weekDefinition = new WeekDefinition();
		fillInWeekDefinition(weekDefinition, nIdForm, dateOfApply);
		WeekDefinitionHome.create(weekDefinition);
		return weekDefinition;
	}

	/**
	 * 
	 * @param nIdForm
	 * @param dateOfApply
	 * @return
	 */
	public static WeekDefinition updateWeekDefinition(int nIdForm, LocalDate dateOfApply) {
		WeekDefinition weekDefinition = WeekDefinitionHome.findByIdFormAndDateOfApply(nIdForm, dateOfApply);
		if (weekDefinition == null) {
			weekDefinition = createWeekDefinition(nIdForm, dateOfApply);
		} else {
			fillInWeekDefinition(weekDefinition, nIdForm, dateOfApply);
			WeekDefinitionHome.update(weekDefinition);
		}
		return weekDefinition;
	}

	/**
	 * 
	 * @param weekDefinition
	 * @param nIdForm
	 * @param dateOfApply
	 */
	public static void fillInWeekDefinition(WeekDefinition weekDefinition, int nIdForm, LocalDate dateOfApply) {
		weekDefinition.setDateOfApply(dateOfApply);
		weekDefinition.setIdForm(nIdForm);
	}

	public static List<WeekDefinition> findWeekDefinitionByIdForm(int nIdForm) {
		List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm(nIdForm);
		fillInListWeekDefinition(listWeekDefinition);
		return listWeekDefinition;
	}

	private static void fillInListWeekDefinition(List<WeekDefinition> listWeekDefinition) {
		for (WeekDefinition weekDefinition : listWeekDefinition) {
			fillInWeekDefinition(weekDefinition);
		}
	}

	private static void fillInWeekDefinition(WeekDefinition weekDefinition) {
		weekDefinition.setListWorkingDay(
				WorkingDayService.findListWorkingDayByWeekDefinition(weekDefinition.getIdWeekDefinition()));
	}

	/**
	 * 
	 * @param nIdForm
	 * @param dateOfApply
	 * @return
	 */
	public static WeekDefinition findWeekDefinitionByIdFormAndClosestToDateOfApply(int nIdForm, LocalDate dateOfApply) {
		List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm(nIdForm);
		List<LocalDate> listDate = new ArrayList<>();
		for (WeekDefinition weekDefinition : listWeekDefinition) {
			listDate.add(weekDefinition.getDateOfApply());
		}
		LocalDate closestDate = Utilities.getClosestDateInPast(listDate, dateOfApply);
		WeekDefinition weekDefinition = listWeekDefinition.stream().filter(x -> closestDate.isEqual(x.getDateOfApply()))
				.findAny().orElse(null);
		weekDefinition.setListWorkingDay(
				WorkingDayService.findListWorkingDayByWeekDefinition(weekDefinition.getIdWeekDefinition()));
		return weekDefinition;
	}

	public static WeekDefinition findWeekDefinitionLightById(int nIdWeekDefinition) {
		return WeekDefinitionHome.findByPrimaryKey(nIdWeekDefinition);
	}

	public static WeekDefinition findWeekDefinitionById(int nIdWeekDefinition) {
		WeekDefinition weekDefinition = WeekDefinitionHome.findByPrimaryKey(nIdWeekDefinition);
		weekDefinition.setListWorkingDay(
				WorkingDayService.findListWorkingDayByWeekDefinition(weekDefinition.getIdWeekDefinition()));
		return weekDefinition;
	}

	public static ReferenceList findAllDateOfWeekDefinition(int nIdForm) {
		ReferenceList listDate = new ReferenceList();
		List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm(nIdForm);
		for (WeekDefinition weekDefinition : listWeekDefinition) {
			listDate.addItem(weekDefinition.getIdWeekDefinition(),
					weekDefinition.getDateOfApply().format(Utilities.formatter));
		}
		return listDate;
	}

	public static HashMap<LocalDate, WeekDefinition> findAllWeekDefinition(int nIdForm) {
		HashMap<LocalDate, WeekDefinition> mapWeekDefinition = new HashMap<>();
		List<WeekDefinition> listWeekDefinition = WeekDefinitionHome.findByIdForm(nIdForm);
		for (WeekDefinition weekDefinition : listWeekDefinition) {
			weekDefinition.setListWorkingDay(
					WorkingDayService.findListWorkingDayByWeekDefinition(weekDefinition.getIdWeekDefinition()));
			mapWeekDefinition.put(weekDefinition.getDateOfApply(), weekDefinition);
		}
		return mapWeekDefinition;
	}

	public static LocalTime getMinStartingTimeOfAListOfWeekDefinition(List<WeekDefinition> listWeekDefinition) {
		LocalTime minStartingTime = null;
		LocalTime startingTimeTemp;
		for (WeekDefinition weekDefinition : listWeekDefinition) {
			startingTimeTemp = getMinStartingTimeOfAWeekDefinition(weekDefinition);
			if (minStartingTime == null || startingTimeTemp.isBefore(minStartingTime)) {
				minStartingTime = startingTimeTemp;
			}
		}
		return minStartingTime;
	}

	public static LocalTime getMinStartingTimeOfAWeekDefinition(WeekDefinition weekDefinition) {
		return WorkingDayService.getMinStartingTimeOfAListOfWorkingDay(weekDefinition.getListWorkingDay());
	}

	public static LocalTime getMaxEndingTimeOfAListOfWeekDefinition(List<WeekDefinition> listWeekDefinition) {
		LocalTime maxEndingTime = null;
		LocalTime endingTimeTemp;
		for (WeekDefinition weekDefinition : listWeekDefinition) {
			endingTimeTemp = getMaxEndingTimeOfAWeekDefinition(weekDefinition);
			if (maxEndingTime == null || endingTimeTemp.isAfter(maxEndingTime)) {
				maxEndingTime = endingTimeTemp;
			}
		}
		return maxEndingTime;
	}

	public static LocalTime getMaxEndingTimeOfAWeekDefinition(WeekDefinition weekDefinition) {
		return WorkingDayService.getMaxEndingTimeOfAListOfWorkingDay(weekDefinition.getListWorkingDay());
	}

	public static int getMinDurationTimeSlotOfAListOfWeekDefinition(List<WeekDefinition> listWeekDefinition) {
		int nMinDuration = 0;
		int nDurationTemp;
		for (WeekDefinition weekDefinition : listWeekDefinition) {
			nDurationTemp = getMinDurationTimeSlotOfAWeekDefinition(weekDefinition);
			if (nMinDuration == 0 || nMinDuration > nDurationTemp) {
				nMinDuration = nDurationTemp;
			}
		}
		return nMinDuration;
	}

	public static int getMinDurationTimeSlotOfAWeekDefinition(WeekDefinition weekDefinition) {
		return WorkingDayService.getMinDurationTimeSlotOfAListOfWorkingDay(weekDefinition.getListWorkingDay());
	}

	public static HashSet<String> getSetDayOfWeekOfAListOfWeekDefinition(List<WeekDefinition> listWeekDefinition) {
		HashSet<String> setDayOfWeek = new HashSet<>();
		for (WeekDefinition weekDefinition : listWeekDefinition) {
			setDayOfWeek
					.addAll(WorkingDayService.getSetDayOfWeekOfAListOfWorkingDay(weekDefinition.getListWorkingDay()));
		}
		return setDayOfWeek;
	}

}
