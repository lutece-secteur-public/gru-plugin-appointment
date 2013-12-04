<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="appointmentPortlet" scope="session" class="fr.paris.lutece.plugins.appointment.web.portlet.AppointmentPortletJspBean" />

<%
	appointmentPortlet.init( request, appointmentPortlet.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect( appointmentPortlet.doModify( request ) );
%>


