#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author:Mine DOGAN <mine.dogan@agem.com.tr>

import json

from base.plugin.abstract_plugin import AbstractPlugin


class DiskQuota(AbstractPlugin):
    def __init__(self, data, context):
        super(AbstractPlugin, self).__init__()
        self.data = data
        self.context = context
        self.logger = self.get_logger()
        self.message_code = self.get_message_code()

        self.username = self.context.get('username')

        self.mount = 'mount -o remount --all'
        self.quotaon_all = 'quotaon --all'
        self.quotaon_avug = 'quotaon -avug'
        self.set_quota = 'setquota --always-resolve -u {0} {1} {2} 0 0 --all'

        self.parameters = json.loads(self.data)

        self.soft_quota = str(int(self.parameters['soft-quota']) * 1024)
        self.hard_quota = str(int(self.parameters['hard-quota']) * 1024)

        self.logger.debug('Parameters were initialized.')

    def handle_policy(self):
        self.logger.debug('Policy handling...')
        try:
            self.execute(self.mount)
            self.logger.debug('{}'.format(self.mount))

            self.execute(self.quotaon_all)
            self.logger.debug('{}'.format(self.quotaon_all))

            self.execute(self.quotaon_avug)
            self.logger.debug('{}'.format(self.quotaon_avug))

            self.execute(self.set_quota.format(self.username, self.soft_quota, self.hard_quota))
            self.logger.debug(
                'Set soft and hard quota. Username: {0}, Soft Quota: {1}, Hard Quota: {2}'.format(self.username,
                                                                                                  self.soft_quota,
                                                                                                  self.hard_quota))

            self.context.create_response(code=self.get_message_code().POLICY_PROCESSED.value,
                                         message='Kotalar başarıyla güncellendi.')

        except Exception as e:
            self.logger.error('[DiskQuota] A problem occurred while handling browser profile: {0}'.format(str(e)))
            self.context.create_response(code=self.get_message_code().POLICY_ERROR.value,
                                         message='Disk Quota profili uygulanırken bir hata oluştu.')


def handle_policy(profile_data, context):
    dq = DiskQuota(profile_data, context)
    dq.handle_policy()
