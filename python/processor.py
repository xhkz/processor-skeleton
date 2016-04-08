#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os
import argparse
import csv
import random
import gzip
import tarfile
import zipfile
import bz2

from lxml import etree


class CompressFile:
    """Class of compressed file

    initialize the name and ext of compressed file

    Attributes:
        path -- path of the compressed file
        name -- filename including base path
        ext  -- extension of compressed file
    """

    def __init__(self, path):
        if not os.path.isfile(path):
            raise IOError('File not exist')

        self.path = path
        self.name, self.ext = os.path.splitext(path)

    def get_file_dict(self):
        """get dict of file name and file content in the compressed file"""
        ext = self.ext.lower()
        try:
            if ext == '.zip':
                archive = zipfile.ZipFile(self.path)
                return {name: archive.read(name) for name in archive.namelist()}
            elif ext == '.tar' or os.path.splitext(self.name)[1] == '.tar':
                # handling tar file or tar.[gz|bz2]
                archive = tarfile.open(self.path)
                return {info.name: archive.extractfile(info.name).read() for info in archive.getmembers()}
            elif ext == '.gz':
                # handling single gz file
                return {self._extract_name(self.name): gzip.GzipFile(self.path).read()}
            elif ext == '.bz2':
                # handling single bz2 file
                return {self._extract_name(self.name): bz2.BZ2File(self.path).read()}
            elif ext == '.xml':
                # handling native xml file
                return {self._extract_name(self.path): open(self.path).read()}
            else:
                print 'unknown compressed file type: %s' % self.path
                return {}
        except Exception, e:
            print 'file dict exception: %s' % e.message
            return {}

    @staticmethod
    def _extract_name(name):
        return name.split('/')[-1]

    def __str__(self):
        return 'Compress file: %s, %s, %s' % (self.path, self.name, self.ext)


class XML:
    """Class of xml file object

    Attributes:
        name    -- name of the xml file
        content -- content of the xml file
    """

    def __init__(self, name, content):
        self.name = name
        self.content = content

    tags = ['name', 'price', 'description', 'calories']

    def parse(self):
        """parse the content of xml file to rows"""
        rows = []
        if self.content:
            doc = etree.fromstring(self.content)
            for food in doc.findall('food'):
                try:
                    # get attribute food.get('id')
                    rows.append([food.find(tag).text or '' for tag in self.tags])
                except AttributeError, ae:
                    print ae.message

        return rows

    def parse_dynamic(self):
        """extract all children of each node in dict"""
        rows = []
        if self.content:
            doc = etree.fromstring(self.content)
            for child in doc.getchildren():
                rows.append({element.tag: element.text for element in child.getchildren()})

        return rows

    def show(self):
        print self.content

    def to_csv(self):
        return CSV(self.name, self.tags, self.parse())

    def to_csv_dynamic(self):
        """
            get all unique keys in each row
            reset each dict in rows to list and reset order of each row to map headers
        """
        rows = self.parse_dynamic()
        headers = sorted(list(set(key for row in rows for key in row.keys())))
        rows = map(lambda x: [x.get(h, '') for h in headers], rows)
        return CSV(self.name, headers, rows)


class CSV:
    """Class of csv file object

    Attributes:
        name   -- name of csv file
        header -- list of columns' names
        rows   -- values in each row
    """

    def __init__(self, name, header, rows):
        self.name = name
        self.header = header
        self.rows = rows

    def save(self, base='./'):
        save_name = base + self.name + '.csv'

        if os.path.isfile(save_name):
            save_name += str(random.random())

        with open(save_name, 'wb') as f:
            writer = csv.writer(f, delimiter=',', quotechar='"')
            writer.writerow(self.header)
            writer.writerows(self.rows)

        print 'csv file is saved to %s' % save_name

    def show(self):
        print ','.join(self.header)
        for row in self.rows:
            print ','.join(row)


if __name__ == '__main__':
    # parse arguments
    parser = argparse.ArgumentParser()
    parser.add_argument('files', metavar='input_file', type=str, nargs='+', help='path of input files')
    parser.add_argument('--show', action='store_true', help='show csv file content')
    parser.add_argument('--save', action='store_true', help='save csv file')
    args = parser.parse_args()

    files = map(lambda x: CompressFile(x), args.files)

    for f in files:
        print 'Processing %s' % f.path
        file_dict = f.get_file_dict()
        for k, v in file_dict.iteritems():
            print '--Processing %s in %s' % (k, f.path)
            if k.lower().endswith('xml'):
                csv_file = XML(k, v).to_csv()
                # csv_file = XML(k, v).to_csv_dynamic()
                if args.show:
                    csv_file.show()
                if args.save:
                    csv_file.save()
