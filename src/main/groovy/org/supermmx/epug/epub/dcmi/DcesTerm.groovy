package org.supermmx.epug.epub.dcmi

class DcesTerm extends DcTerm {
    static final DcesTerm contributer = new DcesTerm('contributer')
    static final DcesTerm title = new DcesTerm('title')
    static final DcesTerm language = new DcesTerm('language')

    String baseUri = 'http://purl.org/dc/elements/1.1/'
}
