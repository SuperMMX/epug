package org.supermmx.epug.creator

import org.supermmx.epug.epub.Publication
import org.supermmx.epug.epub.Rendition
import org.supermmx.epug.epub.Item
import org.supermmx.epug.epub.ItemRef
import org.supermmx.epug.storage.Storage
import org.supermmx.epug.storage.MemoryStorage

class EpubCreator {
    Storage storage
    Publication publication

    EpubCreator(Storage storage) {
        this.storage = storage

        init()
    }

    EpubCreator() {
        storage = new MemoryStorage()

        init()
    }

    private void init() {
        publication = new Publication()
    }

    /**
     * Add an non-spine item to the rendition
     *
     * @param srcPath the path of the source resource
     * @param destPath the relative destination path
     * @param id the specified id of the item, if null or empty,
     * the id will be automatically generated
     */
    Item addItem(String srcPath, String destPath, String id) {
        // TODO: generate id if necessary

        String mediaType = ''
        Item item = new Item(id, destPath, mediaType)
        publication.rendition.manifest.items[(id)] = item

        // TODO: load the item into the storage

        return item
    }

    ItemRef addSpineItem(String srcPath, String destPath, String id, String title) {
        Item item = addItem(srcPath, destPath, id)
        ItemRef ref = new ItemRef(item.id)

        publication.rendition.spine.items << ref

        return ref
    }

    /**
     * Write the publication
     *
     * @param outputFile the output file
     */
    void write(File outputFile) {
    }

    void write(OutputStream os) {
    }
}
