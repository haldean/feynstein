#!/usr/bin/env python3

from translator import SyntaxException

def check_syntax(root):
    '''
    Ensure that no syntactic rules are broken.
    '''

    scene = root.get_by_tag('scene')
    check_blocks(scene)

def check_blocks(scene):
    '''
    Ensure that all of the correct blocks are present.
    '''

    # if not scene.get_by_tag('shapes'):
    #     raise SyntaxException('No shapes block found.')
    # if not scene.get_by_tag('forces'):
    #     raise SyntaxException('No forces block found.')
    # if not scene.get_by_tag('properties'):
    #     raise SyntaxException('No properties block found.')
    if not scene.get_by_tag('onFrame'):
        print('Warning: no onFrame block found. ' +
              'An empty frame update method will be used.')
