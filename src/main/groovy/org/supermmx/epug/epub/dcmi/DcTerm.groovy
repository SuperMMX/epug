package org.supermmx.epug.epub.dcmi

abstract class DcTerm {
    String name
    String label
    String uri
    String definition
    String type
    String baseUri

    String getUri() {
        if (!uri) {
            uri = baseUri + name
        }

        return uri
    }
}
