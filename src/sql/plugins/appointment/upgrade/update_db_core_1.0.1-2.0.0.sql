INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('APPOINTMENT_CATEGORY_MANAGEMENT','appointment.adminFeature.ManageAppointmentCategory.name',1,'jsp/admin/plugins/appointment/ManageAppointmentCategory.jsp','appointment.adminFeature.ManageAppointmentCategory.description',0,'appointment','SYSTEM',NULL,NULL,4);

INSERT INTO core_user_right (id_right,id_user) VALUES ('APPOINTMENT_CATEGORY_MANAGEMENT',1);

INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('super_admin','APPOINTMENT_CATEGORY','*','*');