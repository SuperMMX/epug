package org.supermmx.epug.epub

class ItemRef extends Element {
    String idref
    Linear linear = Linear.yes
    List<String> properties = []

    ItemRef(String idref) {
        this.idref = idref
    }
}
