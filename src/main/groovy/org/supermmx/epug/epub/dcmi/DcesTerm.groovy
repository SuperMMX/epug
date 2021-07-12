package org.supermmx.epug.epub.dcmi

class DcesTerm extends DcTerm {
    static final String BASE_URI = 'http://purl.org/dc/elements/1.1/'

    static final DcesTerm contributor = [ baseUri: BASE_URI, name:'contributor'] as DcesTerm
    static final DcesTerm identifier = [ baseUri: BASE_URI, name:'identifier'] as DcesTerm
    static final DcesTerm title = [ baseUri: BASE_URI, name:'title'] as DcesTerm
    static final DcesTerm language = [ baseUri: BASE_URI, name:'language'] as DcesTerm
}
