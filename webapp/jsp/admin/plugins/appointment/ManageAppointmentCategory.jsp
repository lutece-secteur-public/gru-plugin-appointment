<jsp:useBean id="manageAppointmentCategory" scope="session" class="fr.paris.lutece.plugins.appointment.web.AppointmentCategoryJspBean" />
<% String strContent = manageAppointmentCategory.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
