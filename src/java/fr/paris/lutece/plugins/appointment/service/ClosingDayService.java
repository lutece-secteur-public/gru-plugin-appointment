package fr.paris.lutece.plugins.appointment.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.planning.ClosingDay;
import fr.paris.lutece.plugins.appointment.business.planning.ClosingDayHome;

public class ClosingDayService {

	public static List<LocalDate> findListDateOfClosingDayByIdFormAndDateRange(int nIdForm, LocalDate startingDate, LocalDate endingDate){
		List<LocalDate> listDate = new ArrayList<>();
		List<ClosingDay> listClosingDay = ClosingDayHome.findByIdFormAndDateRange(nIdForm, startingDate, endingDate);
		for (ClosingDay closingDay : listClosingDay){
			listDate.add(closingDay.getDateOfClosingDay());
		}
		return listDate;
	}
	
}
