package org.supermmx.epug.epub

import org.supermmx.epug.epub.dcmi.DcElement

class Metadata {
    List<DcElement> dcTerms = []
    List<Meta> metas = []
    List<Link> links = []

    DcElement findDcElement(Closure condition) {
        return dcTerms.find(condition)
    }

    Meta findMeta(Closure condition) {
        return metas.find(condition)
    }
}
