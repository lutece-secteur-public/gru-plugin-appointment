<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="appointmentFormListPortlet" scope="session" class="fr.paris.lutece.plugins.appointment.web.portlet.AppointmentFormListPortletJspBean" />

<%
	appointmentFormListPortlet.init( request, appointmentFormListPortlet.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect( appointmentFormListPortlet.doModify( request ) );
%>


