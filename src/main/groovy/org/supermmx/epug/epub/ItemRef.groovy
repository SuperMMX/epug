package org.supermmx.epug.epub

class ItemRef extends Element {
    String idref
    boolean linear = true
    List<String> properties = []

    ItemRef(String idref) {
        this.idref = idref
    }
}
