package org.supermmx.epug.epub

class Spine extends Element {
    String toc
    /**
     * page-progression-direction
     */
    String ppd

    List<ItemRef> items = []
}
