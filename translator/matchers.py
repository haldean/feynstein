#!/usr/bin/env python3

import re

def method_for(method):
    '''
    Return a private instance method for a given method signature.
    '''

    return '@Override public void %s' % method

def for_unit(unit_name):
    '''
    Returns a pattern that matches a unit value, with the value in
    group 0.
    '''

    pattern = r'(\d+\.?\d*(?:[eE]\d+)?)\s*%s(?=[^a-zA-Z])' % unit_name
    return re.compile(pattern)

# Matches strings of whitespace
whitespace = re.compile(r'[\n\t ]+')

# Matches open or close braces and puts the matched brace in group 0
braces = re.compile(r'([{}])')

# Matches single-line comments of the // variety
line_comments = re.compile(r'//.*')

# Matches multiline comments, but only after all newlines have been
# removed.
multiline_comments = re.compile(r'/\*.*?\*/')

# Matches lowercase keywords.
keyword = re.compile(r'[a-z]+')

# Matches identifiers.
identifier = re.compile(r'[a-zA-Z][a-zA-Z0-9_]*')

# While this is not definitive proof of the usage of builder syntax,
# it is a good guess. A negative match means that an expression is
# conclusively not in builder syntax. A positive match means that an
# expression probably uses builder syntax.
builder_hint = re.compile(r'[a-zA-Z][a-zA-Z0-9_]*\(' +
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
