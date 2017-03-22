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

public class ClosingDayService {

	private static final String MARK_EXCEL_EXTENSION_XLSX = "xlsx";
	private static final String MARK_FORMAT_DATE_REGEX = "([0-9]{2})/([0-9]{2})/([0-9]{4})";	

	public static List<LocalDate> findListDateOfClosingDayByIdFormAndDateRange(int nIdForm, LocalDate startingDate,
			LocalDate endingDate) {
		List<LocalDate> listDate = new ArrayList<>();
		List<ClosingDay> listClosingDay = ClosingDayHome.findByIdFormAndDateRange(nIdForm, startingDate, endingDate);
		for (ClosingDay closingDay : listClosingDay) {
			listDate.add(closingDay.getDateOfClosingDay());
		}
		return listDate;
	}

	public static List<LocalDate> findListDateOfClosingDayByIdForm(int nIdForm) {
		List<LocalDate> listDate = new ArrayList<>();
		List<ClosingDay> listClosingDay = ClosingDayHome.findByIdForm(nIdForm);
		for (ClosingDay closingDay : listClosingDay) {
			listDate.add(closingDay.getDateOfClosingDay());
		}
		return listDate;
	}

	public static void saveListClosingDay(int nIdForm, List<LocalDate> listClosingDate) {
		for (LocalDate closingDate : listClosingDate) {
			saveClosingDay(nIdForm, closingDate);
		}
	}
	
	public static void saveClosingDay(int nIdForm, LocalDate closingDate) {
		ClosingDay closingDay = new ClosingDay();
		closingDay.setIdForm(nIdForm);
		closingDay.setDateOfClosingDay(closingDate);		
		ClosingDayHome.create(closingDay);
	}

	public static List<LocalDate> getImportClosingDays(FileItem item) throws IOException {
		HashSet<LocalDate> listDays = new HashSet<LocalDate>();
		FileInputStream fis = null;
		Workbook workbook = null;
		String strExtension = FilenameUtils.getExtension(item.getName());
		if (StringUtils.equals(MARK_EXCEL_EXTENSION_XLSX, strExtension)) {
			try {
				fis = (FileInputStream) item.getInputStream();
				// Using XSSF for xlsx format, for xls use HSSF
				workbook = new XSSFWorkbook(fis);
				int numberOfSheets = workbook.getNumberOfSheets();
				// looping over each workbook sheet
				for (int i = 0; i < numberOfSheets; i++) {
					Sheet sheet = workbook.getSheetAt(i);
					Iterator<Row> rowIterator = sheet.iterator();
					// iterating over each row
					while (rowIterator.hasNext()) {
						Row row = (Row) rowIterator.next();
						if (row.getRowNum() > 1) {
							Iterator<Cell> cellIterator = row.cellIterator();
							// Iterating over each cell (column wise) in a
							// particular row.
							while (cellIterator.hasNext()) {
								Cell cell = (Cell) cellIterator.next();
								// The Cell Containing String will is name.
								if (cell.getColumnIndex() == 3) {
									String strdate = StringUtils.EMPTY;
									if (cell.getCellType() == 0) {
										Instant instant = cell.getDateCellValue().toInstant();
										LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
										strdate = localDate.format(Utilities.formatter);
									}									
									if (StringUtils.isNotEmpty(strdate) && strdate.matches(MARK_FORMAT_DATE_REGEX)) {
											LocalDate date = LocalDate.parse(strdate, Utilities.formatter);
											listDays.add(date);										
									}
								}
							}
						}
					}
				}
			} finally {
				fis.close();
				workbook.close();
			}
		}
		return new ArrayList<LocalDate>(listDays);
	}

}
