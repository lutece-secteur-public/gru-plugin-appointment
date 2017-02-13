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
	
	public static WeekDefinition generateWeekDefinition(int nIdForm, LocalDate dateOfApply) {
		WeekDefinition weekDefinition = new WeekDefinition();
		weekDefinition.setDateOfApply(dateOfApply);
		weekDefinition.setIdForm(nIdForm);
		WeekDefinitionHome.create(weekDefinition);
		return weekDefinition;
	}
	
	public static WeekDefinition findWeekDefinitionByFormIdAndDateOfApply(int nIdForm, LocalDate dateOfApply){
		WeekDefinition weekDefinition = WeekDefinitionHome.findByIdFormAndDateOfApply(nIdForm, dateOfApply);
		weekDefinition.setListWorkingDay(WorkingDayService.findListWorkingDayByWeekDefinition(weekDefinition.getIdWeekDefinition()));
		return weekDefinition;
	}
	
}
