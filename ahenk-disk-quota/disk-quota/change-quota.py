#!/usr/bin/python3
# -*- coding: utf-8 -*-

import getpass
import subprocess
from subprocess import PIPE
from base.model.enum.ContentType import ContentType
from base.model.enum.MessageCode import MessageCode
from base.model.enum.MessageType import MessageType
from base.plugin.AbstractCommand import AbstractCommand

class ChangeQuota(AbstractCommand):
    def __init__(self, task, context):
        super(ChangeQuota, self).__init__()
        self.task = task
        self.context = context

        self.current_user = getpass.getuser()
        self.current_quota = self.get_current_quota(self.current_user)
        # self.soft_quota = '1000'
        # self.hard_quota = '1500'
        self.change_quota = 'setquota --always-resolve -u ' + self.current_user + ' ' + str(int(self.soft_quota) * 1024) + ' ' + str(int(self.hard_quota) * 1024) + ' 0 0 --all'

    def handle_task(self):
        #data = {'softQuota': '222', 'hardQuota': self.current_quota['hardQuota'], 'usage': self.current_quota['usage']}
        data = {'softQuota': '222', 'hardQuota': '333', 'usage': '444'}
        #data = {'softQuota': '222'}
        self.create_response(message='_message', data=data)

        #process = self.context.execute(self.change_quota)
        #process.wait()


    def get_current_quota(self, user_name):
        check = subprocess.Popen('repquota --all ', stderr=PIPE, stdout=PIPE, shell=True)
        out = check.stdout.readlines()
        status = check.wait()

        if status > 0:
            return None
        else:
            data = [x.split() for x in out[5:-2]]
            for l in data:
                uid = l[0]
                if uid == user_name:
                    return self.parse(l)


    def parse(self, quota_data):
        if len(quota_data) >= 4:
            quota_result = {}
            quota_result['usage'] = str(quota_data[2])
            quota_result['softQuota'] = str(quota_data[3])
            quota_result['hardQuota'] = str(quota_data[4])
            return quota_result
        else:
            return None


    def create_response(self, message=None, data=None):
        self.context.put('taskId', self.task.get_id())
        self.context.put('type', MessageType.TASK_STATUS.value)
        self.context.put('responseCode', MessageCode.TASK_PROCESSED.value)
        self.context.put('responseMessage', message)
        self.context.put('responseData', data)
        self.context.put('contentType', ContentType.APPLICATION_JSON.value)


def handle_task(task, context):
    print('CHANGE-QUOTA')
    quota = ChangeQuota(task, context)
    quota.handle_task()
