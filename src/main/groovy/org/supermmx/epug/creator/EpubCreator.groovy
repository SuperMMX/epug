package org.supermmx.epug.creator

import org.supermmx.epug.epub.Publication
import org.supermmx.epug.epub.Rendition
import org.supermmx.epug.epub.Item
import org.supermmx.epug.epub.ItemRef
import org.supermmx.epug.epub.Meta
import org.supermmx.epug.epub.MediaType
import org.supermmx.epug.epub.dcmi.DcElement
import org.supermmx.epug.epub.dcmi.DcTerm
import org.supermmx.epug.epub.dcmi.DcmiTerm
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
        if (!id) {
            id = destPath
        }

        def extIndex = srcPath.lastIndexOf('.')
        def ext = ''
        ext = srcPath.substring(extIndex + 1)
        MediaType mediaType = MediaType.fromString(ext)

        Item item = new Item(id, destPath, mediaType)
        publication.rendition.manifest.items[(id)] = item

        // TODO: load the item into the storage
        storage.addResource(srcPath, destPath)

        return item
    }

    ItemRef addSpineItem(String srcPath, String destPath, String id, String title) {
        Item item = addItem(srcPath, destPath, id)
        ItemRef ref = new ItemRef(item.id)

        publication.rendition.spine.items << ref

        return ref
    }

    Meta addMeta(String property, String value, Meta refinedMeta) {
        Meta meta = new Meta(property: property, value: value)
        if (refines != null) {
            meta.refines = refinedMeta.id
        }
        publication.rendition.metadata.metas << meta

        return meta
    }

    Meta addMetaModified(Date date) {
        Meta dateMeta = new Meta(property: "dcterms:${DcmiTerm.modified.name}",
                                 value: date.format("yyyy-MM-dd'T'HH:mm:ssXXX", TimeZone.getTimeZone('UTC')));
        publication.rendition.metadata.metas << dateMeta

        return dateMeta
    }

    DcElement addDcElement(DcTerm term, String value, String id) {
        DcElement dc = new DcElement(id: id, term: term, value: value)
        publication.rendition.metadata.dcTerms << dc

        return dc
    }
    /**
     * Write the publication
     *
     * @param outputFile the output file
     */
    void write(File outputFile) {
        EpubWriter writer = new EpubWriter(publication: publication,
                                           destPath: outputFile,
                                           storage: storage)
        writer.write()
    }

    void write(OutputStream os) {
    }
}
