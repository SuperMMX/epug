package org.supermmx.epug.creator

import org.supermmx.epug.epub.Publication
import org.supermmx.epug.epub.Rendition
import org.supermmx.epug.storage.Storage

import groovy.xml.MarkupBuilder

import java.io.FileOutputStream
import java.io.StringWriter
import java.nio.charset.Charset
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry

class EpubWriter {
    Publication publication
    File destPath
    Storage storage

    private ZipOutputStream zip

    void write() {
        File dir = destPath.getParentFile()
        if (!dir.exists()) {
            dir.mkdirs()
        }

        zip = new ZipOutputStream(new FileOutputStream(destPath), Charset.forName('UTF-8'))

        writeContainer()

        publication.renditions.eachWithIndex { rendition, index ->
            writeRendition(rendition, index)
        }

        zip.close()
    }

    protected void writeContainer() {
        writeMimetype()
        writeContainerXml()
    }

    protected void writeMimetype() {
        zip.putNextEntry(new ZipEntry('mimetype'))

        zip.write('application/epub+zip'.getBytes('UTF-8'))

        zip.closeEntry()
    }

    protected void writeContainerXml() {
        zip.putNextEntry(new ZipEntry('META-INF/container.xml'))

        def writer = new StringWriter()
        new MarkupBuilder(writer).container(xmlns: 'urn:oasis:names:tc:opendocument:xmlns:container', version: '1.0') {
            rootfiles {
                publication.renditions.eachWithIndex { rendition, index ->
                    rootfile('full-path': "OEBPS-${index}/content.opf",
                             'media-type': 'application/oebps-package+xml')
                }
            }
        }

        zip.write(writer.toString().getBytes('UTF-8'))

        zip.closeEntry()
    }

    protected void writeRendition(Rendition rendition, int index) {
        writePacakge(rendition, index)

        writeContent(rendition, index)
    }

    protected void writePacakge(Rendition rendition, int index) {
        writePackageDocument(rendition, index)
    }

    protected void writePackageDocument(Rendition rendition, int index) {
        zip.putNextEntry(new ZipEntry("OEBPS-${index}/content.opf"))

        def writer = new StringWriter()

        MarkupBuilder builder = new MarkupBuilder(writer)
        builder.setOmitNullAttributes(true)
        builder.setOmitEmptyAttributes(true)
        builder.package(xmlns: 'http://www.idpf.org/2007/opf',
                        version: '3.0',
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

        zip.write(writer.toString().getBytes('UTF-8'))

        zip.closeEntry()
    }

    protected void writeContent(Rendition rendition, int index) {
        rendition.manifest.items.each { key, item ->
            byte[] content = storage.getResource(item.href)
            zip.putNextEntry(new ZipEntry("OEBPS-${index}/${item.href}"))
            zip.write(content)
            zip.closeEntry()
        }
    }
}
