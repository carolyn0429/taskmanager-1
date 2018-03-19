#!/usr/bin/python

import psycopg2
import unittest
import requests
import random
from pprint import pprint


class MyTestCase(unittest.TestCase):

    def setUp(self):
        try:
            self.url = 'http://localhost:8080/#'
            self.conn = psycopg2.connect(dbname='s_ink', user='s_ink_user', password='s_ink_pwd')
            self.cur = self.conn.cursor()
            self.cur.execute("select * from task")
            self.rows = self.cur.fetchall()

        except psycopg2.Error as e:
            pprint(e.pgerror)
            pass

    def test_getalist(self):
        url = 'http://localhost:8080/#'
        headers = {'content-type': 'application/json', 'Accept': 'application/json'}
        resp = requests\
            .get(str(url), auth=('s_ink_user', 's_ink_pwd'), headers=headers)

        self.assertEquals(200, resp.status_code)
        self.assertEqual('OK', resp.reason)


    def test_post_a_task(self):
        url = "http://localhost:8080/"
        headers = {'content-type': 'application/json', 'Accept': 'application/json'}

        number = random.randrange(1, 100)
        payload = {"description": "test task " + str(number), "title": "test title " + str(number)}
        resp = requests.put(str(url)+"task/create",
                             json=payload,
                             auth=('s_ink_user', 's_ink_pwd'), headers=headers)
        self.assertEquals(200, resp.status_code)
        rows = self.cur.fetchall()
        # {2}: task, {9}: title
        for row in rows:
            print(str(row))
            self.assertTrue('test task ' + str(number) in str(row[2]))
            self.assertTrue('test title ' + str(number) in str(row[9]))

if __name__ == '__main__':
    try:
        unittest.main()
    finally:
        print("------test is over------")
