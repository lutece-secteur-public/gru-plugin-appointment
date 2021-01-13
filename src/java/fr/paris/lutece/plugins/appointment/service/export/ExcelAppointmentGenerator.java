/*
 * Copyright (c) 2002-2021, City of Paris
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.filegenerator.service.IFileGenerator;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.file.FileUtil;

public class ExcelAppointmentGenerator implements IFileGenerator
{
    private static final String KEY_RESOURCE_TYPE = "appointment.appointment.name";
    private static final String KEY_FILE_DESCRIPTION = "appointment.export.file.description";
    private static final String EXCEL_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String TMP_DIR = System.getProperty( "java.io.tmpdir" );
    private static final boolean ZIP_EXPORT = Boolean.parseBoolean( AppPropertiesService.getProperty( "appointment.export.xls.zip", "false" ) );
    private static final String EXCEL_FILE_EXTENSION = ".xlsx";

    private final String _fileName;
    private final String _fileDescription;
    private final String _strIdForm;
    private final List<String> _defaultColumnList;
    private final Locale _locale;
    private final List<AppointmentDTO> _listAppointmentsDTO;
    private final List<Integer> _entryList;

    public ExcelAppointmentGenerator( String strIdForm, List<String> defaultColumnList, Locale locale, List<AppointmentDTO> listAppointmentsDTO,
            List<Integer> entryList )
    {
        super( );
        _fileName = new SimpleDateFormat( "yyyyMMdd-hhmm" ).format( Calendar.getInstance( locale ).getTime( ) ) + "_"
                + I18nService.getLocalizedString( KEY_RESOURCE_TYPE, locale );
        _fileDescription = I18nService.getLocalizedString( KEY_FILE_DESCRIPTION, locale );
        this._strIdForm = strIdForm;
        this._defaultColumnList = new ArrayList<>( defaultColumnList );
        this._locale = locale;
        this._listAppointmentsDTO = new ArrayList<>( listAppointmentsDTO );
        this._entryList = new ArrayList<>( entryList );
    }

    @Override
    public Path generateFile( ) throws IOException
    {
        Path excelFile = Paths.get( TMP_DIR, _fileName + EXCEL_FILE_EXTENSION );
        AppointmentExportService.buildExcelFileWithAppointments( _strIdForm, _defaultColumnList, _entryList, excelFile, _locale, _listAppointmentsDTO );
        return excelFile;
    }

    @Override
    public String getFileName( )
    {

        return _fileName + ( isZippable( ) ? FileUtil.EXTENSION_ZIP : EXCEL_FILE_EXTENSION );
    }

    @Override
    public String getMimeType( )
    {
        return isZippable( ) ? FileUtil.CONSTANT_MIME_TYPE_ZIP : EXCEL_MIME_TYPE;
    }

    @Override
    public String getDescription( )
    {
        return _fileDescription;
    }

    @Override
    public boolean isZippable( )
    {
        return ZIP_EXPORT;
    }
}
