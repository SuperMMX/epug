package org.supermmx.epug.epub

import org.supermmx.epug.storage.Storage
import org.supermmx.epug.storage.MemoryStorage

/**
 * An Epub publication that contains one or more renditions,
 * and will be packaged in the container .epub file.
 *
 * It contains only one corresponding container.xml
 */
class Publication {
    List<Rendition> renditions = [ new Rendition() ]

    Rendition getDefaultRendition() {
        Rendition rendition = null

        if (renditions.size() > 0) {
            rendition = renditions[0]
        }

        return rendition
    }

    Rendition getRendition() {
        return getDefaultRendition()
    }
}
