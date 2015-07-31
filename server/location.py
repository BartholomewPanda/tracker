#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import json
from sklearn import neighbors
from sklearn.naive_bayes import MultinomialNB

class LocationManager:

    def __init__(self, path, nb_neighbors=9):
        self._path = path
        self._rooms = {}
        self._nb_neighbors = nb_neighbors
        try:
            with open(path, 'r') as f:
                try:
                    self._rooms = json.loads(f.read())
                except ValueError:
                    pass
        except FileNotFoundError:
            pass

    def save(self):
        with open(self._path, 'w') as f:
            f.write(json.dumps(self._rooms))

    def imhere(self, room, scan):
        '''
            Add a new scan to the room database.

            :param room: The room name
            :param scan: Scan of available bssids of the room
            :type room: string
            :type scan: dict (key is bssid and value is level)
        '''
        if not room in self._rooms:
            self._rooms[room] = []
        self._rooms[room].append(scan)

    def trackme(self, scan):
        '''
            Compute the actual location in terms of "scan".

            :param scan: Scan of available bssids of the room
            :type scan: dict (key is bssid and value is level)
            :return: The prediction of the current location
            :rtype: string
        '''
        # build a training data set
        bssids = scan.keys()
        data = []
        targets = []
        for room, scans in self._rooms.items():
            relevant_levels = self._get_levels(scans, bssids)
            data.extend(relevant_levels)
            targets.extend([room for _ in range(len(relevant_levels))])
        # build and train a new classifier
        clf = neighbors.KNeighborsClassifier(self._nb_neighbors)
        clf.fit(data, targets)
        predicted = clf.predict(self._get_levels([scan], bssids))
        if len(predicted) == 0:
            return None
        return predicted[0]

    def _get_levels(self, scans, bssids):
        '''
            Build a list of bssids levels.

            :param scans: The list of scans
            :param bssids: The list of bssids name
            :type scans: list of dict
            :type bssids: list of string
            :return: A list of levels list
            :rtype: list of list of int
        '''
        levels = []
        for scan in scans:
            data = []
            for bssid in bssids:
                if bssid in scan:
                    data.append(scan[bssid])
                else:
                    # if the bssid is not in the list, we compute an
                    # approximative value (average)
                    data.append(self._avg(scans, bssid))
            levels.append(data)
        return levels

    def _avg(self, scans, bssid):
        '''
            Compute the average of the bssid levels.

            :param scans: The list of scans
            :param bssid: The bssid
            :type scans: list of dict
            :type bssid: string
            :return: The average of the bssid levels
            :rtype: int
        '''
        tot = 0
        n = 0
        for scan in scans:
            if bssid in scan:
                tot += scan[bssid]
                n += 1
        if n == 0:
            # TODO: compute another average
            return -80
        return tot / n

