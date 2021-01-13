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
package fr.paris.lutece.plugins.appointment.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.paris.lutece.plugins.appointment.business.planning.ClosingDay;
import fr.paris.lutece.plugins.appointment.business.planning.ClosingDayHome;

/**
 * Service class for the closing day
 * 
 * @author Laurent Payen
 *
 */
public final class ClosingDayService
{

    private static final String MARK_EXCEL_EXTENSION_XLSX = "xlsx";

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private ClosingDayService( )
    {
    }

    /**
     * Find all the closing dates of the form on a given period
     * 
     * @param nIdForm
     *            the form Id
     * @param startingDate
     *            the starting date
     * @param endingDate
     *            the ending date
     * @return a list of closing dates
     */
    public static List<LocalDate> findListDateOfClosingDayByIdFormAndDateRange( int nIdForm, LocalDate startingDate, LocalDate endingDate )
    {
        List<LocalDate> listDate = new ArrayList<>( );
        List<ClosingDay> listClosingDay = ClosingDayHome.findByIdFormAndDateRange( nIdForm, startingDate, endingDate );
        for ( ClosingDay closingDay : listClosingDay )
        {
            listDate.add( closingDay.getDateOfClosingDay( ) );
        }
        return listDate;
    }

    /**
     * Find, if it exists, the closing day of a form for a given date
     * 
     * @param nIdForm
     *            the form Id
     * @param date
     *            the date
     * @return the closing day, if it exists
     */
    public static ClosingDay findClosingDayByIdFormAndDateOfClosingDay( int nIdForm, LocalDate dateOfClosingDay )
    {
        return ClosingDayHome.findByIdFormAndDateOfCLosingDay( nIdForm, dateOfClosingDay );
    }

    /**
     * Find all the dates of the closing days of the form
     * 
     * @param nIdForm
     *            the form Id
     * @return the list of closing dates
     */
    public static List<LocalDate> findListDateOfClosingDayByIdForm( int nIdForm )
    {
        List<LocalDate> listDate = new ArrayList<>( );
        List<ClosingDay> listClosingDay = ClosingDayHome.findByIdForm( nIdForm );
        for ( ClosingDay closingDay : listClosingDay )
        {
            listDate.add( closingDay.getDateOfClosingDay( ) );
        }
        return listDate;
    }

    /**
     * Returns a list of the closing days of a form
     * 
     * @param nIdForm
     *            the form id
     * @return a list of the closing days of the form
     */
    public static List<ClosingDay> findListClosingDay( int nIdForm )
    {
        return ClosingDayHome.findByIdForm( nIdForm );
    }

    /**
     * Save the closing days of a form
     * 
     * @param nIdForm
     *            the form id
     * @param listClosingDate
     *            the closing dates to save
     */
    public static void saveListClosingDay( int nIdForm, List<LocalDate> listClosingDate )
    {

        for ( LocalDate closingDate : listClosingDate )
        {
            saveClosingDay( nIdForm, closingDate );
        }
    }

    /**
     * Save a closing day of a form
     * 
     * @param nIdForm
     *            the form Id
     * @param closingDate
     *            the closing date
     */
    public static void saveClosingDay( int nIdForm, LocalDate closingDate )
    {
        ClosingDay closingDay = new ClosingDay( );
        closingDay.setIdForm( nIdForm );
        closingDay.setDateOfClosingDay( closingDate );
        ClosingDayHome.create( closingDay );
    }

    /**
     * Save a closing day of a form
     * 
     * @param closingDay
     *            the closing day to save
     */
    public static void saveClosingDay( ClosingDay closingDay )
    {
        ClosingDayHome.create( closingDay );
    }

    /**
     * Remove a closing day
     * 
     * @param closingDay
     *            the closing day to remove
     */
    public static void removeClosingDay( ClosingDay closingDay )
    {
        ClosingDayHome.delete( closingDay.getIdClosingDay( ) );
    }

    /**
     * Import the closing dates of a given file
     * 
     * @param item
     *            the file in input
     * @return the list of the closing dates in the file
     * @throws IOException
     *             if error during reading file
     */
    public static List<LocalDate> getImportClosingDays( FileItem item ) throws IOException
    {
        HashSet<LocalDate> listDays = new HashSet<>( );
        String strExtension = FilenameUtils.getExtension( item.getName( ) );
        if ( !MARK_EXCEL_EXTENSION_XLSX.equals( strExtension ) )
        {
            return new ArrayList<>( );
        }
        // Using XSSF for xlsx format, for xls use HSSF
        try ( FileInputStream fis = (FileInputStream) item.getInputStream( ) ; Workbook workbook = new XSSFWorkbook( fis ) )
        {
            int numberOfSheets = workbook.getNumberOfSheets( );
            // looping over each workbook sheet
            for ( int i = 0; i < numberOfSheets; i++ )
            {
                Sheet sheet = workbook.getSheetAt( i );
                Iterator<Row> rowIterator = sheet.iterator( );
                // iterating over each row
                while ( rowIterator.hasNext( ) )
                {
                    Row row = rowIterator.next( );
                    if ( row.getRowNum( ) > 1 )
                    {
                        Iterator<Cell> cellIterator = row.cellIterator( );
                        // Iterating over each cell (column wise) in a
                        // particular row.
                        while ( cellIterator.hasNext( ) )
                        {
                            Cell cell = cellIterator.next( );
                            // The Cell Containing String will is name.
                            if ( cell.getColumnIndex( ) == 3 && cell.getCellType( ) == 0 )
                            {
                                Instant instant = cell.getDateCellValue( ).toInstant( );
                                LocalDate localDate = instant.atZone( ZoneId.systemDefault( ) ).toLocalDate( );
                                listDays.add( localDate );
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>( listDays );
    }

}
