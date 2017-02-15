package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;

import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinitionHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class WeekDefinitionService {

	/**
	 * Name of the bean of the service
	 */
	public static final String BEAN_NAME = "appointment.weekDefinitionService";

	/**
	 * Instance of the service
	 */
	private static volatile WeekDefinitionService _instance;

	/**
	 * Get an instance of the service
	 * 
	 * @return An instance of the service
	 */
	public static WeekDefinitionService getInstance() {
		if (_instance == null) {
			_instance = SpringContextService.getBean(BEAN_NAME);
		}

		return _instance;
	}

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

	/**
	 * 
	 * @param nIdForm
	 * @param dateOfApply
	 * @return
	 */
	public static WeekDefinition findWeekDefinitionByFormIdAndClosestToDateOfApply(int nIdForm, LocalDate dateOfApply) {
		WeekDefinition weekDefinition = WeekDefinitionHome.findByIdFormAndClosestToDateOfApply(nIdForm, dateOfApply);
		weekDefinition.setListWorkingDay(
				WorkingDayService.findListWorkingDayByWeekDefinition(weekDefinition.getIdWeekDefinition()));
		return weekDefinition;
	}

}
