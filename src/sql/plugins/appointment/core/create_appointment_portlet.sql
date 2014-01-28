  
--
-- Structure for table appointment_portlet
--
DROP TABLE IF EXISTS appointment_form_portlet;
CREATE TABLE appointment_form_portlet (
  id_portlet int default '0' NOT NULL,
  id_form int default '0' NOT NULL,
  PRIMARY KEY  (id_portlet)
);
