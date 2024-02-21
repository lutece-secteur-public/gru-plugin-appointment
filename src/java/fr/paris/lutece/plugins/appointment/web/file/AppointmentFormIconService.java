package fr.paris.lutece.plugins.appointment.web.file;

import fr.paris.lutece.plugins.appointment.business.display.Display;
import fr.paris.lutece.plugins.appointment.service.DisplayService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.image.ImageResourceManager;
import fr.paris.lutece.portal.service.image.ImageResourceProvider;
import fr.paris.lutece.portal.service.init.LuteceInitException;
import org.apache.commons.fileupload.FileItem;

/**
 * Image Resource Service for the appointment form icon
 */
public class AppointmentFormIconService implements ImageResourceProvider
{
    private static AppointmentFormIconService _singleton = new AppointmentFormIconService( );
    private static final String IMAGE_RESOURCE_TYPE_ID = "appointmentForm_icon";

    /**
     * Creates a new instance of AppointmentFormIconService
     */
    private AppointmentFormIconService( )
    {
    }

    /**
     * Init
     *
     * @throws LuteceInitException
     *         if an error occurs
     */
    public static synchronized void init( )
    {
        getInstance( ).register( );
    }

    /**
     * Initializes the service
     */
    public void register( )
    {
        ImageResourceManager.registerProvider( this );
    }

    /**
     * Get the unique instance of the service
     *
     * @return The unique instance
     */
    public static AppointmentFormIconService getInstance( )
    {
        return _singleton;
    }

    /**
     * Return the Resource id
     *
     * @param nIdResource
     *         The resource identifier
     * @return The Resource Image
     */
    @Override
    public ImageResource getImageResource( int nIdResource )
    {
        Display display = DisplayService.findDisplayWithFormId( nIdResource );

        if ( display != null )
        {
            return display.getIcon( );
        }
        return null;
    }

    /**
     * Return the Resource Type id
     *
     * @return The Resource Type Id
     */
    public String getResourceTypeId( )
    {
        return IMAGE_RESOURCE_TYPE_ID;
    }

    /**
     * Add Image Resource
     *
     * @param fileItem
     * @return the Image File Key
     */
    @Override
    public String addImageResource( FileItem fileItem )
    {
        return null;
    }

}

