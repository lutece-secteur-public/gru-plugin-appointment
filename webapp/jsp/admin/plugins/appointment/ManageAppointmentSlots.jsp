<jsp:useBean id="appointmentSlot" scope="session" class="fr.paris.lutece.plugins.appointment.web.AppointmentSlotJspBean" />
<% String strContent = appointmentSlot.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
