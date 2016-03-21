package org.supermmx.epug.creator

import org.supermmx.epug.epub.MediaType
import org.supermmx.epug.epub.Navigation
import org.supermmx.epug.epub.NavigationItem
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
    static final String NAV_FILE = 'epub-nav.xhtml'

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

        writeNavDocument(rendition, index)

        writeContent(rendition, index)
    }

    protected void writeNavDocument(Rendition rendition, int index) {
        if (rendition.navs.size() == 0) {
            Navigation nav = new Navigation(type: Navigation.Type.toc,
                                            title: '')
            NavigationItem item = new NavigationItem()
            rendition.navs << nav
        }

        zip.putNextEntry(new ZipEntry("OEBPS-${index}/${NAV_FILE}"))

        def writer = new StringWriter()

        MarkupBuilder builder = new MarkupBuilder(writer)
        builder.setOmitNullAttributes(true)

        def navItemAction
        navItemAction = { item ->
            if (item in Navigation) {
                if (item.type == Navigation.Type.toc) {
                    // TODO: I18N
                    builder.h1('Table of Contents')
                }
            } else {
                builder.a(href: item.file + (item.anchor ? ("#${item.anchor}") : ''),
                          item.title)
            }

            if (item.items.size() > 0) {
                builder.ol {
                    item.items.each { subItem ->
                        li {
                            navItemAction(subItem)
                        }
                    }
                }
            }
        }

        builder.pi('xml': [version: '1.0', encoding: 'UTF-8'])
        builder.yield('<!DOCTYPE html>\n', false)
        builder.html(xmlns: 'http://www.w3.org/1999/xhtml',
                     'xmlns:epub': 'http://www.idpf.org/2007/ops') {
            head {
                meta(charset: 'utf-8')
            }
            body {
                rendition.navs.each { nav ->
                    builder.nav('epub:type': nav.type) {
                        navItemAction(nav)
                    }
                }
            }
        }

        zip.write(writer.toString().getBytes('UTF-8'))

        zip.closeEntry()
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
                    "dc:${dcTerm.term.name}"(id: dcTerm.id, dcTerm.value)
                }
                rendition.metadata.metas.each { meta ->
                    builder.meta(id: meta.id,
                                 'xml:lang': meta.lang,
                                 refines: meta.refines == null ? null: "#${meta.refines}",
                                 property: meta.property,
                                 scheme: meta.scheme,
                                 meta.value)
                }
            }

            manifest {
                // nav document
                item(id: 'epub-nav',
                     href: NAV_FILE,
                     properties: 'nav',
                     'media-type': MediaType.XHTML.mime)

                rendition.manifest.items.each { id, item ->
                    builder.item(id: item.id,
                                 href: item.href,
                                 properties: item.properties.join(' '),
                                 'media-type': item.mediaType.mime)
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
