#!/usr/bin/env python3

class Block:
    '''
    Represents a block of code with an identifier. 

    This identifier usually corresponds to either a method or class
    declaration. Tags are used to keep track of the Feynstein context
    which this block exists within (i.e., is it in the shape block,
    the onFrame block, etc.).
    '''

    def __init__(self, block_id, children, tag=None):
        self.block_id = block_id
        self.children = children
        self.tag = tag

    def block_to_string(self, tab_level=0):
        '''
        Convert a block to Java syntax.
        '''

        rest = '  ' * tab_level + '%s { \n' % (self.block_id)
        for child in self.children:
            if isinstance(child, Block):
                rest += child.block_to_string(tab_level+1)
            else:
                rest += '  ' * (tab_level + 1) + '%s;\n' % child
        rest += '  ' * tab_level + '}\n'
        return rest

    def get_by_tag(self, tag):
        '''
        Returns the first child with the given tag. If it has no
        children with the correct tag, returns None.
        '''

        for child in self.children:
            if child.tag == tag: return child
        return None

    def __str__(self):
        return self.block_to_string()
