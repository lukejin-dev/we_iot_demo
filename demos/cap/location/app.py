#!/usr/bin/env python
# coding: utf-8
# vim: tabstop=4 expandtab shiftwidth=4 softtabstop=4 number

"""
Location app running on CAP.

@author: Lu, Ken (bluewish.ken.lu@gmail.com)

"""
import os
import logging
import logging.config
from daemon import Daemon

class CapLocApp(Daemon):

    def __init__(self, app_path=None):
        self._app_path = None

        if app_path is None:
            self.app_path = os.path.dirname(os.path.abspath(__file__))

        pidfile = os.path.join(self.app_path, "CapLocApp.pid")
        Daemon.__init__(self, pidfile)

    @property
    def app_path(self):
        return self._app_path

    @app_path.setter
    def app_path(self, v):
        assert os.path.exists(v), "Path " + v + " does not exists"
        self._app_path = v


    def standalone(self):
        self.run()

    def run(self):
        logging.config.dictConfig(DEFAULT_LOGGING_CONFIG)
        logging.info("Start location app on Intel CAP.")

DEFAULT_LOGGING_CONFIG =  {
    'version': 1,
    'disable_existing_loggers': False,
    'formatters': {
        'verbose': {
            'format': '%(name)-12s %(levelname)-8s %(asctime)s %(module)s %(process)d %(thread)d %(message)s'
            },
        'simple': {
            'format': '%(name)-12s %(levelname)-8s %(message)s'
            },
        'default': {
            'format' : '%(name)-12s %(asctime)-8s %(levelname)s %(message)s',
            'datefmt' : '%Y-%m-%d %H:%M:%S'
            }
    },
    'handlers': {
        'console': {
            'class': 'logging.StreamHandler',
            'formatter': 'default',
        },
        'sys-logger': {
            'class': 'logging.handlers.SysLogHandler',
            'address': '/dev/log',
            'formatter': 'simple',
        },
    },
    'loggers': {
        '': {
            'handlers': ['sys-logger', 'console'],
            'level': 'DEBUG',
        },
    }
}


if __name__ == "__main__":
    appobj = CapLocApp()
    appobj.standalone()