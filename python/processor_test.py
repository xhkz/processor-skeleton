#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os
import unittest

from processor import CompressFile, XML


class TestProcessor(unittest.TestCase):
    def setUp(self):
        self.test_path = os.path.dirname(os.path.realpath(__file__)) + '/../files/'

    def test_new_compress_file(self):
        xml_file = CompressFile(self.test_path + 'simple.xml')
        self.assertEqual(xml_file.path, self.test_path + 'simple.xml')
        self.assertEqual(xml_file.ext, '.xml')

    def test_non_exist_file(self):
        self.assertRaises(IOError, CompressFile, 'non_exist_file')

    def test_empty_file(self):
        empty_file = CompressFile(self.test_path + 'empty.xml')
        file_dict = empty_file.get_file_dict()
        self.assertEqual(file_dict, {'empty.xml': ''})
        self.assertEqual(XML(file_dict.keys()[0], file_dict.get('empty.xml')).parse(), [])
        self.assertEqual(XML(file_dict.keys()[0], file_dict.get('empty.xml')).parse_dynamic(), [])

    def test_normal_file(self):
        normal_file = CompressFile(self.test_path + '1.tar.gz')
        self.assertEqual(len(normal_file.get_file_dict()), 1)


if __name__ == '__main__':
    suite = unittest.TestLoader().loadTestsFromTestCase(TestProcessor)
    testResult = unittest.TextTestRunner(verbosity=2).run(suite)
