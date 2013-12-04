<jsp:useBean id="appointmentFormFields" scope="session" class="fr.paris.lutece.plugins.appointment.web.AppointmentFormFieldJspBean" />
<% String strContent = appointmentFormFields.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
