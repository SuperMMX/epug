package org.supermmx.epug.epub

/**
 * An Epub rendition, which contains the corresponding .opf package document
 */
class Rendition {
    /**
     * The id of the primary dc:identifier element in the metadata
     */
    String uniqueIdentifier
    Metadata metadata = new Metadata()
    Manifest manifest = new Manifest()
    Spine spine = new Spine()

    List<Navigation> navs = []

    Item findItem(String id) {
        return manifest.items[(id)]
    }
}
