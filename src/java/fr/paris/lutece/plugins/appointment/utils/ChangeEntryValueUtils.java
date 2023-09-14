package fr.paris.lutece.plugins.appointment.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class ChangeEntryValueUtils {
	
	//Properties
    private static final String PROPERTY_CODE_ATTRIBUTE_ENTRY_ID = "appointment.codeAttribut.entryId";
    private static final String PROPERTY_COMMA_JOIN = "appointment.comma.join";
    
	public static Response setSpecificEntryValue(Entry entry, List<String> codesPredemandeList) 
	{
		String codesPredemande = String.join(AppPropertiesService.getProperty(PROPERTY_COMMA_JOIN),codesPredemandeList);

		Response response = new Response( );
		
		for (Field field : entry.getFields()) 
		{
			if (StringUtils.equals(field.getCode(),
					AppPropertiesService.getProperty(PROPERTY_CODE_ATTRIBUTE_ENTRY_ID))) 
			{
				field.setValue(codesPredemande);
				response.setEntry(entry);
				response.setResponseValue(field.getValue());
			}
		}

		return response;
	}
}
