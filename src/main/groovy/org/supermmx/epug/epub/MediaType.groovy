package org.supermmx.epug.epub

enum MediaType {
    EPUB_PACKAGE('application/oebps-package+xml', [ 'xml' ]),

    GIF('image/gif', [ 'gif' ]),
    JPEG('image/jpeg', [ 'jpeg', 'jpg' ]),
    PNG('image/png', [ 'png' ]),
    SVG('image/svg+xml', [ 'svg' ]),
    XHTML('application/xhtml+xml', [ 'xhtml' ]),
    OPF('application/x-dtbncx+xml', [ 'opf' ]),
    OpenType('application/vnd.ms-opentype', [ 'otf', 'ttf', 'ttc']),
    WOFF('application/font-woff', [ 'woff', 'woff2']),
    SMIL('application/smil+xml', [ 'smil' ]),
    PLS('application/pls+xml', [ 'pls' ]),
    MP3('audio/mpeg', [ 'mp3' ]),
    MP4('audio/mp4', [ 'mp4' ]),
    CSS('text/css', [ 'css' ]),
    JS('text/javascript', [ 'js' ]),
    UNKNOWN('application/octet-stream', [])

    private static final Map<String, MediaType> extMap = [:]
    static {
        MediaType.values().each { mediaType ->
            mediaType.exts.each { ext ->
                extMap[(ext)] = mediaType
            }
        }
    }

    String mime
    List<String> exts = []

    MediaType(String mime, List<String> exts) {
        this.mime = mime
        this.exts = exts
    }

    static MediaType fromString(String value) {
        MediaType mediaType = extMap[(value)]
        if (mediaType == null) {
            mediaType = MediaType.UNKNOWN
        }

        return mediaType
    }
}
