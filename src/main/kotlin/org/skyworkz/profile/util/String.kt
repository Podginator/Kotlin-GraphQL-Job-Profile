package org.skyworkz.profile.util

import java.nio.ByteBuffer
import java.nio.charset.Charset

fun String.toByteBuffer() : ByteBuffer = ByteBuffer.wrap(this.toByteArray(Charset.defaultCharset()))