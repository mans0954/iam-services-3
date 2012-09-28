use openiam;
DROP TABLE IF EXISTS REPORT_QUERY;
CREATE TABLE REPORT_QUERY(
	report_query_id varchar(20) NOT NULL,
	report_name VARCHAR(64) NOT NULL UNIQUE,
    query_script_path VARCHAR(255) NOT NULL,
    params VARCHAR(255) NOT NULL,
    required_params VARCHAR(255) NOT NULL,
    dto_class VARCHAR(255) NOT NULL,
    PRIMARY KEY (report_query_id)
)Engine=InnoDB;

