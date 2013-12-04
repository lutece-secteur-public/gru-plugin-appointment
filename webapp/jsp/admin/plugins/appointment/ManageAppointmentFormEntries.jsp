<jsp:useBean id="appointmentFormEntry" scope="session" class="fr.paris.lutece.plugins.appointment.web.AppointmentFormEntryJspBean" />
<% String strContent = appointmentFormEntry.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
