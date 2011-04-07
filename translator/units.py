#!/usr/bin/env python3

import blocks, matchers, re

conversions = {
    # Length
    'km': 1000,
    'm': 1,
    'cm': 0.01,
    'mm': 0.001,

    'mi': 1609.34,
    'yd': 0.9144,
    'ft': 0.3048,

    # Time
    'year': 3.154e7,
    'month': 2.628e6,
    'week': 604800,
    'day': 86400,
    'hour': 3600,
    'min': 60,
    'sec': 1,
    'ms': 0.001,
    'microsec': 1e-6,

    # Mass
    'tonne': 1000,
    'kg': 1,
    'g': 0.001,

    'ton': 1016.0469,
    'lb': 0.4536,
    'oz': 0.028349,

    # Force
    'forcelb': 4.448,
    'newton': 1,
    'dyne': 1e-5,
    }

unit_matchers = {}
for unit in conversions.keys():
    unit_matchers[unit] = matchers.for_unit(unit)

def translate_units_for_expr(expr):
    if expr and re.search('\d', expr):
        for unit, m in unit_matchers.items():
            expr = re.sub(m, r'(\1*%f)' % conversions[unit], expr)
    return expr

def translate_units(block):
    for i, expr in enumerate(block.children):
        if isinstance(expr, blocks.Block):
            expr.block_id = translate_units_for_expr(expr.block_id)
            translate_units(expr)
        else:
            block.children[i] = translate_units_for_expr(expr)
