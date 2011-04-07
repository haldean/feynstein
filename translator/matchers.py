#!/usr/bin/env python3

import re

def method_for(method):
    return '@Override private void %s' % method

keyword = re.compile(r'[a-z]+')
identifier = re.compile(r'[a-zA-Z][a-zA-Z0-9_]*')
builder_hint = re.compile(r'\s*[a-zA-Z][a-zA-Z0-9_]*\(' +
                          r'[a-zA-Z][a-zA-Z0-9_]*=[^=]')
outer_parens = re.compile(r'([a-zA-Z][a-zA-Z0-9_]*)\((.*)\)')
accessor = re.compile(r'\#([a-zA-Z][a-zA-Z0-9_]*)')

block_translations = {
    'shapes': method_for('createShapes()'),
    'forces': method_for('createForces()'),
    'properties': method_for('setProperties()'),
    'onFrame': method_for('onFrame()'),
    }
