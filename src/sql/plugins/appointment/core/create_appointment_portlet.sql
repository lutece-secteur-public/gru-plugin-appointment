  
--
-- Structure for table appointment_portlet
--
DROP TABLE IF EXISTS appointment_portlet;
CREATE TABLE appointment_portlet (
  id_portlet int default '0' NOT NULL,
  appointment_feed_id varchar(100) default NULL,
  PRIMARY KEY  (id_portlet)
);
