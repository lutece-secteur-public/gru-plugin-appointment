<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="appointmentFormPortlet" scope="session" class="fr.paris.lutece.plugins.appointment.web.portlet.AppointmentFormPortletJspBean" />

<% appointmentFormPortlet.init( request, appointmentFormPortlet.RIGHT_MANAGE_ADMIN_SITE); %>
<%= appointmentFormPortlet.getCreate ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>


