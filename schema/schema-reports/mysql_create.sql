use openiam;
DROP TABLE IF EXISTS REPORT_QUERY;
CREATE TABLE REPORT_QUERY(
	id serial PRIMARY KEY not null,
	report_name VARCHAR(64) NOT NULL UNIQUE,
    query_script_path VARCHAR(255) NOT NULL,
    params VARCHAR(255) NOT NULL,
    required_params VARCHAR(255) NOT NULL
)Engine=InnoDB;

