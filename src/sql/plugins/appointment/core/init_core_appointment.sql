--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'APPOINTMENT_FORM_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('APPOINTMENT_FORM_MANAGEMENT','appointment.adminFeature.ManageAppointmentForm.name',1,'jsp/admin/plugins/appointment/ManageAppointmentForms.jsp','appointment.adminFeature.ManageAppointmentForm.description',0,'appointment','APPLICATIONS',NULL,NULL,4);
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('APPOINTMENT_CALENDAR_TEMPLATE','appointment.adminFeature.manageCalendarTemplates.name',0,'jsp/admin/plugins/appointment/ManageCalendarTemplates.jsp','appointment.adminFeature.manageCalendarTemplates.name',0,'appointment','APPLICATIONS',NULL,NULL,5);


INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES (1026,'super_admin','APPOINTMENT_FORM','*','*');

--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'APPOINTMENT_FORM_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('APPOINTMENT_FORM_MANAGEMENT',1);
DELETE FROM core_user_right WHERE id_right = 'APPOINTMENT_CALENDAR_TEMPLATE';
INSERT INTO core_user_right (id_right,id_user) VALUES ('APPOINTMENT_CALENDAR_TEMPLATE',1);


INSERT INTO core_portlet_type VALUES ('APPOINTMENT_PORTLET','appointment.myAppointments.name','plugins/appointment/CreatePortletAppointment.jsp','plugins/appointment/ModifyPortletAppointment.jsp','fr.paris.lutece.plugins.appointment.business.portlet.AppointmentPortletHome','appointment','plugins/appointment/DoCreatePortletAppointment.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/appointment/portlet/create_portletappointment.html','','plugins/appointment/DoModifyPortletAppointment.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/appointment/portlet/modify_portletappointment.html','');
INSERT INTO core_portlet_type VALUES ('APPOINTMENT_FORM_PORTLET','appointment.appointmentForm.name','plugins/appointment/CreatePortletAppointmentForm.jsp','plugins/appointment/ModifyPortletAppointmentForm.jsp','fr.paris.lutece.plugins.appointment.business.portlet.AppointmentFormPortletHome','appointment','plugins/appointment/DoCreatePortletAppointmentForm.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/appointment/portlet/create_portletappointmentform.html','','plugins/appointment/DoModifyPortletAppointmentForm.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/appointment/portlet/modify_portletappointmentform.html','');
INSERT INTO core_portlet_type VALUES ('APPOINTMENT_FORM_LIST_PORTLET','appointment.portlet.appointmentFormListPortlet.name','plugins/appointment/CreatePortletAppointmentFormList.jsp','plugins/appointment/ModifyPortletAppointmentFormList.jsp','fr.paris.lutece.plugins.appointment.business.portlet.AppointmentFormListPortletHome','appointment','plugins/appointment/DoCreatePortletAppointmentFormList.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/appointment/portlet/create_portletappointmentformlist.html','','plugins/appointment/DoModifyPortletAppointmentFormList.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/appointment/portlet/modify_portletappointmentformlist.html','');

INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('APPOINTMENT_FORM', 3, 2);

INSERT INTO core_datastore(entity_key, entity_value) VALUES ('core.cache.status.appointment.appointmentFormCacheService.enabled', '1');

-- 
-- CATEGORY
--
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('APPOINTMENT_CATEGORY_MANAGEMENT','appointment.adminFeature.manageCategories.name',1,'jsp/admin/plugins/appointment/ManageAppointmentCategory.jsp','appointment.adminFeature.manageCategories.description',0,'appointment','SYSTEM',NULL,NULL,4);

INSERT INTO core_user_right (id_right,id_user) VALUES ('APPOINTMENT_CATEGORY_MANAGEMENT',1);