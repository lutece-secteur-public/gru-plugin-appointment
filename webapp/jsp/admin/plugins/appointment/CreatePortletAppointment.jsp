
<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="appointmentPortlet" scope="session" class="fr.paris.lutece.plugins.appointment.web.portlet.AppointmentPortletJspBean" />

<% appointmentPortlet.init( request, appointmentPortlet.RIGHT_MANAGE_ADMIN_SITE); %>
<%= appointmentPortlet.getCreate ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>


