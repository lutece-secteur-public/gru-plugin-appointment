/*
    Note : AUTO_INCREMENT fields work with Mysql. For PostgreSQL, you have to 
    replace AUTO_INCREMENT, depending on your PostgreSQL version. You can
    find an example on buid.properties in target/lutece/sql directory after compilation.
    
    make sure to NOT use this on production and have a backup

*/

SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE appointment_appointment MODIFY id_appointment INT AUTO_INCREMENT;
ALTER TABLE  appointment_calendar_template MODIFY id_calendar_template INT AUTO_INCREMENT;
ALTER TABLE  appointment_category MODIFY id_category INT AUTO_INCREMENT;
ALTER TABLE  appointment_display  MODIFY id_display INT AUTO_INCREMENT;
ALTER TABLE  appointment_form MODIFY id_form INT AUTO_INCREMENT;
ALTER TABLE  appointment_localization MODIFY  id_localization INT AUTO_INCREMENT;
ALTER TABLE  appointment_form_message MODIFY id_form_message INT AUTO_INCREMENT;
ALTER TABLE  appointment_week_definition MODIFY id_week_definition INT AUTO_INCREMENT;
ALTER TABLE  appointment_working_day  MODIFY id_working_day INT AUTO_INCREMENT;
ALTER TABLE  appointment_time_slot MODIFY id_time_slot INT AUTO_INCREMENT;
ALTER TABLE  appointment_closing_day MODIFY id_closing_day INT AUTO_INCREMENT;

ALTER TABLE  appointment_user MODIFY id_user INT AUTO_INCREMENT;
ALTER TABLE  appointment_slot MODIFY id_slot INT AUTO_INCREMENT;
ALTER TABLE  appointment_appointment_response MODIFY id_appointment_response INT AUTO_INCREMENT;
ALTER TABLE  appointment_form_rule  MODIFY id_form_rule INT AUTO_INCREMENT;
ALTER TABLE  appointment_reservation_rule MODIFY id_reservation_rule INT AUTO_INCREMENT;

SET FOREIGN_KEY_CHECKS = 1;