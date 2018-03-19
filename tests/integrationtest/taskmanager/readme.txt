### integration test ###

installation:
step:
using 'virtualenv' to create project calledn 'taskmanager' in virtual python dev environment
using 'pip' to install required python dependencies

command:
>virtualenv -p /usr/bin/python taskmanager
>source taskmanager/bin/activate
>pip install psycopg2
>pip install requests

(or pip3 if using python 3.6+)

folder structure:
tests
    integration
        test_taskmanager.py
            -setUp: setup database connection
                database name: s_ink
                table name: task
                username: s_ink_user
                password: s_ink_pwd
            -test_getalist:
                test the GET http method to /list endpoint
                verify http response code 200
            -test_post_a_task
                test the PUT http method to /create endpoint
                verify http response code 200
                verify database has corresponding entries inserted