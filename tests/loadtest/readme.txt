### Jmeter load test###
how to load jmeter script:

1. pre-run scheduler and store data entries >1000 into task table
2. pg_dump table into task_1000_entries_table_backup file
3. open http://localhost:8080
4. psql restore table data into task table.
5. data will start showing up in http://localhost:8080 endpoint
6. start jmeter locally
7. open 'taskmanager_test.jmx' jmeter project from jmeter
8. expand project structure, go to 'Thread Group', configure # of threads, ramp-up time and loop counts.
9. click green play button on the top to run the load test.
10. see graph on 'Graph_taskmanager' tab.

with 100 threads,
ramp-up time in 1000 seconds, and loop counts 10, the throughput is 60/minutes
ramp-up time in 100 seconds, and loop counts 10, the throughput is 600/minutes
ramp-up time in 10 seconds, and loop counts 10, the throughput is 640/minutes
ramp-up time in 5 seconds, and loop counts 10, the throughput is 709/minutes
ramp-up time in 2 seconds, and loop counts 10, the throughput is 789/minutes
ramp-up time in 0 seconds, and loop counts 10, the throughput is 496/minutes

as we can see from throughput rate, when ramp up time increases until the certain point, the throughput rate
 decreases, and if we introduce more delay between requests, the throughput rate will keep almost constant,
 thus in our case, we should set the ramp up time between 2-10 seconds.

configuration:
HTTP Get /list request:
threads: 100
ramp-up time: 5 secs
loop: 10

JDBC request:
table: task
jdbc connection info
username: s_ink_user
password: s_ink_pwd