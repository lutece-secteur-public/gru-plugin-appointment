/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.appointment.service.export;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.Utilities;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.ReferenceList;

public final class AppointmentExportService
{

    private static final String KEY_RESOURCE_TYPE = "appointment.appointment.name";
    private static final String KEY_COLUMN_LAST_NAME = "appointment.manageAppointments.columnLastName";
    private static final String KEY_COLUMN_FIRST_NAME = "appointment.manageAppointments.columnFirstName";
    private static final String KEY_COLUMN_EMAIL = "appointment.manageAppointments.columnEmail";
    private static final String KEY_COLUMN_DATE_APPOINTMENT = "appointment.dateAppointment.title";
    private static final String KEY_TIME_START = "appointment.model.entity.appointmentform.attribute.timeStart";
    private static final String KEY_TIME_END = "appointment.model.entity.appointmentform.attribute.timeEnd";
    private static final String KEY_COLUMN_ADMIN = "appointment.manageAppointments.columnAdmin";
    private static final String KEY_COLUMN_STATUS = "appointment.labelStatus";
    private static final String KEY_COLUMN_STATE = "appointment.manageAppointments.columnState";
    private static final String KEY_COLUMN_NB_BOOKED_SEATS = "appointment.manageAppointments.columnNumberOfBookedseatsPerAppointment";
    private static final String KEY_DATE_APPOINT_TAKEN = "appointment.model.entity.appointmentform.attribute.dateTaken";
    private static final String KEY_HOUR_APPOINT_TAKEN = "appointment.model.entity.appointmentform.attribute.hourTaken";

    private static final String CONSTANT_COMMA = ",";

    private static final List<String> DEFAULT_COLUMN_LIST = Arrays.asList( KEY_COLUMN_LAST_NAME, KEY_COLUMN_FIRST_NAME, KEY_COLUMN_EMAIL,
            KEY_COLUMN_DATE_APPOINTMENT, KEY_TIME_START, KEY_TIME_END, KEY_COLUMN_ADMIN, KEY_COLUMN_STATUS, KEY_COLUMN_STATE, KEY_COLUMN_NB_BOOKED_SEATS,
            KEY_DATE_APPOINT_TAKEN, KEY_HOUR_APPOINT_TAKEN );

    private AppointmentExportService( )
    {
    }

    /**
     * Build the excel fil of the list of the appointments found in the manage appointment viw by filter
     * 
     * @param strIdForm
     *            the form id
     * @param excelFile
     *            the excel file to write
     * @param defaultColumnList
     *            the default columns to export
     * @param entryList
     *            the entries to export
     * @param locale
     *            the local
     * @param listAppointmentsDTO
     *            the list of the appointments to input in the excel file
     */
    public static void buildExcelFileWithAppointments( String strIdForm, List<String> defaultColumnList, List<Integer> entryList, Path excelFile, Locale locale,
            List<AppointmentDTO> listAppointmentsDTO )
    {
        AppointmentFormDTO tmpForm = FormService.buildAppointmentFormLight( Integer.parseInt( strIdForm ) );
        List<List<Object>> linesValues = new ArrayList<>( );
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( Integer.valueOf( strIdForm ) );
        List<Entry> listEntry = EntryHome.getEntryList( entryFilter ).stream( ).filter( e -> entryList.contains( e.getIdEntry( ) ) )
                .collect( Collectors.toList( ) );

        if ( tmpForm == null )
        {
            return;
        }

        linesValues.add( Collections.singletonList( tmpForm.getTitle( ) ) );
        linesValues.add( createHeaderContent( defaultColumnList, listEntry, locale ) );

        if ( listAppointmentsDTO != null )
        {
        	StateService stateService = null;

            Map<Integer, String> mapDefaultValueGenAttBackOffice = createDefaultValueMap( listEntry );
            if ( WorkflowService.getInstance( ).isAvailable( ) )
            {
            	 stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
            }
            for ( AppointmentDTO appointmentDTO : listAppointmentsDTO )
            {
                linesValues.add( createLineContent( appointmentDTO, tmpForm.getIdWorkflow( ), defaultColumnList, listEntry, mapDefaultValueGenAttBackOffice,
                        stateService, locale ) );
            }
        }
        writeWorkbook( linesValues, excelFile, locale );
    }

    private static final Map<Integer, String> createDefaultValueMap( List<Entry> listEntry )
    {
        Map<Integer, String> mapDefaultValueGenAttBackOffice = new HashMap<>( );
        for ( Entry e : listEntry )
        {
            if ( !e.isOnlyDisplayInBack( ) )
            {
                continue;
            }
            e = EntryHome.findByPrimaryKey( e.getIdEntry( ) );
            if ( e.getFields( ) != null && e.getFields( ).size( ) == 1 && StringUtils.isNotEmpty( e.getFields( ).get( 0 ).getValue( ) ) )
            {
                mapDefaultValueGenAttBackOffice.put( e.getIdEntry( ), e.getFields( ).get( 0 ).getValue( ) );
            }
            else
                if ( e.getFields( ) != null )
                {
                    for ( Field field : e.getFields( ) )
                    {
                        if ( field.isDefaultValue( ) )
                        {
                            mapDefaultValueGenAttBackOffice.put( e.getIdEntry( ), field.getValue( ) );
                        }
                    }
                }
        }
        return mapDefaultValueGenAttBackOffice;
    }

    private static final void writeWorkbook( List<List<Object>> linesValues, Path excelFile, Locale locale )
    {
        int nRownum = 0;
        try ( XSSFWorkbook workbook = new XSSFWorkbook( ) ; OutputStream os = Files.newOutputStream( excelFile ) )
        {
            XSSFSheet sheet = workbook.createSheet( I18nService.getLocalizedString( KEY_RESOURCE_TYPE, locale ) );
            for ( List<Object> line : linesValues )
            {
                Row row = sheet.createRow( nRownum++ );
                int nCellnum = 0;
                for ( Object cellValue : line )
                {
                    Cell cell = row.createCell( nCellnum++ );
                    if ( cellValue instanceof String )
                    {
                        cell.setCellValue( (String) cellValue );
                    }
                    else
                        if ( cellValue instanceof Boolean )
                        {
                            cell.setCellValue( (Boolean) cellValue );
                        }
                        else
                            if ( cellValue instanceof Date )
                            {
                                cell.setCellValue( (Date) cellValue );
                            }
                            else
                                if ( cellValue instanceof Double )
                                {
                                    cell.setCellValue( (Double) cellValue );
                                }
                }
            }
            workbook.write( os );
        }
        catch( IOException e )
        {
            AppLogService.error( e );
        }
    }

    private static final List<Object> createHeaderContent( List<String> defaultColumnList, List<Entry> listEntry, Locale locale )
    {
        List<Object> strInfos = new ArrayList<>( );
        for ( String key : defaultColumnList )
        {
            strInfos.add( I18nService.getLocalizedString( key, locale ) );
        }

        if ( CollectionUtils.isNotEmpty( listEntry ) )
        {
            for ( Entry e : listEntry )
            {
                strInfos.add( e.getTitle( ) );
            }
        }
        return strInfos;
    }

    private static final List<Object> createLineContent( AppointmentDTO appointmentDTO, int idWorkflow, List<String> defaultColumnList, List<Entry> listEntry,
            Map<Integer, String> mapDefaultValueGenAttBackOffice, StateService stateService, Locale locale )
    {
        List<Object> strWriter = new ArrayList<>( );
        addDefaultColumnValues( appointmentDTO, idWorkflow, defaultColumnList, strWriter, stateService, locale );

        List<Integer> listIdResponse = AppointmentResponseService.findListIdResponse( appointmentDTO.getIdAppointment( ) );
        List<Response> listResponses = new ArrayList<>( );
        for ( int nIdResponse : listIdResponse )
        {
            Response resp = ResponseHome.findByPrimaryKey( nIdResponse );
            if ( resp != null )
            {
                listResponses.add( resp );
            }
        }
        for ( Entry e : listEntry )
        {
            String value = getEntryValue( e, listResponses, mapDefaultValueGenAttBackOffice );
            if ( StringUtils.isNotEmpty( value ) )
            {
                strWriter.add( value );
            }
        }
        return strWriter;
    }

    private static final void addDefaultColumnValues( AppointmentDTO appointmentDTO, int idWorkflow, List<String> defaultColumnList, List<Object> strWriter,
            StateService stateService, Locale locale )
    {
        if ( defaultColumnList.contains( KEY_COLUMN_LAST_NAME ) )
        {
            strWriter.add( appointmentDTO.getLastName( ) );
        }
        if ( defaultColumnList.contains( KEY_COLUMN_FIRST_NAME ) )
        {
            strWriter.add( appointmentDTO.getFirstName( ) );
        }
        if ( defaultColumnList.contains( KEY_COLUMN_EMAIL ) )
        {
            strWriter.add( appointmentDTO.getEmail( ) );
        }
        if ( defaultColumnList.contains( KEY_COLUMN_DATE_APPOINTMENT ) )
        {
            strWriter.add( appointmentDTO.getDateOfTheAppointment( ) );
        }
        if ( defaultColumnList.contains( KEY_TIME_START ) )
        {
            strWriter.add( appointmentDTO.getStartingTime( ).toString( ) );
        }
        if ( defaultColumnList.contains( KEY_TIME_END ) )
        {
            strWriter.add( appointmentDTO.getEndingTime( ).toString( ) );
        }
        if ( defaultColumnList.contains( KEY_COLUMN_ADMIN ) )
        {
            strWriter.add( appointmentDTO.getAdminUser( ) );
        }
        if ( defaultColumnList.contains( KEY_COLUMN_STATUS ) )
        {
            String status = I18nService.getLocalizedString( AppointmentDTO.PROPERTY_APPOINTMENT_STATUS_RESERVED, locale );
            if ( appointmentDTO.getIsCancelled( ) )
            {
                status = I18nService.getLocalizedString( AppointmentDTO.PROPERTY_APPOINTMENT_STATUS_UNRESERVED, locale );
            }
            strWriter.add( status );
        }
        if ( defaultColumnList.contains( KEY_COLUMN_STATE ) )
        {
            String strState = StringUtils.EMPTY; 
        	if( stateService != null) {
        		
	            State stateAppointment = stateService.findByResource( appointmentDTO.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE, idWorkflow );
	            if ( stateAppointment != null )
	            {
	                appointmentDTO.setState( stateAppointment );
	                strState = stateAppointment.getName( );
	            }
        	}
	            strWriter.add( strState );
        	
        }
        if ( defaultColumnList.contains( KEY_COLUMN_NB_BOOKED_SEATS ) )
        {
            strWriter.add( Integer.toString( appointmentDTO.getNbBookedSeats( ) ) );
        }
        if ( defaultColumnList.contains( KEY_DATE_APPOINT_TAKEN ) )
        {
            strWriter.add( appointmentDTO.getDateAppointmentTaken( ).toLocalDate( ).format( Utilities.getFormatter( ) ) );
        }
        if ( defaultColumnList.contains( KEY_HOUR_APPOINT_TAKEN ) )
        {
            strWriter.add( appointmentDTO.getDateAppointmentTaken( ).toLocalTime( ).withSecond( 0 ).toString( ) );
        }
    }

    private static final String getEntryValue( Entry e, List<Response> listResponses, Map<Integer, String> mapDefaultValueGenAttBackOffice )
    {
        Integer key = e.getIdEntry( );
        StringBuilder strValue = new StringBuilder( );
        String strPrefix = StringUtils.EMPTY;
        for ( Response resp : listResponses )
        {
            String strRes = StringUtils.EMPTY;
            if ( key.equals( resp.getEntry( ).getIdEntry( ) ) )
            {
                Field f = resp.getField( );
                int nfield = 0;
                if ( f != null )
                {
                    nfield = f.getIdField( );
                    Field field = FieldHome.findByPrimaryKey( nfield );
                    if ( field != null )
                    {
                        strRes = field.getTitle( );
                    }
                }
                else
                {
                    strRes = resp.getResponseValue( );
                }
            }
            if ( StringUtils.isNotEmpty( strRes ) )
            {
                strValue.append( strPrefix + strRes );
                strPrefix = CONSTANT_COMMA;
            }
        }
        if ( StringUtils.isEmpty( strValue.toString( ) ) && mapDefaultValueGenAttBackOffice.containsKey( key ) )
        {
            strValue.append( mapDefaultValueGenAttBackOffice.get( key ) );
        }
        return strValue.toString( );
    }

    public static ReferenceList getDefaultColumnList( Locale locale )
    {
        ReferenceList refList = new ReferenceList( );
        for ( String key : DEFAULT_COLUMN_LIST )
        {
            refList.addItem( key, I18nService.getLocalizedString( key, locale ) );
        }
        return refList;
    }

    public static ReferenceList getCustomColumnList( String strIdForm )
    {
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( Integer.valueOf( strIdForm ) );
        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );

        ReferenceList refList = new ReferenceList( );
        for ( Entry entry : listEntry )
        {
            refList.addItem( String.valueOf( entry.getIdEntry( ) ), entry.getTitle( ) );
        }
        return refList;
    }
}
