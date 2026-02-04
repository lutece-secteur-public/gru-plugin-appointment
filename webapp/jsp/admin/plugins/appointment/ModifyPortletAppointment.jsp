<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.appointment.web.portlet.AppointmentPortletJspBean"%>

${ appointmentPortletJspBean.init( pageContext.request, AppointmentPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ appointmentPortletJspBean.getModify( pageContext.request ) }
