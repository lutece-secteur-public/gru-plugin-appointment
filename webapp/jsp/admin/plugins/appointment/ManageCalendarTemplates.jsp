<%@ page errorPage="../../ErrorPage.jsp" trimDirectiveWhitespaces="true" %>
<jsp:useBean id="manageAppointmentCalendarTemplate" scope="session" class="fr.paris.lutece.plugins.appointment.web.CalendarTemplateJspBean" />
<%
	String strContent = manageAppointmentCalendarTemplate.processController ( request , response );
	if ( strContent != null )
	{
%>

<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
<%
	}
%>
