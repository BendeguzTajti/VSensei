package com.example.vsensei.util

import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun String.compress(): String {
    val bos = ByteArrayOutputStream()
//    GZIPOutputStream(bos).bufferedWriter(UTF_8).use { it.write(this) }
//    return bos.toByteArray().toString(Charset.defaultCharset())

//    val obj = ByteArrayOutputStream()
//    val gzip = GZIPOutputStream(obj)
//    gzip.write(this.toByteArray(StandardCharsets.ISO_8859_1))
    GZIPOutputStream(bos).bufferedWriter(UTF_8).use { it.write(this) }
//    gzip.close()
    return bos.toString(StandardCharsets.ISO_8859_1.name())
}

fun String.decompress(): String {
    val bytes = this.toByteArray(StandardCharsets.ISO_8859_1)
    val gis = GZIPInputStream(ByteArrayInputStream(bytes))
    val bf = BufferedReader(InputStreamReader(gis, StandardCharsets.ISO_8859_1))
    var outStr: String? = ""
    var line: String?
    while (bf.readLine().also { line = it } != null) {
        outStr += line
    }
    return outStr ?: "{}"
}