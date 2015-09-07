package fr.paris.lutece.plugins.appointment.service.daemon;

	import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormMessagesHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.ReminderAppointment;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.web.l10n.LocaleService;

/**
 * 
 * Class Appointment Daemon Reminder
 * To send alert reminder 
 */
public class AppointmentReminderDaemon extends Daemon 

{
	//mark
	private static final String MARK_FIRST_NAME = "%%FIRST_NAME%%";
	private static final String MARK_LAST_NAME = "%%LAST_NAME%%";
	private static final String MARK_DATE_APP = "%%DATE%%";
	private static final String MARK_CANCEL_APP = "%%CANCEL_APPOINTMENT%%" ;
	private static final String MARK_PREFIX_SENDER = "@contact-everyone.fr" ;
    private static final String	MARK_SENDER_SMS = "magali.lemaire@paris.fr" ;
    private static final String MARK_ENTRY_TYPE_PHONE = "Numéro de téléphone" ;
    private static final String 	MARK_VALID_STATUT = "Validé" ;
    private static final int 	MARK_DURATION_LIMIT = 5;
    
	//properties
    private static final String PROPERTY_MAIL_SENDER_NAME = "appointment.reminder.mailSenderName";
    //constants
    private static final DateFormat dateformat = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );
    //service 
    private final StateService _stateService  = SpringContextService.getBean( StateService.BEAN_SERVICE );
    
	@Override
	public void run( ) 
	{
		Date date = new Date();
        Calendar calendar = new GregorianCalendar(  );
        calendar.setTime( date );
        Timestamp timestampDay = new Timestamp( calendar.getTimeInMillis(  ) );
		List<Appointment> listAppointments = getListAppointment( ) ;
        
        for ( Appointment appointment : listAppointments )
        {
        	Calendar cal2 = new GregorianCalendar(  );
        	Date startAppointment = appointment.getStartAppointment( ) ;
        	cal2.setTime( startAppointment );
        	Timestamp timeStartDate = new Timestamp( cal2.getTimeInMillis(  ) );
        	
        	if ( timeStartDate.getTime( ) > timestampDay.getTime( ) )
        	{
	        	long lDiffTimeStamp = Math.abs ( timestampDay.getTime() - timeStartDate.getTime( ) ) ;
	        	int nDays = ( int ) lDiffTimeStamp / (1000*60*60*24) ;
	        	int nDiffHours = ( ( int ) lDiffTimeStamp /( 60 * 60 * 1000 ) % 24 ) + ( nDays * 24 ) ;
	        	int nDiffMin =  ( nDiffHours * 60 ) + ( int ) ( lDiffTimeStamp / ( 60 * 1000 ) % 60 ) ;
	        	
	        	List<AppointmentForm> listForms =  AppointmentFormHome.getActiveAppointmentFormsList( );
	        	
	        	for ( AppointmentForm  form : listForms )
	        	{
	        		int nIdForm = form.getIdForm( ) ;
	        		List <ReminderAppointment> listReminders = AppointmentFormMessagesHome.loadListRemindersAppointments( nIdForm  ) ;
	    		
	    			for ( ReminderAppointment reminder : listReminders )
	    			{
	    				sendReminder ( appointment , reminder, startAppointment, nDiffMin, nIdForm ) ;
	    			}
	        	}
	        }
        }
	}
	/**
	 * Get list appointment
	 * @return list appointment
	 */
	private List<Appointment> getListAppointment ( )
	{
		List <Appointment> listAllAppointments = AppointmentHome.getAppointmentsList( ) ;
		List <Integer> list = new ArrayList<Integer> ( ) ;
		List<Appointment> listAppointments = new ArrayList<Appointment> ( ) ;
		
		for ( Appointment appointment : listAllAppointments )
		{
			State stateAppointment= _stateService.findByPrimaryKey( appointment.getStatus( ) ) ;
			if (stateAppointment != null )
			{
				if( stateAppointment.getName( ).equals( MARK_VALID_STATUT ) ) 
				{
					list.add( appointment.getIdAppointment( ) ) ;
				}
			}
		}
		listAppointments = AppointmentHome.getAppointmentListById( list );
		return listAppointments ;
	}
	/**
	 * Send Alert reminder
	 * @param appointment the appointment
	 * @param reminder the reminder
	 * @param startAppointment date of start appointment
	 * @param nDiffMin diffrence time in minutes
	 * @param nIdForm id form
	 */
	private void sendReminder ( Appointment appointment , ReminderAppointment reminder, Date startAppointment, int nDiffMin , int nIdForm )
	{
		int nMinTime = ( reminder.getTimeToAlert( ) * 60 ) - MARK_DURATION_LIMIT  ;
		int nMaxTime = ( reminder.getTimeToAlert( ) * 60 ) + MARK_DURATION_LIMIT  ;
		
    	if ( nDiffMin <= nMaxTime  &&  nDiffMin >= nMinTime && ( ( appointment.getHasNotify ( ) == 0 ) || ( appointment.getHasNotify ( ) != ( reminder.getRank( ) ) ) ) )
    	{
    		boolean bNotified = false ;
    		Locale locale = LocaleService.getDefault(  );
    		String strSenderMail = MailService.getNoReplyEmail(  );
    		String strSenderName = I18nService.getLocalizedString( PROPERTY_MAIL_SENDER_NAME, locale );
    		
    		String strText = reminder.getAlertMessage( ) ;
    		if ( strText!=null && !strText.isEmpty( ) )
    		{
    			strText = strText.replaceAll( MARK_FIRST_NAME, appointment.getFirstName( ) );
    			strText = strText.replaceAll( MARK_LAST_NAME, appointment.getLastName( ) );
    			strText = strText.replaceAll( MARK_DATE_APP,  dateformat.format( startAppointment ) );
    			strText = strText.replaceAll( MARK_CANCEL_APP , AppointmentApp.getCancelAppointmentUrl( appointment ) ) ;
    		}
    		if ( reminder.isEmailNotify( ) && !appointment.getEmail( ).isEmpty( ) )
    		{
        		MailService.sendMailText( appointment.getEmail( ) , PROPERTY_MAIL_SENDER_NAME, strSenderMail ,reminder.getAlertSubject( ) , strText  );
        		bNotified = true ;
        		
    		}
    		if ( reminder.isSmsNotify( ) )
    		{
    			String strRecipient = getNumberPhone( nIdForm, appointment ) ;
    			if ( !strRecipient.isEmpty( ) )
    			{
	    			strRecipient += MARK_PREFIX_SENDER ;
	    			MailService.sendMailText( strRecipient  , strSenderName ,  MARK_SENDER_SMS ,reminder.getAlertSubject( ) , strText  );
	        		bNotified = true ;
    			}
    		}
    		if ( bNotified )
    		{
    			appointment.setHasNotify( reminder.getRank( ) );
    			AppointmentHome.update( appointment );
    		}
    	}
	}
	/**
	 * Get number phone
	 * @param nIdForm the id form
	 * @param app the appointment
	 * @return number phone
	 */
	private String getNumberPhone( int nIdForm, Appointment app )
	{
			List<Integer> listResponse = AppointmentHome.findListIdResponse( app.getIdAppointment( ) );
			EntryFilter entryFilter = new EntryFilter(  );
		    entryFilter.setIdResource( Integer.valueOf( nIdForm ) );
	       
		    List<Entry> listEntry = EntryHome.getEntryList( entryFilter ) ;
	       
		    Map <Integer, EntryType> listGenatt = new HashMap <Integer, EntryType> ( );
		    String strRes = StringUtils.EMPTY;
		    
			for ( Entry e : listEntry )
			{
				if ( e.getEntryType() != null && e.getEntryType( ).getTitle( ).equals( MARK_ENTRY_TYPE_PHONE ) )
				{
					listGenatt.put( e.getIdEntry( ), e.getEntryType( ) );
				}
			}
			for( Integer id : listGenatt.keySet( ) )
			{
				for( Integer e : listResponse )
				{
					ResponseFilter respFilter = new ResponseFilter ( );
					respFilter.setIdEntry( id );
					List<Response> listResp = ResponseHome.getResponseList( respFilter );
					
					for ( Response resp :  listResp )
					{
						if ( e.equals( resp.getIdResponse( ) ) )
						{
							strRes = resp.getResponseValue( );
						}
					}
				}
			}
		return strRes ;
	}
}
