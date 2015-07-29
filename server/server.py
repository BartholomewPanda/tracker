#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import socketserver
import json
import signal
import location

manager = location.LocationManager('./rooms.json')

def signal_handler(signal, frame):
    manager.save()
    sys.exit(0)

class QueryHandler(socketserver.BaseRequestHandler):

    INVALID_QUERY = json.dumps({'state': 'error', 'info': 'invalid query'})
    UNKNOWN_ERROR = json.dumps({'state': 'error', 'info': 'unknown error'})
    OK_VALUE = json.dumps({'state': 'ok'})

    def imhere(self, query):
        if not 'room' in query or not 'scan' in query:
            return self.INVALID_QUERY
        manager.imhere(query['room'], query['scan'])
        return self.OK_VALUE

    def trackme(self, query):
        if not 'scan' in query:
            return self.INVALID_QUERY
        predicted = manager.trackme(query['scan'])
        if predicted == None:
            return UNKNOWN_ERROR
        return json.dumps({'state': 'ok', 'predicted': predicted})

    def handle(self):
        data = self.request[0].decode('utf8')
        socket = self.request[1]
        query = json.loads(data)
        print(query)
        if not 'action' in query:
            response = self.INVALID_QUERY
        elif query['action'] == 'imhere':
            response = self.imhere(query)
        elif query['action'] == 'trackme':
            response = self.trackme(query)
        else:
            response = self.INVALID_QUERY
        socket.sendto(response.encode(), self.client_address)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print('usage: %s port' % sys.argv[0])
        sys.exit(1)
    signal.signal(signal.SIGINT, signal_handler)
    server = socketserver.UDPServer(('', int(sys.argv[1])), QueryHandler)
    print('Server is running...')
    server.serve_forever()

