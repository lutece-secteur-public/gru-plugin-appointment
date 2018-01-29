package fr.paris.lutece.plugins.appointment.web.portlet;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.appointment.business.portlet.AppointmentFormListPortlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

public abstract class AbstractPortletJspBean extends PortletJspBean
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 8507575062494354655L;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreate( HttpServletRequest request )
    {
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        HtmlTemplate template = getCreateTemplate( strPageId, strPortletTypeId );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModify( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        AppointmentFormListPortlet portlet = (AppointmentFormListPortlet) PortletHome.findByPrimaryKey( nPortletId );
        HtmlTemplate template = getModifyTemplate( portlet );

        return template.getHtml( );
    }
}
