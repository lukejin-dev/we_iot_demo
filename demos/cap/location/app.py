#!/usr/bin/env python
# coding: utf-8
# vim: tabstop=4 expandtab shiftwidth=4 softtabstop=4 number

"""
Location app running on CAP.

@author: Lu, Ken (bluewish.ken.lu@gmail.com)

"""
import os, sys
import logging
import logging.config
import signal
import bluetooth._bluetooth as bluez
import struct
import datetime
from daemon import Daemon

LE_META_EVENT = 0x3e
OGF_LE_CTL=0x08
OCF_LE_SET_SCAN_ENABLE=0x000C

# these are actually subevents of LE_META_EVENT
EVT_LE_CONN_COMPLETE=0x01
EVT_LE_ADVERTISING_REPORT=0x02

class CapLocApp(Daemon):

    def __init__(self, app_path=None):
        self._app_path = None
        self._dev_id = 0
        self._dev_sock = None
        self._is_terminate = False
        self._old_filter = None
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

        signal.signal(signal.SIGINT, signal_handler)

        if not self._open_bluetooth():
            logging.error("App quit....")
            return

        self._hci_set_le_scan_params()
        self._hci_enable_le_scan()

        last_report_time = datetime.datetime.now()
        update_dict = {}
        while not self._is_terminate:
            results = self._get_scan_result()
            for key in results:
                update_dict[key] = results[key]
            current_time = datetime.datetime.now()
            diff = (current_time - last_report_time).total_seconds()
            if diff > 1:
                logging.debug("report >>>")
                message = ""
                for key in update_dict.keys():
                    logging.debug("  addr: %s, rssi: -%d" % (key, 255 - update_dict[key]))
                update_dict.clear()
                last_report_time = datetime.datetime.now()

        self._hci_disable_le_scan()

    def _get_scan_result(self):
        ret_dict = {}
        pkt = self._dev_sock.recv(255)
        ptype, event, plen = struct.unpack("BBB", pkt[:3])
        #logging.debug("Packet type: " + str(ptype) + " event: " + str(event) + " len: " + str(plen))
        if event == LE_META_EVENT:
            subevent, = struct.unpack("B", pkt[3:4])
            remain = pkt[4:]
            if subevent == EVT_LE_ADVERTISING_REPORT:
                #logging.debug("ble advertising event...")
                numbers = struct.unpack("B", remain[0:1])[0]
                offset = 1
                for index in range(0, numbers):
                    addr_str = self._packed_bdaddr_to_string(remain[offset + 2:offset + 8])     # ble addr
                    length = struct.unpack("B", remain[offset + 8 :offset + 9])[0]
                    rssi = struct.unpack("B", remain[offset + 9 + length:offset + 9 + length + 1])[0]
                    offset = offset + 9 + length + 2
                    ret_dict[addr_str] = rssi
        return ret_dict

    def _packed_bdaddr_to_string(self, bdaddr_packed):
        return ':'.join('%02x'%i for i in struct.unpack("<BBBBBB", bdaddr_packed[::-1]))

    def _open_bluetooth(self):
        try:
            self._dev_sock = bluez.hci_open_dev(0)
        except:
            logging.error("Fail to open bluetooth device.")
            return False
        return True

    def _hci_set_le_scan_params(self):
        # set scan parameter
        self._old_filter = self._dev_sock.getsockopt( bluez.SOL_HCI, bluez.HCI_FILTER, 14)
        new_filter = bluez.hci_filter_new()
        bluez.hci_filter_all_events(new_filter)
        bluez.hci_filter_set_ptype(new_filter, bluez.HCI_EVENT_PKT)
        self._dev_sock.setsockopt( bluez.SOL_HCI, bluez.HCI_FILTER, new_filter)

    def _hci_toggle_le_scan(self, enable):
        cmd_pkt = struct.pack("<BB", enable, 0x00)
        bluez.hci_send_cmd(self._dev_sock, OGF_LE_CTL, OCF_LE_SET_SCAN_ENABLE, cmd_pkt)

    def _hci_enable_le_scan(self):
        self._hci_toggle_le_scan(0x01)

    def _hci_disable_le_scan(self):
        self._hci_toggle_le_scan(0x00)
        self._dev_sock.setsockopt( bluez.SOL_HCI, bluez.HCI_FILTER, self._old_filter)

    def terminate(self):
        self._is_terminate = True
        self._hci_disable_le_scan()

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

def signal_handler(signal, frame):
    logging.warn('You pressed Ctrl+C!')
    appobj.terminate()
    sys.exit(0)

if __name__ == "__main__":
    global appobj
    appobj = CapLocApp()
    appobj.standalone()