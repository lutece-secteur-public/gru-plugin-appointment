package fr.paris.lutece.plugins.appointment.web.rs;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.ClientResponse.Status;

import fr.paris.lutece.plugins.appointment.service.FormTradeService;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import net.sf.json.JSONObject;

/**
 * REST service for import form
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_PATH + Constants.FORM_PATH )
public class FormRest
{

    /**
     * Default constructor
     */
    public FormRest( )
    {
        super( );
    }

    @POST
    @Path( Constants.IMPORT_PATH )
    @Consumes( MediaType.APPLICATION_JSON )
    public Response importForm( String strInputJson )
    {
        Response response = Response.ok( ).build( );
        try
        {
            FormTradeService.importFormFromJson( JSONObject.fromObject( strInputJson ) );
        }
        catch( IOException e )
        {
            response = Response.status( Status.NOT_ACCEPTABLE ).build( );
        }
        return response;
    }

}
