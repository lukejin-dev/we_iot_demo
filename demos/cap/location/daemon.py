#!/usr/bin/env python
# coding: utf-8
# vim: tabstop=4 expandtab shiftwidth=4 softtabstop=4 number
"""
Config file helper class. The config file is in json format.

Copyright 2015 (c) metanexus. All right reserved.
"""

__author__ = 'ken'

import os
import sys
import time
import atexit
import logging
import signal
import traceback


class Daemon(object):
    """
    A generic daemon class.
    Usage: subclass the Daemon class and override the run() method
    """

    def __init__(self, pidfile, stdin='/dev/null',
                 stdout='/dev/null', stderr='/dev/null'):
        self.stdin = stdin
        self.stdout = stdout
        self.stderr = stderr
        self.pidfile = pidfile

    def daemonize(self):
        """
        do the UNIX double-fork magic, see Stevens' "Advanced
        Programming in the UNIX Environment" for details (ISBN 0201563177)
        http://www.erlenstar.demon.co.uk/unix/faq_2.html#SEC16
        """
        # Do first fork
        self.fork()

        # Decouple from parent environment
        self.dettach_env()

        # Do second fork
        self.fork()

        # Flush standart file descriptors
        sys.stdout.flush()
        sys.stderr.flush()

        #
        self.attach_stream('stdin', mode='r')
        # print("stdin")
        if sys.version.startswith('3'):
            self.attach_stream('stdout', mode='ba+', buffering=0)
        elif sys.version.startswith('2'):
            self.attach_stream('stdout', mode='a+', buffering=0)

        self.attach_stream('stderr', mode='a+')

        # write pidfile
        try:
            self.create_pidfile()
        except EnvironmentError as ee:
            sys.stderr.write("Fail to create pid file " + self.pidfile)
            logging.error("Fail to create pid file " + self.pidfile)


    def attach_stream(self, name, mode, *args, **kwargs):
        """
        Replaces the stream with new one
        """
        stream = open(getattr(self, name), mode, *args, **kwargs)
        os.dup2(stream.fileno(), getattr(sys, name).fileno())

    def dettach_env(self):
        os.chdir("/")
        os.setsid()
        os.umask(0)

    def fork(self):
        """
        Spawn the child process
        """
        try:
            pid = os.fork()
            if pid > 0:
                sys.exit(0)
        except OSError as e:
            sys.stderr.write("Fork failed: %d (%s)\n" % (e.errno, e.strerror))
            logging.error("Fork failed: %d (%s)\n" % (e.errno, e.strerror))
            sys.exit(1)

    def create_pidfile(self):
        atexit.register(self.delpid)
        pid = str(os.getpid())
        open(self.pidfile,'w+').write("%s\n" % pid)

    def delpid(self):
        """
        Removes the pidfile on process exit
        """
        os.remove(self.pidfile)

    def start(self):
        """
        Start the daemon
        """
        # Check for a pidfile to see if the daemon already runs
        pid = self.get_pid()

        if pid:
            message = "pidfile %s already exist. Daemon already running?\n"
            sys.stderr.write(message % self.pidfile)
            logging.error(message % self.pidfile)
            sys.exit(1)

        # Start the daemon
        self.daemonize()
        try:
            self.run()
        except Exception as e:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            logging.error("Fail to execute as error: " + str(e) + "\n" +
                          "".join(traceback.format_exception(exc_type, exc_value, exc_traceback)))

    def get_pid(self):
        """
        Returns the PID from pidfile
        """
        try:
            pf = open(self.pidfile,'r')
            pid = int(pf.read().strip())
            pf.close()
        except (IOError, TypeError):
            pid = None
        return pid

    def stop(self, silent=False):
        """
        Stop the daemon
        """
        # Get the pid from the pidfile
        pid = self.get_pid()

        if not pid:
            if not silent:
                message = "pidfile %s does not exist. Daemon not running?\n"
                sys.stderr.write(message % self.pidfile)
                logging.error(message % self.pidfile)
            return # not an error in a restart

        # Try killing the daemon process
        try:
            while True:
                os.kill(pid, signal.SIGTERM)
                time.sleep(0.1)
        except OSError as err:
            err = str(err)
            if err.find("No such process") > 0:
                if os.path.exists(self.pidfile):
                    os.remove(self.pidfile)
            else:
                sys.stdout.write(str(err))
                logging.error(str(err))
                sys.exit(1)

    def restart(self):
        """
        Restart the daemon
        """
        self.stop(silent=True)
        self.start()

    def run(self):
        """
        You should override this method when you subclass Daemon. It will be called after the process has been
        daemonized by start() or restart().
        """
        raise NotImplementedError