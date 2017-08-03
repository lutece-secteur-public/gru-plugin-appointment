package fr.paris.lutece.plugins.appointment.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fr.paris.lutece.plugins.appointment.business.category.Category;
import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.localization.Localization;
import fr.paris.lutece.plugins.appointment.business.message.FormMessage;
import fr.paris.lutece.plugins.appointment.business.planning.ClosingDay;
import fr.paris.lutece.plugins.appointment.business.planning.TimeSlot;
import fr.paris.lutece.plugins.appointment.business.planning.WeekDefinition;
import fr.paris.lutece.plugins.appointment.business.planning.WorkingDay;
import fr.paris.lutece.plugins.appointment.business.rule.FormRule;
import fr.paris.lutece.plugins.appointment.business.rule.ReservationRule;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Import/Export Forms
 * 
 * @author Laurent Payen
 *
 */
public class TradeService {

	private static final String CLOSING_DAYS = "closing_days";
	private static final String WEEK_DEFINITIONS = "week_definitions";
	private static final String RESERVATION_RULES = "reservation_rules";
	private static final String WORKING_DAYS = "working_days";
	private static final String TIME_SLOTS = "time_slots";
	private static final String SLOTS = "slots";
	private static final String CATEGORY = "category";
	private static final String FORM_RULE = "form_rule";
	private static final String DISPLAY = "display";
	private static final String LOCALIZATION = "localization";
	private static final String FORM_MESSAGE = "form_message";
	private static final String FORM = "form";
	private static final String IMPORT = "Import";

	public static void importForm(JSONObject jsonObject) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		Category category = mapper.readValue(jsonObject.get(CATEGORY).toString(), Category.class);
		if (category != null){				
			category = CategoryService.saveCategory(category);
		}
		
		Form form = mapper.readValue(jsonObject.get(FORM).toString(), Form.class);
		if (form != null) {
			form.setTitle(IMPORT + " " + form.getTitle());
			if (category != null){
				form.setIdCategory(category.getIdCategory());
			}
			form = FormService.saveForm(form);						
			int nIdForm = form.getIdForm();				
			FormRule formRule = mapper.readValue(jsonObject.get(FORM_RULE).toString(), FormRule.class);
			if (formRule != null){
				formRule.setIdForm(nIdForm);
				FormRuleService.saveFormRule(formRule);
			}
			Display display = mapper.readValue(jsonObject.get(DISPLAY).toString(), Display.class);
			if (display != null){
				display.setIdForm(nIdForm);
				DisplayService.saveDisplay(display);
			}
			Localization localization = mapper.readValue(jsonObject.get(LOCALIZATION).toString(), Localization.class);
			if (localization != null){
				localization.setIdForm(nIdForm);
				LocalizationService.saveLocalization(localization);
			}
			FormMessage formMessage = mapper.readValue(jsonObject.get(FORM_MESSAGE).toString(), FormMessage.class);
			if (formMessage != null){
				formMessage.setIdForm(nIdForm);
				FormMessageService.saveFormMessage(formMessage);
			}
			List<ReservationRule> listReservationRules = Arrays
					.asList(mapper.readValue(jsonObject.getJSONArray(RESERVATION_RULES).toString(), ReservationRule[].class));
			for (ReservationRule reservationRule : listReservationRules) {
				reservationRule.setIdForm(nIdForm);
				ReservationRuleService.saveReservationRule(reservationRule);
			}	
			List<ClosingDay> listClosingDays = Arrays
					.asList(mapper.readValue(jsonObject.getJSONArray(CLOSING_DAYS).toString(), ClosingDay[].class));
			for (ClosingDay closingDay : listClosingDays) {
				closingDay.setIdForm(nIdForm);
				ClosingDayService.saveClosingDay(closingDay);
			}	
			HashMap<Integer, Integer> mapIdWeekDefinition = new HashMap<>();
			List<WeekDefinition> listWeekDefinitions = Arrays
					.asList(mapper.readValue(jsonObject.getJSONArray(WEEK_DEFINITIONS).toString(), WeekDefinition[].class));
			int oldIdWeekDefinition;
			for (WeekDefinition weekDefinition : listWeekDefinitions) {
				oldIdWeekDefinition = weekDefinition.getIdWeekDefinition();
				weekDefinition.setIdForm(nIdForm);
				weekDefinition = WeekDefinitionService.saveWeekDefinition(weekDefinition);
				mapIdWeekDefinition.put(oldIdWeekDefinition, weekDefinition.getIdWeekDefinition());
			}			
			HashMap<Integer, Integer> mapIdWorkingDay = new HashMap<>();
			List<WorkingDay> listWorkingDays = Arrays
					.asList(mapper.readValue(jsonObject.getJSONArray(WORKING_DAYS).toString(), WorkingDay[].class));
			int oldIdWorkingDay;
			for (WorkingDay workingDay : listWorkingDays) {
				oldIdWorkingDay = workingDay.getIdWorkingDay();
				workingDay.setIdWeekDefinition(mapIdWeekDefinition.get(workingDay.getIdWeekDefinition()));				
				workingDay = WorkingDayService.saveWorkingDay(workingDay);
				mapIdWorkingDay.put(oldIdWorkingDay, workingDay.getIdWorkingDay());
			}					
			List<TimeSlot> listTimeSlots = Arrays
					.asList(mapper.readValue(jsonObject.getJSONArray(TIME_SLOTS).toString(), TimeSlot[].class));			
			for (TimeSlot timeSlot : listTimeSlots) {				
				timeSlot.setIdWorkingDay(mapIdWorkingDay.get(timeSlot.getIdWorkingDay()));				
				TimeSlotService.saveTimeSlot(timeSlot);				
			}			
			List<Slot> listSlots = Arrays
					.asList(mapper.readValue(jsonObject.getJSONArray(SLOTS).toString(), Slot[].class));			
			for (Slot slot : listSlots) {				
				slot.setIdForm(nIdForm);	
				slot.setIdSlot(0);
				SlotService.saveSlot(slot);				
			}	
			
			// Generic attributes
			
		}
	}

	public static JSONObject exportForm(int nIdForm) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		JSONObject jsObj = new JSONObject();
		Form form = FormService.findFormLightByPrimaryKey(nIdForm);
		if (form != null) {
			jsObj.put(FORM, mapper.writeValueAsString(form));
			Category category = CategoryService.findCategoryById(form.getIdCategory());
			jsObj.put(CATEGORY, mapper.writeValueAsString(category));
		}
		FormRule formRule = FormRuleService.findFormRuleWithFormId(nIdForm);
		jsObj.put(FORM_RULE, mapper.writeValueAsString(formRule));

		Display display = DisplayService.findDisplayWithFormId(nIdForm);
		jsObj.put(DISPLAY, mapper.writeValueAsString(display));

		Localization localization = LocalizationService.findLocalizationWithFormId(nIdForm);
		jsObj.put(LOCALIZATION, mapper.writeValueAsString(localization));

		FormMessage formMessage = FormMessageService.findFormMessageByIdForm(nIdForm);
		jsObj.put(FORM_MESSAGE, mapper.writeValueAsString(formMessage));

		JSONArray jsReservationRules = new JSONArray();
		List<ReservationRule> listReservationRules = ReservationRuleService.findListReservationRule(nIdForm);
		for (ReservationRule reservationRule : listReservationRules) {
			jsReservationRules.add(mapper.writeValueAsString(reservationRule));
		}
		if (!jsReservationRules.isEmpty()) {
			jsObj.put(RESERVATION_RULES, jsReservationRules);
		}

		JSONArray jsClosingDays = new JSONArray();
		List<ClosingDay> listClosingDays = ClosingDayService.findListClosingDay(nIdForm);
		for (ClosingDay closingDay : listClosingDays) {
			jsClosingDays.add(mapper.writeValueAsString(closingDay));
		}
		if (!jsClosingDays.isEmpty()) {
			jsObj.put(CLOSING_DAYS, jsClosingDays);
		}

		JSONArray jsWeekDefinitions = new JSONArray();
		JSONArray jsWorkingDays = new JSONArray();
		JSONArray jsTimeSlots = new JSONArray();
		List<WeekDefinition> listWeekDefinitions = WeekDefinitionService.findListWeekDefinition(nIdForm);
		for (WeekDefinition weekDefinition : listWeekDefinitions) {
			jsWeekDefinitions.add(mapper.writeValueAsString(weekDefinition));
			for (WorkingDay workingDay : weekDefinition.getListWorkingDay()) {
				jsWorkingDays.add(mapper.writeValueAsString(workingDay));
				for (TimeSlot timeSlot : workingDay.getListTimeSlot()) {
					jsTimeSlots.add(mapper.writeValueAsString(timeSlot));
				}
			}
		}
		if (!jsWeekDefinitions.isEmpty()) {
			jsObj.put(WEEK_DEFINITIONS, jsWeekDefinitions);
		}
		if (!jsWorkingDays.isEmpty()) {
			jsObj.put(WORKING_DAYS, jsWorkingDays);
		}
		if (!jsTimeSlots.isEmpty()) {
			jsObj.put(TIME_SLOTS, jsTimeSlots);
		}

		JSONArray jsSlots = new JSONArray();
		List<Slot> listSlots = SlotService.findListSlot(nIdForm);
		for (Slot slot : listSlots) {
			jsSlots.add(mapper.writeValueAsString(slot));
		}
		if (!jsSlots.isEmpty()) {
			jsObj.put(SLOTS, jsSlots);
		}
		
		// Generic attributes
		
		
		return jsObj;
	}

}
