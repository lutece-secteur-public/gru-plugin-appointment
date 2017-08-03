package fr.paris.lutece.plugins.appointment.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Import/Export Forms
 * 
 * @author Laurent Payen
 *
 */
public class TradeService
{

    private static final String CLOSING_DAYS = "closing_days";
    private static final String WEEK_DEFINITIONS = "week_definitions";
    private static final String RESERVATION_RULES = "reservation_rules";
    private static final String WORKING_DAYS = "working_days";
    private static final String TIME_SLOTS = "time_slots";
    private static final String SLOTS = "slots";
    private static final String ENTRIES = "entries";
    private static final String FIELDS = "fields";
    private static final String CATEGORY = "category";
    private static final String FORM_RULE = "form_rule";
    private static final String DISPLAY = "display";
    private static final String LOCALIZATION = "localization";
    private static final String FORM_MESSAGE = "form_message";
    private static final String FORM = "form";
    private static final String IMPORT = "Import";

    private static ObjectMapper mapper;

    public TradeService( )
    {
        mapper = new ObjectMapper( );
        mapper.registerModule( new JavaTimeModule( ) );
    }

    public static void importForm( JSONObject jsonObject ) throws JsonParseException, JsonMappingException, IOException
    {
        ObjectMapper mapper = new ObjectMapper( );
        mapper.registerModule( new JavaTimeModule( ) );
        Object objectCategory = jsonObject.get( CATEGORY );
        Category category = null;
        if ( objectCategory != null )
        {
            category = mapper.readValue( objectCategory.toString( ), Category.class );
            if ( category != null )
            {
                category = CategoryService.saveCategory( category );
            }
        }
        Object objectForm = jsonObject.get( FORM );
        Form form = null;
        if ( objectForm != null )
        {
            form = mapper.readValue( objectForm.toString( ), Form.class );
        }
        if ( form != null )
        {
            form.setTitle( IMPORT + StringUtils.SPACE + form.getTitle( ) );
            if ( category != null )
            {
                form.setIdCategory( category.getIdCategory( ) );
            }
            form = FormService.saveForm( form );
            int nIdForm = form.getIdForm( );
            importFormRule( jsonObject, nIdForm );
            importDisplay( jsonObject, nIdForm );
            importLocalization( jsonObject, nIdForm );
            importFormMessage( jsonObject, nIdForm );
            importReservationRules( jsonObject, nIdForm );
            importClosingDays( jsonObject, nIdForm );
            importWeekDefinitions( jsonObject, nIdForm );
            importSlots( jsonObject, nIdForm );
            importEntries( jsonObject, nIdForm );
        }
    }

    public static JSONObject exportForm( int nIdForm ) throws JsonProcessingException
    {
        JSONObject jsObj = new JSONObject( );
        Form form = FormService.findFormLightByPrimaryKey( nIdForm );
        if ( form != null )
        {
            jsObj.put( FORM, mapper.writeValueAsString( form ) );
            Category category = CategoryService.findCategoryById( form.getIdCategory( ) );
            if ( category != null )
            {
                jsObj.put( CATEGORY, mapper.writeValueAsString( category ) );
            }
        }
        exportFormRule( jsObj, nIdForm );
        exportDisplay( jsObj, nIdForm );
        exportLocalization( jsObj, nIdForm );
        exportFormMessage( jsObj, nIdForm );
        exportReservationRules( jsObj, nIdForm );
        exportClosingDays( jsObj, nIdForm );
        exportWeekDefinitions( jsObj, nIdForm );
        exportSlots( jsObj, nIdForm );
        exportEntries( jsObj, nIdForm );
        return jsObj;
    }

    private static void importDisplay( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        Object objectDisplay = jsonObject.get( DISPLAY );
        Display display = null;
        if ( objectDisplay != null )
        {
            display = mapper.readValue( objectDisplay.toString( ), Display.class );
        }
        if ( display != null )
        {
            display.setIdForm( nIdForm );
            DisplayService.saveDisplay( display );
        }
    }

    private static void importFormRule( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        Object objectFormRule = jsonObject.get( FORM_RULE );
        FormRule formRule = null;
        if ( objectFormRule != null )
        {
            formRule = mapper.readValue( objectFormRule.toString( ), FormRule.class );
        }
        if ( formRule != null )
        {
            formRule.setIdForm( nIdForm );
            FormRuleService.saveFormRule( formRule );
        }
    }

    private static void importLocalization( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        Object objectLocalization = jsonObject.get( LOCALIZATION );
        Localization localization = null;
        if ( objectLocalization != null )
        {
            localization = mapper.readValue( objectLocalization.toString( ), Localization.class );
        }
        if ( localization != null )
        {
            localization.setIdForm( nIdForm );
            LocalizationService.saveLocalization( localization );
        }
    }

    private static void importFormMessage( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        Object objectFormMessage = jsonObject.get( FORM_MESSAGE );
        FormMessage formMessage = null;
        if ( objectFormMessage != null )
        {
            formMessage = mapper.readValue( objectFormMessage.toString( ), FormMessage.class );
        }
        if ( formMessage != null )
        {
            formMessage.setIdForm( nIdForm );
            FormMessageService.saveFormMessage( formMessage );
        }
    }

    private static void importReservationRules( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        JSONArray jsArrayReservationRules = jsonObject.getJSONArray( RESERVATION_RULES );
        if ( CollectionUtils.isNotEmpty( jsArrayReservationRules ) )
        {
            List<ReservationRule> listReservationRules = Arrays.asList( mapper.readValue( jsArrayReservationRules.toString( ), ReservationRule [ ].class ) );
            if ( CollectionUtils.isNotEmpty( listReservationRules ) )
            {
                for ( ReservationRule reservationRule : listReservationRules )
                {
                    if ( reservationRule != null )
                    {
                        reservationRule.setIdForm( nIdForm );
                        ReservationRuleService.saveReservationRule( reservationRule );
                    }
                }
            }
        }
    }

    private static void importClosingDays( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        JSONArray jsArrayClosingDays = jsonObject.getJSONArray( CLOSING_DAYS );
        if ( CollectionUtils.isNotEmpty( jsArrayClosingDays ) )
        {
            List<ClosingDay> listClosingDays = Arrays.asList( mapper.readValue( jsArrayClosingDays.toString( ), ClosingDay [ ].class ) );
            if ( CollectionUtils.isNotEmpty( listClosingDays ) )
            {
                for ( ClosingDay closingDay : listClosingDays )
                {
                    if ( closingDay != null )
                    {
                        closingDay.setIdForm( nIdForm );
                        ClosingDayService.saveClosingDay( closingDay );
                    }
                }
            }
        }
    }

    private static void importSlots( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        JSONArray jsArraySlots = jsonObject.getJSONArray( SLOTS );
        if ( CollectionUtils.isNotEmpty( jsArraySlots ) )
        {
            List<Slot> listSlots = Arrays.asList( mapper.readValue( jsArraySlots.toString( ), Slot [ ].class ) );
            if ( CollectionUtils.isNotEmpty( listSlots ) )
            {
                for ( Slot slot : listSlots )
                {
                    if ( slot != null )
                    {
                        slot.setIdForm( nIdForm );
                        slot.setIdSlot( 0 );
                        SlotService.saveSlot( slot );
                    }
                }
            }
        }
    }

    private static void importEntries( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        HashMap<Integer, Integer> mapIdEntry = new HashMap<>( );
        JSONArray jsArrayEntries = jsonObject.getJSONArray( ENTRIES );
        if ( CollectionUtils.isNotEmpty( jsArrayEntries ) )
        {
            List<Entry> listEntries = Arrays.asList( mapper.readValue( jsArrayEntries.toString( ), Entry [ ].class ) );
            if ( CollectionUtils.isNotEmpty( listEntries ) )
            {
                int nOldIdEntry;
                int nNewIdEntry;
                for ( Entry entry : listEntries )
                {
                    if ( entry != null )
                    {
                        nOldIdEntry = entry.getIdEntry( );
                        entry.setIdResource( nIdForm );
                        nNewIdEntry = EntryHome.create( entry );
                        mapIdEntry.put( nOldIdEntry, nNewIdEntry );
                    }
                }
            }
        }
        JSONArray jsArrayFields = jsonObject.getJSONArray( FIELDS );
        if ( CollectionUtils.isNotEmpty( jsArrayFields ) )
        {
            List<Field> listFields = Arrays.asList( mapper.readValue( jsArrayFields.toString( ), Field [ ].class ) );
            if ( CollectionUtils.isNotEmpty( listFields ) )
            {
                int nNewIdEntry;
                for ( Field field : listFields )
                {
                    if ( field != null )
                    {
                        nNewIdEntry = 0;
                        if ( field.getParentEntry( ) != null && mapIdEntry.containsKey( field.getParentEntry( ).getIdEntry( ) ) )
                        {
                            nNewIdEntry = mapIdEntry.get( field.getParentEntry( ).getIdEntry( ) );
                        }
                        field.setParentEntry( EntryHome.findByPrimaryKey( nNewIdEntry ) );
                        FieldHome.create( field );
                    }
                }
            }
        }
    }

    private static void importWeekDefinitions( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        HashMap<Integer, Integer> mapIdWeekDefinition = new HashMap<>( );
        JSONArray jsArrayWeekDefinitions = jsonObject.getJSONArray( WEEK_DEFINITIONS );
        if ( CollectionUtils.isNotEmpty( jsArrayWeekDefinitions ) )
        {
            List<WeekDefinition> listWeekDefinitions = Arrays.asList( mapper.readValue( jsArrayWeekDefinitions.toString( ), WeekDefinition [ ].class ) );
            if ( CollectionUtils.isNotEmpty( listWeekDefinitions ) )
            {
                int nOldIdWeekDefinition;
                for ( WeekDefinition weekDefinition : listWeekDefinitions )
                {
                    if ( weekDefinition != null )
                    {
                        nOldIdWeekDefinition = weekDefinition.getIdWeekDefinition( );
                        weekDefinition.setIdForm( nIdForm );
                        weekDefinition = WeekDefinitionService.saveWeekDefinition( weekDefinition );
                        mapIdWeekDefinition.put( nOldIdWeekDefinition, weekDefinition.getIdWeekDefinition( ) );
                    }
                }
            }
        }
        HashMap<Integer, Integer> mapIdWorkingDay = new HashMap<>( );
        JSONArray jsArrayWorkingDays = jsonObject.getJSONArray( WORKING_DAYS );
        if ( CollectionUtils.isNotEmpty( jsArrayWorkingDays ) )
        {
            List<WorkingDay> listWorkingDays = Arrays.asList( mapper.readValue( jsArrayWorkingDays.toString( ), WorkingDay [ ].class ) );
            if ( CollectionUtils.isNotEmpty( listWorkingDays ) )
            {
                int nOldIdWorkingDay;
                int nNewIdWeekDefinition;
                for ( WorkingDay workingDay : listWorkingDays )
                {
                    if ( workingDay != null )
                    {
                        nOldIdWorkingDay = workingDay.getIdWorkingDay( );
                        nNewIdWeekDefinition = 0;
                        if ( mapIdWeekDefinition.containsKey( workingDay.getIdWeekDefinition( ) ) )
                        {
                            nNewIdWeekDefinition = mapIdWeekDefinition.get( workingDay.getIdWeekDefinition( ) );
                        }
                        workingDay.setIdWeekDefinition( nNewIdWeekDefinition );
                        workingDay = WorkingDayService.saveWorkingDay( workingDay );
                        mapIdWorkingDay.put( nOldIdWorkingDay, workingDay.getIdWorkingDay( ) );
                    }
                }
            }
        }
        JSONArray jsArrayTimeSlots = jsonObject.getJSONArray( TIME_SLOTS );
        if ( CollectionUtils.isNotEmpty( jsArrayTimeSlots ) )
        {
            List<TimeSlot> listTimeSlots = Arrays.asList( mapper.readValue( jsArrayTimeSlots.toString( ), TimeSlot [ ].class ) );
            if ( CollectionUtils.isNotEmpty( listTimeSlots ) )
            {
                int nNewIdWorkingDay;
                for ( TimeSlot timeSlot : listTimeSlots )
                {
                    if ( timeSlot != null )
                    {
                        nNewIdWorkingDay = 0;
                        if ( mapIdWorkingDay.containsKey( timeSlot.getIdWorkingDay( ) ) )
                        {
                            nNewIdWorkingDay = mapIdWorkingDay.get( timeSlot.getIdWorkingDay( ) );
                        }
                        timeSlot.setIdWorkingDay( nNewIdWorkingDay );
                        TimeSlotService.saveTimeSlot( timeSlot );
                    }
                }
            }
        }
    }

    private static void exportWeekDefinitions( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        JSONArray jsWeekDefinitions = new JSONArray( );
        JSONArray jsWorkingDays = new JSONArray( );
        JSONArray jsTimeSlots = new JSONArray( );
        List<WeekDefinition> listWeekDefinitions = WeekDefinitionService.findListWeekDefinition( nIdForm );
        if ( CollectionUtils.isNotEmpty( listWeekDefinitions ) )
        {
            for ( WeekDefinition weekDefinition : listWeekDefinitions )
            {
                if ( weekDefinition != null )
                {
                    jsWeekDefinitions.add( mapper.writeValueAsString( weekDefinition ) );
                    if ( CollectionUtils.isNotEmpty( weekDefinition.getListWorkingDay( ) ) )
                    {
                        for ( WorkingDay workingDay : weekDefinition.getListWorkingDay( ) )
                        {
                            if ( workingDay != null )
                            {
                                jsWorkingDays.add( mapper.writeValueAsString( workingDay ) );
                                if ( CollectionUtils.isNotEmpty( workingDay.getListTimeSlot( ) ) )
                                {
                                    for ( TimeSlot timeSlot : workingDay.getListTimeSlot( ) )
                                    {
                                        if ( timeSlot != null )
                                        {
                                            jsTimeSlots.add( mapper.writeValueAsString( timeSlot ) );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if ( CollectionUtils.isNotEmpty( jsWeekDefinitions ) )
        {
            jsObj.put( WEEK_DEFINITIONS, jsWeekDefinitions );
        }
        if ( CollectionUtils.isNotEmpty( jsWorkingDays ) )
        {
            jsObj.put( WORKING_DAYS, jsWorkingDays );
        }
        if ( CollectionUtils.isNotEmpty( jsTimeSlots ) )
        {
            jsObj.put( TIME_SLOTS, jsTimeSlots );
        }
    }

    private static void exportFormRule( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        FormRule formRule = FormRuleService.findFormRuleWithFormId( nIdForm );
        if ( formRule != null )
        {
            jsObj.put( FORM_RULE, mapper.writeValueAsString( formRule ) );
        }
    }

    private static void exportDisplay( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        Display display = DisplayService.findDisplayWithFormId( nIdForm );
        if ( display != null )
        {
            jsObj.put( DISPLAY, mapper.writeValueAsString( display ) );
        }
    }

    private static void exportLocalization( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        Localization localization = LocalizationService.findLocalizationWithFormId( nIdForm );
        if ( localization != null )
        {
            jsObj.put( LOCALIZATION, mapper.writeValueAsString( localization ) );
        }
    }

    private static void exportFormMessage( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        FormMessage formMessage = FormMessageService.findFormMessageByIdForm( nIdForm );
        if ( formMessage != null )
        {
            jsObj.put( FORM_MESSAGE, mapper.writeValueAsString( formMessage ) );
        }
    }

    private static void exportReservationRules( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        JSONArray jsReservationRules = new JSONArray( );
        List<ReservationRule> listReservationRules = ReservationRuleService.findListReservationRule( nIdForm );
        if ( CollectionUtils.isNotEmpty( listReservationRules ) )
        {
            for ( ReservationRule reservationRule : listReservationRules )
            {
                if ( reservationRule != null )
                {
                    jsReservationRules.add( mapper.writeValueAsString( reservationRule ) );
                }
            }
        }
        if ( CollectionUtils.isNotEmpty( jsReservationRules ) )
        {
            jsObj.put( RESERVATION_RULES, jsReservationRules );
        }
    }

    private static void exportClosingDays( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        JSONArray jsClosingDays = new JSONArray( );
        List<ClosingDay> listClosingDays = ClosingDayService.findListClosingDay( nIdForm );
        if ( CollectionUtils.isNotEmpty( listClosingDays ) )
        {
            for ( ClosingDay closingDay : listClosingDays )
            {
                if ( closingDay != null )
                {
                    jsClosingDays.add( mapper.writeValueAsString( closingDay ) );
                }
            }
        }
        if ( CollectionUtils.isNotEmpty( jsClosingDays ) )
        {
            jsObj.put( CLOSING_DAYS, jsClosingDays );
        }
    }

    private static void exportSlots( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        JSONArray jsSlots = new JSONArray( );
        List<Slot> listSlots = SlotService.findListSlot( nIdForm );
        if ( CollectionUtils.isNotEmpty( listSlots ) )
        {
            for ( Slot slot : listSlots )
            {
                if ( slot != null )
                {
                    jsSlots.add( mapper.writeValueAsString( slot ) );
                }
            }
        }
        if ( CollectionUtils.isNotEmpty( jsSlots ) )
        {
            jsObj.put( SLOTS, jsSlots );
        }
    }

    private static void exportEntries( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        JSONArray jsEntries = new JSONArray( );
        JSONArray jsFields = new JSONArray( );
        List<Entry> listEntries = EntryService.findListEntry( nIdForm );
        if ( CollectionUtils.isNotEmpty( listEntries ) )
        {
            for ( Entry entry : listEntries )
            {
                if ( entry != null )
                {
                    jsEntries.add( mapper.writeValueAsString( entry ) );
                    if ( CollectionUtils.isNotEmpty( entry.getFields( ) ) )
                    {
                        for ( Field field : entry.getFields( ) )
                        {
                            if ( field != null )
                            {
                                jsFields.add( mapper.writeValueAsString( field ) );
                            }
                        }
                    }
                }
            }
        }
        if ( CollectionUtils.isNotEmpty( jsEntries ) )
        {
            jsObj.put( ENTRIES, jsEntries );
        }
        if ( CollectionUtils.isNotEmpty( jsFields ) )
        {
            jsObj.put( FIELDS, jsFields );
        }
    }
}
