#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# Author: >
# Author: Volkan Şahin <volkansah.in> <bm.volkansahin@gmail.com>

import json

from base.plugin.abstract_plugin import AbstractPlugin


class DiskQuota(AbstractPlugin):
    def __init__(self, data, context):
        super(AbstractPlugin, self).__init__()
        self.data = data
        self.context = context
        self.logger = self.get_logger()
        self.message_code = self.get_message_code()

        #TODO is installed?

        self.set_quota_command = 'setquota --always-resolve -u {0} {1} {2} 0 0 --all'
        self.soft_quota = None
        self.hard_quota = None

        try:
            self.logger.debug('[DiskQuota] ')
            self.username = self.context.get('username')
            json_data = json.loads(data)
            self.soft_quota = json_data['soft-quota']
            self.hard_quota = json_data['hard-quota']

            self.soft_quota = str(int(self.soft_quota) * 1024)
            self.hard_quota = str(int(self.hard_quota) * 1024)

        except Exception as e:
            self.logger.error('[DiskQuota] '.format(str(e)))

        self.logger.info('[DiskQuota] Parameters were initialized.')

    def handle_policy(self):
        self.logger.info('[DiskQuota] Policy handling...')
        try:
            #TODO
            pass
            result_code, p_out, p_err = self.execute(self.set_quota_command.format(self.username,self.soft_quota,self.hard_quota))

            if result_code == 0:
                self.logger.debug('[DiskQuota] Writing preferences to user profile')
            else:
                self.logger.debug('[DiskQuota] Writing preferences to user profile')


            self.context.create_response(code=self.get_message_code().POLICY_PROCESSED.value,
                                         message='User disk-quota profile processed successfully')

        except Exception as e:
            self.logger.error('[DiskQuota] A problem occurred while handling browser profile: {0}'.format(str(e)))
            self.context.create_response(code=self.get_message_code().POLICY_ERROR.value,
                                         message='A problem occurred while handling disk-quota profile: {0}'.format(
                                             str(e)))


def handle_policy(profile_data, context):
    print('Handling Disk Quota')
    dq = DiskQuota(profile_data, context)
    dq.handle_policy()
    print('Disk Quota Handled')
