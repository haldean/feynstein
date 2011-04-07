#!/usr/bin/env python3

import re

def method_for(method):
    '''
    Return a private instance method for a given method signature.
    '''

    return '@Override private void %s' % method

# Matches lowercase keywords.
keyword = re.compile(r'[a-z]+')

# Matches identifiers.
identifier = re.compile(r'[a-zA-Z][a-zA-Z0-9_]*')

# While this is not definitive proof of the usage of builder syntax,
# it is a good guess. A negative match means that an expression is
# conclusively not in builder syntax. A positive match means that an
# expression probably uses builder syntax.
builder_hint = re.compile(r'\s*[a-zA-Z][a-zA-Z0-9_]*\(' +
                          r'[a-zA-Z][a-zA-Z0-9_]*=[^=]')

# Matches an identifier followed by a paren-wrapped string. First
# group is the identifier, second group is the argument list.
outer_parens = re.compile(r'([a-zA-Z][a-zA-Z0-9_]*)\((.*)\)')

# Matches a shape accessor.
accessor = re.compile(r'\#([a-zA-Z][a-zA-Z0-9_]*)')

# Definitions for block method translation.
block_translations = {
    'shapes': method_for('createShapes()'),
    'forces': method_for('createForces()'),
    'properties': method_for('setProperties()'),
    'onFrame': method_for('onFrame()'),
    }
