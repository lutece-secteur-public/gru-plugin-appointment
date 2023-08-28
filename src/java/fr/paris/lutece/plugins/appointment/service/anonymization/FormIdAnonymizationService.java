package fr.paris.lutece.plugins.appointment.service.anonymization;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.anonymization.AbstractTextAnonymizationService;

public class FormIdAnonymizationService extends AbstractTextAnonymizationService
{
	@Override
	protected String getAnonymisedValue(Entry entry, Response response)
	{
		return String.valueOf( entry.getIdResource( ) );
	}

}
