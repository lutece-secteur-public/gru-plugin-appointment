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
import org.apache.commons.lang.StringUtils;
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
    private static final String MARK_FORMAT_DATE_REGEX = "([0-9]{2})/([0-9]{2})/([0-9]{4})";

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
        HashSet<LocalDate> listDays = new HashSet<LocalDate>( );
        FileInputStream fis = null;
        Workbook workbook = null;
        String strExtension = FilenameUtils.getExtension( item.getName( ) );
        if ( StringUtils.equals( MARK_EXCEL_EXTENSION_XLSX, strExtension ) )
        {
            try
            {
                fis = (FileInputStream) item.getInputStream( );
                // Using XSSF for xlsx format, for xls use HSSF
                workbook = new XSSFWorkbook( fis );
                int numberOfSheets = workbook.getNumberOfSheets( );
                // looping over each workbook sheet
                for ( int i = 0; i < numberOfSheets; i++ )
                {
                    Sheet sheet = workbook.getSheetAt( i );
                    Iterator<Row> rowIterator = sheet.iterator( );
                    // iterating over each row
                    while ( rowIterator.hasNext( ) )
                    {
                        Row row = (Row) rowIterator.next( );
                        if ( row.getRowNum( ) > 1 )
                        {
                            Iterator<Cell> cellIterator = row.cellIterator( );
                            // Iterating over each cell (column wise) in a
                            // particular row.
                            while ( cellIterator.hasNext( ) )
                            {
                                Cell cell = (Cell) cellIterator.next( );
                                // The Cell Containing String will is name.
                                if ( cell.getColumnIndex( ) == 3 )
                                {
                                    String strdate = StringUtils.EMPTY;
                                    if ( cell.getCellType( ) == 0 )
                                    {
                                        Instant instant = cell.getDateCellValue( ).toInstant( );
                                        LocalDate localDate = instant.atZone( ZoneId.systemDefault( ) ).toLocalDate( );
                                        strdate = localDate.format( Utilities.getFormatter( ) );
                                    }
                                    if ( StringUtils.isNotEmpty( strdate ) && strdate.matches( MARK_FORMAT_DATE_REGEX ) )
                                    {
                                        LocalDate date = LocalDate.parse( strdate, Utilities.getFormatter( ) );
                                        listDays.add( date );
                                    }
                                }
                            }
                        }
                    }
                }
            }
            finally
            {
                if ( fis != null )
                {
                    fis.close( );
                }
                if ( workbook != null )
                {
                    workbook.close( );
                }
            }
        }
        return new ArrayList<LocalDate>( listDays );
    }

}
