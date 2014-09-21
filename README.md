MyRepMoney
==========

The MyRepMoney project attempts to achieve a single purpose - consolidate political financial data on the US Federal and state levels in to a single repository that can then be used for analysis and reporting.  Ultimately the project will consist of the following components:

1.  Data Gathering -> a series of processes that collect the data and consolidate it in to a single repository
2.  Data Analytics -> a series of processes that attempt to generate meaningful statistics from the data
3.  Web Reports -> a set of reports available on various devices that enable access to the data.
4.  APIs -> a set of APIs that 3rd parties can use to access the data


Data Gathering
=============
The current development is focused on creating a robust process for Data Gathering.  The code contained in this document includes Chef and CloudFormation scripts used to deploy the Data Gathering process.  The services run on AWS using a mix of EC2, SQS and RDS.  The application code is written in Java.

The data gathering process is split in to 2 jobs with a messaging queue.  The first job, which runs on a single EC2 instance, reads a database table and sends message requests to take action to download various data asets.  The second job, which runs on one or more EC2 instances as defined by an Auto Scaling Group, reads those messages and initiates the required actions to process the data sets specified.

The following items are long term improvements that may need to be implemented.

1.  DB Backup / Restore Solution
The current backup and restore process is to run a shell script on the job server when the job server.  Whenever the job server is started, the script runs to load the database.  Once loaded, an inital backup is taken, and then a cron job is put in place to run daily to back up the database in to S3.

Problems with this approach:  There are several issues.  First, backups only run when the job server is running.  Second, the database will be restored if the job server is re-launched, not only when the database is re-launched.  If other processes are running this is a sub-optimal solution as it will interrupt and possibly lose their work.  

2.  Job Status Logging
Job status is only logged at the server level in app log files, not in a central location.  Some type of central database job status logging should be implemented.

3.  Expanded Capability for Job Server
As written, the job server only initiates downloads from Internet data sets.  This should / could be expanded to enable multiple data sources, including transformations of data in staging tables to reporting tables.


Data Analytics
==============
The following are potential metrics to define and implement:

In Aggregate...
- Funds raised by zip code
- Funds raised by state
- Funds raised by Congressional district
- Funds raised by Employer
- Funds raised by Industry

Per candidate...
- Funds raised by zip code
- Funds raised by state
- Funds raised by Congressional district
- Funds raised by Employer
- Funds raised by Industry
- Ratio of funds raised within the district vs. outside the district

Per Party...
- Funds raised by zip code
- Funds raised by state
- Funds raised by Congressional district
- Funds raised by Employer
- Funds raised by Industry

Individual vs. Committee...
- Funds raised by zip code
- Funds raised by state
- Funds raised by Congressional district
- Funds raised by Employer
- Funds raised by Industry


Web Reports
==============
To be determined.


APIs
==============
To be determined.


Bugs
==============
1. LOG FILE DOES NOT ROLL OVER EACH DAY
2. DATABASE CONNECTIONS DO NOT CLOSE PROPERLY 