package org.supermmx.epug.epub

class Item extends Element {
    String href
    MediaType mediaType
    String fallback
    List<String> properties = []
    String mediaOverlay

    Item(String id, String href, MediaType mediaType) {
        this.id = id
        this.href = href
        this.mediaType = mediaType
    }
}
