package org.supermmx.epug.epub.dcmi

class DcmiTerm extends DcTerm {
    static final String BASE_URI = 'http://purl.org/dc/terms/'
    static final DcmiTerm modified = [baseUri: BASE_URI, name: 'modified'] as DcmiTerm
    static final DcmiTerm creator = [baseUri: BASE_URI, name: 'creator'] as DcmiTerm
}
