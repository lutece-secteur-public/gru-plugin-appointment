-- liquibase formatted sql
-- changeset appointment:update_db_core_1.0.1-2.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('APPOINTMENT_CATEGORY_MANAGEMENT','appointment.adminFeature.manageCategories.name',1,'jsp/admin/plugins/appointment/ManageAppointmentCategory.jsp','appointment.adminFeature.manageCategories.description',0,'appointment','SYSTEM',NULL,NULL,4);

INSERT INTO core_user_right (id_right,id_user) VALUES ('APPOINTMENT_CATEGORY_MANAGEMENT',1);