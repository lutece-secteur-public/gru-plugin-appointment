/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
import fr.paris.lutece.plugins.regularexpression.business.RegularExpressionHome;
import fr.paris.lutece.plugins.appointment.service.WorkflowTraderService;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.util.AppLogService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Import/Export Forms from Json
 * 
 * @author Laurent Payen
 *
 */
public final class FormTraderService
{

    // PROPERTIES
    private static final String CATEGORY = "category";
    private static final String CLOSING_DAYS = "closing_days";
    private static final String DISPLAY = "display";
    private static final String ENTRIES = "entries";
    private static final String FIELDS = "fields";
    private static final String FORM = "form";
    private static final String FORM_MESSAGE = "form_message";
    private static final String FORM_RULE = "form_rule";
    private static final String IMPORT = "Import";
    private static final String LOCALIZATION = "localization";
    private static final String RESERVATION_RULES = "reservation_rules";
    private static final String SLOTS = "slots";
    private static final String WEEK_DEFINITIONS = "week_definitions";
    private static final String WORKFLOW = "workflow";

    /**
     * The mapper (need to add the javaTime module for Java 8 date compatibility)l
     */
    private static ObjectMapper _mapper = new ObjectMapper( ).registerModule( new JavaTimeModule( ) ).setSerializationInclusion( Include.NON_NULL );

    private static Plugin _plugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

    /**
     * Private constructor
     */
    private FormTraderService( )
    {
    }

    /**
     * Import a form in database from a json
     * 
     * @param jsonObject
     *            the json object
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static void importFormFromJson( JSONObject jsonObject ) throws IOException
    {
        Form form = null;
        Category category = null;
        int nIdWorkflow = 0;
        Object objectCategory = jsonObject.get( CATEGORY );
        Object objectWorkflow = jsonObject.get( WORKFLOW );
        try
        {
            if ( objectCategory != null )
            {
                category = _mapper.readValue( objectCategory.toString( ), Category.class );
                if ( category != null )
                {
                    category = CategoryService.saveCategory( category );
                }
            }
            if ( objectWorkflow != null )
            {
                nIdWorkflow = WorkflowTraderService.importWorkflowFromJson( JSONObject.fromObject( objectWorkflow ) );
            }
            Object objectForm = jsonObject.get( FORM );
            if ( objectForm != null )
            {
                form = _mapper.readValue( objectForm.toString( ), Form.class );
            }
            if ( form != null )
            {
                // To avoid multiple forms with same name
                if ( CollectionUtils.isNotEmpty( FormService.findFormsByTitle( form.getTitle( ) ) ) )
                {
                    form.setTitle( IMPORT + StringUtils.SPACE + form.getTitle( ) );
                }
                if ( category != null )
                {
                    form.setIdCategory( category.getIdCategory( ) );
                }
                if ( nIdWorkflow != 0 )
                {
                    form.setIdWorkflow( nIdWorkflow );
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
        catch( IOException e )
        {
            AppLogService.error( "Error during import of the json", e );
            throw e;
        }
    }

    /**
     * Export a form in a json object
     * 
     * @param nIdForm
     *            the form id
     * @return a json object of the form
     * @throws JsonProcessingException
     */
    public static JSONObject exportFormToJson( int nIdForm )
    {
        JSONObject jsObj = new JSONObject( );
        try
        {
            Form form = FormService.findFormLightByPrimaryKey( nIdForm );
            if ( form != null )
            {
                jsObj.put( FORM, _mapper.writeValueAsString( form ) );
                Category category = CategoryService.findCategoryById( form.getIdCategory( ) );
                if ( category != null )
                {
                    jsObj.put( CATEGORY, _mapper.writeValueAsString( category ) );
                }
                int nIdWorkflow = form.getIdWorkflow( );
                if ( nIdWorkflow > 0 )
                {
                    jsObj.put( WORKFLOW, WorkflowTraderService.exportWorkflowToJson( nIdWorkflow, Locale.getDefault( ) ) );
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
        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( "Error during export of the form into json object", e );
        }
        return jsObj;
    }

    /**
     * Import the form rule part of a form from a json object
     * 
     * @param jsonObject
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static void importFormRule( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        FormRule formRule = null;
        Object objectFormRule = jsonObject.get( FORM_RULE );
        if ( objectFormRule != null )
        {
            formRule = _mapper.readValue( objectFormRule.toString( ), FormRule.class );
        }
        if ( formRule != null )
        {
            formRule.setIdForm( nIdForm );
            FormRuleService.saveFormRule( formRule );
        }
    }

    /**
     * Import the display part of a form from a json object
     * 
     * @param jsonObject
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static void importDisplay( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        Display display = null;
        Object objectDisplay = jsonObject.get( DISPLAY );
        if ( objectDisplay != null )
        {
            display = _mapper.readValue( objectDisplay.toString( ), Display.class );
        }
        if ( display != null )
        {
            display.setIdForm( nIdForm );
            DisplayService.saveDisplay( display );
        }
    }

    /**
     * Import the localization part of a form from a json object
     * 
     * @param jsonObject
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static void importLocalization( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        Localization localization = null;
        Object objectLocalization = jsonObject.get( LOCALIZATION );
        if ( objectLocalization != null )
        {
            localization = _mapper.readValue( objectLocalization.toString( ), Localization.class );
        }
        if ( localization != null )
        {
            localization.setIdForm( nIdForm );
            LocalizationService.saveLocalization( localization );
        }
    }

    /**
     * Import the form message part of a form from a json object
     * 
     * @param jsonObject
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static void importFormMessage( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        FormMessage formMessage = null;
        Object objectFormMessage = jsonObject.get( FORM_MESSAGE );
        if ( objectFormMessage != null )
        {
            formMessage = _mapper.readValue( objectFormMessage.toString( ), FormMessage.class );
        }
        if ( formMessage != null )
        {
            formMessage.setIdForm( nIdForm );
            FormMessageService.saveFormMessage( formMessage );
        }
    }

    /**
     * Import the reservation rule part of a form from a json object
     * 
     * @param jsonObject
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static void importReservationRules( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        List<ReservationRule> listReservationRules = new ArrayList<>( );
        JSONArray jsArrayReservationRules = null;
        if ( jsonObject.containsKey( RESERVATION_RULES ) )
        {
            jsArrayReservationRules = jsonObject.getJSONArray( RESERVATION_RULES );
        }
        if ( CollectionUtils.isNotEmpty( jsArrayReservationRules ) )
        {
            listReservationRules = Arrays.asList( _mapper.readValue( jsArrayReservationRules.toString( ), ReservationRule [ ].class ) );
        }
        for ( ReservationRule reservationRule : listReservationRules )
        {
            reservationRule.setIdForm( nIdForm );
            ReservationRuleService.saveReservationRule( reservationRule );
        }
    }

    /**
     * Import the closing days of a form from a json object
     * 
     * @param jsonObject
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static void importClosingDays( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        List<ClosingDay> listClosingDays = new ArrayList<>( );
        JSONArray jsArrayClosingDays = null;
        if ( jsonObject.containsKey( CLOSING_DAYS ) )
        {
            jsArrayClosingDays = jsonObject.getJSONArray( CLOSING_DAYS );
        }
        if ( CollectionUtils.isNotEmpty( jsArrayClosingDays ) )
        {
            listClosingDays = Arrays.asList( _mapper.readValue( jsArrayClosingDays.toString( ), ClosingDay [ ].class ) );
        }
        for ( ClosingDay closingDay : listClosingDays )
        {
            closingDay.setIdForm( nIdForm );
            ClosingDayService.saveClosingDay( closingDay );
        }
    }

    /**
     * Import the week definition of a form from a json object
     * 
     * @param jsonObject
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static void importWeekDefinitions( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        List<WeekDefinition> listWeekDefinitions = new ArrayList<>( );
        HashMap<Integer, Integer> mapIdWeekDefinition = new HashMap<>( );
        HashMap<Integer, Integer> mapIdWorkingDay = new HashMap<>( );
        JSONArray jsArrayWeekDefinitions = null;
        int nOldIdWeekDefinition;
        int nNewIdWeekDefinition;
        int nOldIdWorkingDay;
        int nNewIdWorkingDay;
        if ( jsonObject.containsKey( WEEK_DEFINITIONS ) )
        {
            jsArrayWeekDefinitions = jsonObject.getJSONArray( WEEK_DEFINITIONS );
        }
        if ( CollectionUtils.isNotEmpty( jsArrayWeekDefinitions ) )
        {
            listWeekDefinitions = Arrays.asList( _mapper.readValue( jsArrayWeekDefinitions.toString( ), WeekDefinition [ ].class ) );
        }
        for ( WeekDefinition weekDefinition : listWeekDefinitions )
        {
            nOldIdWeekDefinition = weekDefinition.getIdWeekDefinition( );
            weekDefinition.setIdForm( nIdForm );
            weekDefinition = WeekDefinitionService.saveWeekDefinition( weekDefinition );
            mapIdWeekDefinition.put( nOldIdWeekDefinition, weekDefinition.getIdWeekDefinition( ) );
            for ( WorkingDay workingDay : weekDefinition.getListWorkingDay( ) )
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
                for ( TimeSlot timeSlot : workingDay.getListTimeSlot( ) )
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

    /**
     * import the slots of a form from a json object
     * 
     * @param jsonObject
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static void importSlots( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        List<Slot> listSlots = new ArrayList<>( );
        JSONArray jsArraySlots = null;
        if ( jsonObject.containsKey( SLOTS ) )
        {
            jsArraySlots = jsonObject.getJSONArray( SLOTS );
        }
        if ( CollectionUtils.isNotEmpty( jsArraySlots ) )
        {
            listSlots = Arrays.asList( _mapper.readValue( jsArraySlots.toString( ), Slot [ ].class ) );
        }
        for ( Slot slot : listSlots )
        {
            slot.setIdForm( nIdForm );
            slot.setIdSlot( 0 );
            SlotService.saveSlot( slot );
        }
    }

    /**
     * Import the generic attributes of a form from a json object
     * 
     * @param jsonObject
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static void importEntries( JSONObject jsonObject, int nIdForm ) throws JsonParseException, JsonMappingException, IOException
    {
        List<Entry> listEntries = new ArrayList<>( );
        List<Field> listFields = new ArrayList<>( );
        HashMap<Integer, Integer> mapIdEntry = new HashMap<>( );
        JSONArray jsArrayEntries = null;
        JSONArray jsArrayFields = null;
        int nOldIdEntry;
        int nNewIdEntry;
        int nNewIdField;
        if ( jsonObject.containsKey( ENTRIES ) )
        {
            jsArrayEntries = jsonObject.getJSONArray( ENTRIES );
        }
        if ( jsonObject.containsKey( FIELDS ) )
        {
            jsArrayFields = jsonObject.getJSONArray( FIELDS );
        }
        if ( CollectionUtils.isNotEmpty( jsArrayEntries ) )
        {
            listEntries = Arrays.asList( _mapper.readValue( jsArrayEntries.toString( ), Entry [ ].class ) );
        }
        for ( Entry entry : listEntries )
        {
            nOldIdEntry = entry.getIdEntry( );
            entry.setIdResource( nIdForm );
            nNewIdEntry = EntryHome.create( entry );
            mapIdEntry.put( nOldIdEntry, nNewIdEntry );
        }
        if ( CollectionUtils.isNotEmpty( jsArrayFields ) )
        {
            listFields = Arrays.asList( _mapper.readValue( jsArrayFields.toString( ), Field [ ].class ) );
        }
        HashMap<String, RegularExpression> mapRegularExpression = new HashMap<>( );
        List<RegularExpression> listRegularExpressionsInDB = RegularExpressionService.getInstance( ).getAllRegularExpression( );
        for ( RegularExpression regularExpression : listRegularExpressionsInDB )
        {
            if ( !mapRegularExpression.containsKey( regularExpression.getTitle( ) ) )
            {
                mapRegularExpression.put( regularExpression.getTitle( ), regularExpression );
            }
        }
        for ( Field field : listFields )
        {
            nNewIdEntry = 0;
            if ( field.getParentEntry( ) != null && mapIdEntry.containsKey( field.getParentEntry( ).getIdEntry( ) ) )
            {
                nNewIdEntry = mapIdEntry.get( field.getParentEntry( ).getIdEntry( ) );
            }
            field.setParentEntry( EntryHome.findByPrimaryKey( nNewIdEntry ) );
            nNewIdField = FieldHome.create( field );
            List<RegularExpression> listRegularExpressionsToImport = field.getRegularExpressionList( );
            int nIdExpressionToGet;
            for ( RegularExpression regularExpression : listRegularExpressionsToImport )
            {
                if ( mapRegularExpression.containsKey( regularExpression.getTitle( ) ) )
                {
                    nIdExpressionToGet = mapRegularExpression.get( regularExpression.getTitle( ) ).getIdExpression( );
                }
                else
                {
                    RegularExpressionHome.create( regularExpression, _plugin );
                    nIdExpressionToGet = regularExpression.getIdExpression( );
                }
                FieldHome.createVerifyBy( nNewIdField, nIdExpressionToGet );
            }
        }
    }

    /**
     * Export and save in database the form rule of a form from a json object
     * 
     * @param jsObj
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonProcessingException
     */
    private static void exportFormRule( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        FormRule formRule = FormRuleService.findFormRuleWithFormId( nIdForm );
        if ( formRule != null )
        {
            jsObj.put( FORM_RULE, _mapper.writeValueAsString( formRule ) );
        }
    }

    /**
     * Export and save in database the display of a form from a json object
     * 
     * @param jsObj
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonProcessingException
     */
    private static void exportDisplay( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        Display display = DisplayService.findDisplayWithFormId( nIdForm );
        if ( display != null )
        {
            jsObj.put( DISPLAY, _mapper.writeValueAsString( display ) );
        }
    }

    /**
     * Export and save in database the localization of a form from a json object
     * 
     * @param jsObj
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonProcessingException
     */
    private static void exportLocalization( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        Localization localization = LocalizationService.findLocalizationWithFormId( nIdForm );
        if ( localization != null )
        {
            jsObj.put( LOCALIZATION, _mapper.writeValueAsString( localization ) );
        }
    }

    /**
     * Export and save in database the form message of a form from a json object
     * 
     * @param jsObj
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonProcessingException
     */
    private static void exportFormMessage( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        FormMessage formMessage = FormMessageService.findFormMessageByIdForm( nIdForm );
        if ( formMessage != null )
        {
            jsObj.put( FORM_MESSAGE, _mapper.writeValueAsString( formMessage ) );
        }
    }

    /**
     * Export and save in database the reservation rules of a form from a json object
     * 
     * @param jsObj
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonProcessingException
     */
    private static void exportReservationRules( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        JSONArray jsReservationRules = new JSONArray( );
        for ( ReservationRule reservationRule : ReservationRuleService.findListReservationRule( nIdForm ) )
        {
            jsReservationRules.add( _mapper.writeValueAsString( reservationRule ) );
        }
        if ( CollectionUtils.isNotEmpty( jsReservationRules ) )
        {
            jsObj.put( RESERVATION_RULES, jsReservationRules );
        }
    }

    /**
     * Export and save in database the closing days of a form from a json object
     * 
     * @param jsObj
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonProcessingException
     */
    private static void exportClosingDays( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        JSONArray jsClosingDays = new JSONArray( );
        for ( ClosingDay closingDay : ClosingDayService.findListClosingDay( nIdForm ) )
        {

            jsClosingDays.add( _mapper.writeValueAsString( closingDay ) );
        }
        if ( CollectionUtils.isNotEmpty( jsClosingDays ) )
        {
            jsObj.put( CLOSING_DAYS, jsClosingDays );
        }
    }

    /**
     * Export and save in database the week definition of a form from a json object
     * 
     * @param jsObj
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonProcessingException
     */
    private static void exportWeekDefinitions( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        JSONArray jsWeekDefinitions = new JSONArray( );
        for ( WeekDefinition weekDefinition : WeekDefinitionService.findListWeekDefinition( nIdForm ) )
        {
            jsWeekDefinitions.add( _mapper.writeValueAsString( weekDefinition ) );
        }
        if ( CollectionUtils.isNotEmpty( jsWeekDefinitions ) )
        {
            jsObj.put( WEEK_DEFINITIONS, jsWeekDefinitions );
        }
    }

    /**
     * Export and save in database the slots of a form from a json object
     * 
     * @param jsObj
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonProcessingException
     */
    private static void exportSlots( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        JSONArray jsSlots = new JSONArray( );
        for ( Slot slot : SlotService.findListSlot( nIdForm ) )
        {

            jsSlots.add( _mapper.writeValueAsString( slot ) );
        }
        if ( CollectionUtils.isNotEmpty( jsSlots ) )
        {
            jsObj.put( SLOTS, jsSlots );
        }
    }

    /**
     * Export and save in database the generic attributes of a form from a json object
     * 
     * @param jsObj
     *            the json object
     * @param nIdForm
     *            the form id
     * @throws JsonProcessingException
     */
    private static void exportEntries( JSONObject jsObj, int nIdForm ) throws JsonProcessingException
    {
        JSONArray jsEntries = new JSONArray( );
        JSONArray jsFields = new JSONArray( );
        for ( Entry entry : EntryService.findListEntry( nIdForm ) )
        {
            jsEntries.add( _mapper.writeValueAsString( entry ) );
            for ( Field fieldLight : entry.getFields( ) )
            {
                Field fullField = FieldHome.findByPrimaryKey( fieldLight.getIdField( ) );
                jsFields.add( _mapper.writeValueAsString( fullField ) );
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
