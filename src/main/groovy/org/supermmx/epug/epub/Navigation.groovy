package org.supermmx.epug.epub

class Navigation implements NavigationItemContainer {
    static enum Type {
        toc, landmarks, loa, loi, lot, lov
    }

    Navigation.Type type
}
