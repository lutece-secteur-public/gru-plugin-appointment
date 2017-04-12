package fr.paris.lutece.plugins.appointment.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import fr.paris.lutece.plugins.appointment.business.appointment.AppointmentResponseHome;
import fr.paris.lutece.plugins.appointment.service.upload.AppointmentAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;

/**
 * Service Class for the appointment Response
 * 
 * @author Laurent Payen
 *
 */
public class AppointmentResponseService {

	/**
	 * Remove the responses for the given entry
	 * 
	 * @param nIdEntry
	 *            the entry
	 */
	public static void removeResponsesByIdEntry(int nIdEntry) {
		AppointmentResponseHome.removeResponsesByIdEntry(nIdEntry);
	}

	/**
	 * Return the list of the responses of the appointment
	 * 
	 * @param nIdAppointment
	 *            the appointment id
	 * @return the list of the responses
	 */
	public static List<Response> findListResponse(int nIdAppointment) {
		return AppointmentResponseHome.findListResponse(nIdAppointment);
	}

	/**
	 * Return the list of the id of the response of the appointment
	 * 
	 * @param nIdAppointment
	 *            the appointment id
	 * @return the list of the response id
	 */
	public static List<Integer> findListIdResponse(int nIdAppointment) {
		return AppointmentResponseHome.findListIdResponse(nIdAppointment);
	}

	/**
	 * Find and build all the response of an appointment
	 * 
	 * @param nIdAppointment
	 *            the appointment id
	 * @param request
	 *            the request
	 * @return a list of response
	 */
	public static List<Response> findAndBuildListResponse(int nIdAppointment, HttpServletRequest request) {
		List<Integer> listIdResponse = AppointmentResponseService.findListIdResponse(nIdAppointment);
		List<Response> listResponses = new ArrayList<Response>(listIdResponse.size());
		for (int nIdResponse : listIdResponse) {
			Response response = ResponseHome.findByPrimaryKey(nIdResponse);
			if (response.getField() != null) {
				response.setField(FieldHome.findByPrimaryKey(response.getField().getIdField()));
			}
			if (response.getFile() != null) {
				fr.paris.lutece.portal.business.file.File file = FileHome
						.findByPrimaryKey(response.getFile().getIdFile());
				PhysicalFile physicalFile = PhysicalFileHome
						.findByPrimaryKey(file.getPhysicalFile().getIdPhysicalFile());
				file.setPhysicalFile(physicalFile);
				response.setFile(file);
				String strIdEntry = Integer.toString(response.getEntry().getIdEntry());
				FileItem fileItem = new GenAttFileItem(physicalFile.getValue(), file.getTitle(),
						IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry, response.getIdResponse());
				AppointmentAsynchronousUploadHandler.getHandler().addFileItemToUploadedFilesList(fileItem,
						IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry, request);
			}
			listResponses.add(response);
		}
		return listResponses;
	}
}
