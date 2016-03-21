package org.supermmx.epug.epub

trait NavigationItemContainer {
    String title
    List<NavigationItem> items = []

    NavigationItemContainer leftShift(NavigationItem item) {
        items << item

        return this
    }
}
