<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.appointment.web.portlet.AppointmentPortletJspBean"%>

${ appointmentPortletJspBean.init( pageContext.request, AppointmentPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( appointmentPortletJspBean.doModify( pageContext.request ) ) }
