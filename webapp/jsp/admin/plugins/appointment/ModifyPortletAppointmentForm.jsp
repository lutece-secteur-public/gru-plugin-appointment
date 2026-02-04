<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.appointment.web.portlet.AppointmentFormPortletJspBean"%>

${ appointmentFormPortletJspBean.init( pageContext.request, AppointmentFormPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ appointmentFormPortletJspBean.getModify( pageContext.request ) }
