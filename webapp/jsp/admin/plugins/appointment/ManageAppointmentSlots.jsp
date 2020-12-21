<jsp:useBean id="typicalWeek" scope="session" class="fr.paris.lutece.plugins.appointment.web.TypicalWeekJspBean" />
<% String strContent = typicalWeek.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
