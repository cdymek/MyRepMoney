/**
This file documents the table create scripts used to populate a clean environment.  To load a new environment in AWS, use the following url:
https://s3.amazonaws.com/cdymekbackup/MyRepMoney/MyRepMoney-full-stack-pub-vpn-test.json
JCS Server Id: 54.165.51.99

Abbreviations
stg => staging
cand => candidate
comm => committee
ind => individual
contrib => contribution
*/

/**JCS Control Table*/
CREATE TABLE master_source_download (
SOURCE_ENTITY VARCHAR(25) NOT NULL,
SOURCE_URL VARCHAR(500) NOT NULL,
SOURCE_FILE_TYPE VARCHAR(25) NOT NULL,
TARGET_TABLE VARCHAR(500) NOT NULL,
SOURCE_ACTIVE TINYINT(1) NOT NULL DEFAULT 0,
DATE_ADDED TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ELECTION_CYCLE VARCHAR(10) NOT NULL,
DATA_LOADER_CLASS VARCHAR(200) NOT NULL,
ADDED_BY VARCHAR(25)
);

/** Candidate Contributions from a Committee Table **/
CREATE TABLE stg_fec_candidate_contrib_comm (
CMTE_ID VARCHAR(9) NOT NULL,
AMNDT_IND VARCHAR(1),
RPT_TP VARCHAR(3),
TRANSACTION_PGI VARCHAR(5),
IMAGE_NUM VARCHAR(11),
TRANSACTION_TP VARCHAR(3),
ENTITY_TP VARCHAR(3),
NAME VARCHAR(200),
CITY VARCHAR(30),
STATE VARCHAR(2),
ZIP_CODE VARCHAR(9),
EMPLOYER VARCHAR(38),
OCCUPATION VARCHAR(38),
TRANSACTION_DT VARCHAR(10),
TRANSACTION_AMT DECIMAL(14,2),
OTHER_ID VARCHAR(9),
CAND_ID VARCHAR(9),
TRAN_ID VARCHAR(32),
FILE_NUM INTEGER(14),
MEMO_CD VARCHAR(1),
MEMO_TEXT VARCHAR(100),
SUB_ID INTEGER(19),
ELECTION_CYCLE VARCHAR(10)
);

insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2004/pas204.zip', 'stg_fec_candidate_contrib_comm', 1, 
	'2014-09-12 21:39', 'DYMEKC', '2003-2004', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');

update master_source_download SET SCHEDULED_LOAD_TIME = '02:45:00' WHERE TARGET_TABLE='stg_fec_candidate_contrib_comm';

/**Committee Master File tablee*/
CREATE TABLE stg_fec_comm_master_file (
CMTE_ID VARCHAR(9) NOT NULL,
CMTE_NM VARCHAR(200),
TRES_NM VARCHAR(90),
CMTE_ST1 VARCHAR(34),
CMTE_ST2 VARCHAR(34),
CMTE_CITY VARCHAR(30),
CMTE_ST VARCHAR(2),
CMTE_ZIP VARCHAR(9),
CITY VARCHAR(30),
STATE VARCHAR(2),
ZIP_CODE VARCHAR(9),
CMTE_DSGN VARCHAR(1),
OCCUPATION VARCHAR(1),
CMTE_TP VARCHAR(10),
CMTE_PTY_AFFILIATION VARCHAR(3),
CMTE_FILING_FREQ VARCHAR(1),
ORG_TP VARCHAR(1),
CONNECTED_ORG_NM VARCHAR(200),
CAND_ID INTEGER(9),
ELECTION_CYCLE VARCHAR(10)
);

/**2013-2014*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2014/cm14.zip', 'stg_fec_comm_master_file', 1, 
	'2014-09-13 12:49', 'DYMEKC', '2013-2014', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2011-2012*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2012/cm12.zip', 'stg_fec_comm_master_file', 1, 
	'2014-09-13 12:49', 'DYMEKC', '2011-2012', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2009-2010*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2010/cm10.zip', 'stg_fec_comm_master_file', 1, 
	'2014-09-13 12:49', 'DYMEKC', '2009-2010', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2007-2008*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2008/cm08.zip', 'stg_fec_comm_master_file', 1, 
	'2014-09-13 12:49', 'DYMEKC', '2007-2008', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2005-2006*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2006/cm06.zip', 'stg_fec_comm_master_file', 1, 
	'2014-09-13 12:49', 'DYMEKC', '2005-2006', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2003-2004*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2004/cm04.zip', 'stg_fec_comm_master_file', 1, 
	'2014-09-13 12:49', 'DYMEKC', '2003-2004', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');

update master_source_download SET SCHEDULED_LOAD_TIME = '02:50:00' WHERE TARGET_TABLE='stg_fec_comm_master_file';


/**Candidate Master File tablee*/
CREATE TABLE stg_fec_cand_master_file (
CAND_ID VARCHAR(9) NOT NULL,
CAND_NAME VARCHAR(200),
CAND_PTY_AFFILIATION VARCHAR(3),
CAND_ELECTION_YR INTEGER(4),
CAND_OFFICE_ST VARCHAR(2),
CAND_OFFICE VARCHAR(1),
CAND_OFFICE_DISTRICT VARCHAR(2),
CAND_ICI VARCHAR(1),
CAND_STATUS VARCHAR(1),
CAND_PCC VARCHAR(9),
CAND_ST1 VARCHAR(34),
CAND_ST2 VARCHAR(34),
CAND_CITY VARCHAR(30),
CAND_ST VARCHAR(2),
CAND_ZIP VARCHAR(9),
ELECTION_CYCLE VARCHAR(10)
);

/**2013-2014*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2014/cn14.zip', 'stg_fec_cand_master_file', 1, 
	'2014-09-14 18:19', 'DYMEKC', '2013-2014', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2011-2012*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2012/cn12.zip', 'stg_fec_cand_master_file', 1, 
	'2014-09-14 18:19', 'DYMEKC', '2011-2012', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2009-2010*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2010/cn10.zip', 'stg_fec_cand_master_file', 1, 
	'2014-09-14 18:19', 'DYMEKC', '2009-2010', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2007-2008*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2008/cn08.zip', 'stg_fec_cand_master_file', 1, 
	'2014-09-14 18:19', 'DYMEKC', '2007-2008', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2005-2006*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2006/cn06.zip', 'stg_fec_cand_master_file', 1, 
	'2014-09-14 18:19', 'DYMEKC', '2005-2006', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2003-2004*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2004/cn04.zip', 'stg_fec_cand_master_file', 1, 
	'2014-09-14 18:19', 'DYMEKC', '2003-2004', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');

update master_source_download SET SCHEDULED_LOAD_TIME = '02:55:00' WHERE TARGET_TABLE='stg_fec_cand_master_file';


/**Candidate Committee Link File tablee*/
CREATE TABLE stg_fec_cand_comm_link_file (
CAND_ID VARCHAR(9) NOT NULL,
CAND_ELECTION_YR INTEGER(4) NOT NULL,
FEC_ELECTION_YR INTEGER(4) NOT NULL,
CMTE_ID VARCHAR(9),
CMTE_TP VARCHAR(1),
CMTE_DSGN VARCHAR(1),
LINKAGE_ID INTEGER(12) NOT NULL,
ELECTION_CYCLE VARCHAR(10)
);

/**2013-2014*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2014/ccl14.zip', 'stg_fec_cand_comm_link_file', 1, 
	'2014-09-15 21:49', 'DYMEKC', '2013-2014', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2011-2012*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2012/ccl12.zip', 'stg_fec_cand_comm_link_file', 1, 
	'2014-09-15 21:49', 'DYMEKC', '2011-2012', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2009-2010*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2010/ccl10.zip', 'stg_fec_cand_comm_link_file', 1, 
	'2014-09-15 21:49', 'DYMEKC', '2009-2010', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2007-2008*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2008/ccl08.zip', 'stg_fec_cand_comm_link_file', 1, 
	'2014-09-15 21:49', 'DYMEKC', '2007-2008', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2005-2006*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2006/ccl06.zip', 'stg_fec_cand_comm_link_file', 1, 
	'2014-09-15 21:49', 'DYMEKC', '2005-2006', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2003-2004*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2004/ccl04.zip', 'stg_fec_cand_comm_link_file', 1, 
	'2014-09-15 21:49', 'DYMEKC', '2003-2004', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');

update master_source_download SET SCHEDULED_LOAD_TIME = '02:40:00' WHERE TARGET_TABLE='stg_fec_cand_comm_link_file';


/**Other Commmitee to Committee Contributions File tablee*/
CREATE TABLE stg_fec_comm_comm_contrib_file (
CMTE_ID VARCHAR(9) NOT NULL,
AMNDT_IND VARCHAR(1) NOT NULL,
RPT_TP VARCHAR(3) NOT NULL,
TRANSACTION_PGI VARCHAR(5),
IMAGE_NUM VARCHAR(11),
TRANSACTION_TP VARCHAR(3),
ENTITY_TP VARCHAR(3),
NAME VARCHAR(200) NOT NULL,
CITY VARCHAR(30),
STATE VARCHAR(2),
ZIP_CODE VARCHAR(9),
EMPLOYER VARCHAR(38) NOT NULL,
OCCUPATION VARCHAR(38),
TRANSACTION_DT DATE,
TRANSACTION_AMT DECIMAL(14,2),
OTHER_ID VARCHAR(9),
TRAN_ID VARCHAR(3),
FILE_NUM INTEGER(22),
MEMO_CD VARCHAR(1),
MEMO_TEXT VARCHAR(100),
SUB_ID INTEGER(19) NOT NULL,
ELECTION_CYCLE VARCHAR(10)
);

/**2013-2014*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2014/oth14.zip', 'stg_fec_comm_comm_contrib_file', 1, 
	'2014-09-16 22:22', 'DYMEKC', '2013-2014', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2011-2012*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2012/oth12.zip', 'stg_fec_comm_comm_contrib_file', 1, 
	'2014-09-16 22:22', 'DYMEKC', '2011-2012', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2009-2010*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2010/oth10.zip', 'stg_fec_comm_comm_contrib_file', 1, 
	'2014-09-16 22:22', 'DYMEKC', '2009-2010', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2007-2008*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2008/oth08.zip', 'stg_fec_comm_comm_contrib_file', 1, 
	'2014-09-16 22:22', 'DYMEKC', '2007-2008', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2005-2006*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2006/oth06.zip', 'stg_fec_comm_comm_contrib_file', 1, 
	'2014-09-16 22:22', 'DYMEKC', '2005-2006', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2003-2004*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2004/oth04.zip', 'stg_fec_comm_comm_contrib_file', 1, 
	'2014-09-16 22:22', 'DYMEKC', '2003-2004', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');

update master_source_download SET SCHEDULED_LOAD_TIME = '02:35:00' WHERE TARGET_TABLE='stg_fec_comm_comm_contrib_file';


/**Individual Contributions File tablee*/
CREATE TABLE stg_fec_ind_contrib_file (
CMTE_ID VARCHAR(9) NOT NULL,
AMNDT_IND VARCHAR(1),
RPT_TP VARCHAR(3),
TRANSACTION_PGI VARCHAR(5),
IMAGE_NUM VARCHAR(11),
TRANSACTION_TP VARCHAR(3),
ENTITY_TP VARCHAR(3),
NAME VARCHAR(200),
CITY VARCHAR(30),
STATE VARCHAR(2),
ZIP_CODE VARCHAR(9),
EMPLOYER VARCHAR(38),
OCCUPATION VARCHAR(38),
TRANSACTION_DT DATE,
TRANSACTION_AMT DECIMAL(14,2),
OTHER_ID VARCHAR(9),
TRAN_ID VARCHAR(3),
FILE_NUM INTEGER(22),
MEMO_CD VARCHAR(1),
MEMO_TEXT VARCHAR(100),
SUB_ID INTEGER(19) NOT NULL,
ELECTION_CYCLE VARCHAR(10)
);

/**2013-2014*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2014/indiv14.zip', 'stg_fec_ind_contrib_file', 1, 
	'2014-09-17 15:22', 'DYMEKC', '2013-2014', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2011-2012*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2012/indiv12.zip', 'stg_fec_ind_contrib_file', 1, 
	'2014-09-17 15:22', 'DYMEKC', '2011-2012', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2009-2010*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2010/indiv10.zip', 'stg_fec_ind_contrib_file', 1, 
	'2014-09-17 15:22', 'DYMEKC', '2009-2010', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2007-2008*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2008/indiv08.zip', 'stg_fec_ind_contrib_file', 1, 
	'2014-09-17 15:22', 'DYMEKC', '2007-2008', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2005-2006*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2006/indiv06.zip', 'stg_fec_ind_contrib_file', 1, 
	'2014-09-17 15:22', 'DYMEKC', '2005-2006', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');
/**2003-2004*/
insert into master_source_download (SOURCE_ENTITY, SOURCE_URL, TARGET_TABLE, SOURCE_ACTIVE, 
	DATE_ADDED, ADDED_BY, ELECTION_CYCLE, SOURCE_FILE_TYPE, DATA_LOADER_CLASS, SCHEDULED_LOAD_TIME, 
	DELIMITER) 
VALUES ('FEC', 'ftp://ftp.fec.gov/FEC/2004/indiv04.zip', 'stg_fec_ind_contrib_file', 1, 
	'2014-09-17 15:22', 'DYMEKC', '2003-2004', 'zip', 'com.MyRepMoney.dataloader.FECDataLoader', '', 
	'|');

update master_source_download SET SCHEDULED_LOAD_TIME = '02:30:00' WHERE TARGET_TABLE='stg_fec_ind_contrib_file';
