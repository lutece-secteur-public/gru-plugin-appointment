<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.appointment.web.portlet.AppointmentFormPortletJspBean"%>

${ appointmentFormPortletJspBean.init( pageContext.request, AppointmentFormPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( appointmentFormPortletJspBean.doModify( pageContext.request ) ) }
