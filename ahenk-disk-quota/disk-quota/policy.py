#!/usr/bin/python3
# -*- coding: utf-8 -*-
# Author:Mine DOGAN <mine.dogan@agem.com.tr>

import json
import os
import sys

from base.plugin.abstract_plugin import AbstractPlugin

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))

from fstab import Fstab


class DiskQuota(AbstractPlugin):
    def __init__(self, data, context):
        super(AbstractPlugin, self).__init__()
        self.data = data
        self.context = context
        self.logger = self.get_logger()
        self.message_code = self.get_message_code()

        self.username = self.context.get('username')

        self.mount = 'mount -o remount {}'
        self.quotaon_all = 'quotaon --all'
        self.quotaon_avug = 'quotaon -avug'
        self.set_quota = 'setquota --always-resolve -u {0} {1} {2} 0 0 --all'

        self.parameters = json.loads(self.data)

        self.soft_quota = str(int(self.parameters['soft-quota']) * 1024)
        self.hard_quota = str(int(self.parameters['hard-quota']) * 1024)
        self.default_quota = str(int(self.parameters['default-quota']) * 1024)

        self.logger.debug('Parameters were initialized.')

    def handle_policy(self):
        self.logger.debug('Policy handling...')
        try:
            # Check fstab & append 'usrquota' option if not exists
            fs = Fstab()
            fs.read('/etc/fstab')
            fstab_entries = []
            fslines = fs.lines
            for line in fslines:
                if line.has_filesystem() and 'usrquota' not in line.options:
                    if line.dict['directory'] == '/' or line.dict['directory'] == '/home/':
                        self.logger.debug('Appending \'usrquota\' option to {}'.format(line.dict['directory']))
                        line.options += ['usrquota']
                        fstab_entries.append(line.dict['directory'])
            fs.write('/etc/fstab')

            # Re-mount necessary fstab entries
            for entry in fstab_entries:
                self.execute(self.mount.format(entry))
                self.logger.debug('Remounting fstab entry {}'.format(entry))

            self.execute(self.quotaon_all)
            self.logger.debug('{}'.format(self.quotaon_all))

            self.execute(self.quotaon_avug)
            self.logger.debug('{}'.format(self.quotaon_avug))

            self.execute(self.set_quota.format(self.username, self.soft_quota, self.hard_quota))
            self.logger.debug(
                'Set soft and hard quota. Username: {0}, Soft Quota: {1}, Hard Quota: {2}'.format(self.username,
                                                                                                  self.soft_quota,
                                                                                                  self.hard_quota))

            self.create_default_quota_file()
            self.context.create_response(code=self.get_message_code().POLICY_PROCESSED.value,
                                         message='Kotalar başarıyla güncellendi.')

        except Exception as e:
            self.logger.error('[DiskQuota] A problem occurred while handling browser profile: {0}'.format(str(e)))
            self.context.create_response(code=self.get_message_code().POLICY_ERROR.value,
                                         message='Disk Quota profili uygulanırken bir hata oluştu.')

    def create_default_quota_file(self):
        self.write_file('default_quota',self.default_quota)


def handle_policy(profile_data, context):
    dq = DiskQuota(profile_data, context)
    dq.handle_policy()

