<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.appointment.web.portlet.AppointmentFormListPortletJspBean"%>

${ appointmentFormListPortletJspBean.init( pageContext.request, AppointmentFormListPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( appointmentFormListPortletJspBean.doCreate( pageContext.request ) ) }
