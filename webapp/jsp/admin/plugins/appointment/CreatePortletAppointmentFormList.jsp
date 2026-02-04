<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.appointment.web.portlet.AppointmentFormListPortletJspBean"%>

${ appointmentFormListPortletJspBean.init( pageContext.request, AppointmentFormListPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ appointmentFormListPortletJspBean.getCreate( pageContext.request ) }
