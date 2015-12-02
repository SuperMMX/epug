package org.supermmx.epug.creator

import org.supermmx.epug.epub.Publication
import org.supermmx.epug.epub.Rendition

import groovy.xml.MarkupBuilder

class EpubWriter {
    Publication publication
    File destPath

    void write() {
        if (!destPath.exists()) {
            destPath.mkdirs()
        }

        writeContainer()

        publication.renditions.eachWithIndex { rendition, index ->
            writeRendition(rendition, index)
        }
    }

    protected void writeContainer() {
        writeMimetype()
        writeContainerXml()
    }

    protected void writeMimetype() {
        File mimetypeFile = new File(destPath, 'mimetype')
        File parentFile = mimetypeFile.getParentFile()
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }

        mimetypeFile << 'application/epub+zip'
    }

    protected void writeContainerXml() {
        File container = new File(destPath, 'META-INF/container.xml')
        File parentFile = container.getParentFile()
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }

        FileWriter writer = new FileWriter(container)
        new MarkupBuilder(writer).container(xmlns: 'urn:oasis:names:tc:opendocument:xmlns:container', version: '1.0') {
            rootfiles {
                publication.renditions.eachWithIndex { rendition, index ->
                    rootfile('full-path': "OEBPS-${index}/content.opf",
                             'media-type': 'application/oebps-package+xml')
                }
            }
        }
    }

    protected void writeRendition(Rendition rendition, int index) {
        writePacakge(rendition, index)

        writeContent(rendition, index)
    }

    protected void writePacakge(Rendition rendition, int index) {
        writePackageDocument(rendition, index)
    }

    protected void writePackageDocument(Rendition rendition, int index) {
        File packageDocument = new File(destPath, "OEBPS-${index}/content.opf")
        File parentFile = packageDocument.getParentFile()
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }

        FileWriter writer = new FileWriter(packageDocument)

        MarkupBuilder builder = new MarkupBuilder(writer)
        builder.setOmitNullAttributes(true)
        builder.setOmitEmptyAttributes(true)
        builder.package(version: '3.0',
                        'unique-identifier': rendition.uniqueIdentifier) {
            metadata('xmlns:dc': 'http://purl.org/dc/elements/1.1/') {
                rendition.metadata.dcTerms.each { dcTerm ->
                    "dc:${dcTerm.term.name}"(dcTerm.value)
                }
                rendition.metadata.metas.each { meta ->
                    meta(refines: meta.refines,
                         property: meta.property,
                         scheme: meta.scheme,
                         meta.value)
                }
            }

            manifest {
                rendition.manifest.items.each { id, item ->
                    builder.item(id: item.id,
                                 href: item.href,
                                 properties: item.properties.join(' '),
                                 'media-type': item.mediaType)
                }
            }

            rendition.spine.with {
                spine(id: id, toc: toc, 'page-progression-direction': ppd) {
                    items.each { item ->
                        itemref(idref: item.idref,
                                linear: item.linear,
                                properties: item.properties.join(' '))
                    }
                }

            }
        }
    }

    protected void writeContent(Rendition rendition, int index) {

    }
}
