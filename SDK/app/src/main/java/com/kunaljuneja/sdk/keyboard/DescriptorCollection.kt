package com.kunaljuneja.sdk.keyboard

internal object DescriptorCollection {

    const val ID_KEYBOARD : Byte = 1

    val KEYBOARD : ByteArray = byteArrayOf(
        0x05.toByte(),  0x01.toByte(), // Usage page (Generic Desktop)
         0x09.toByte(),  0x06.toByte(), // Usage (Keyboard)
         0xA1.toByte(),  0x01.toByte(), // Collection (Application)
         0x85.toByte(), ID_KEYBOARD, //    Report ID
         0x05.toByte(),  0x07.toByte(), //       Usage page (Key Codes)
         0x19.toByte(),  0xE0.toByte(), //       Usage minimum (224)
         0x29.toByte(),  0xE7.toByte(), //       Usage maximum (231)
         0x15.toByte(),  0x00.toByte(), //       Logical minimum (0)
         0x25.toByte(),  0x01.toByte(), //       Logical maximum (1)
         0x75.toByte(),  0x01.toByte(), //       Report size (1)
         0x95.toByte(),  0x08.toByte(), //       Report count (8)
         0x81.toByte(),  0x02.toByte(), //       Input (Data, Variable, Absolute) ; Modifier byte
         0x75.toByte(),  0x08.toByte(), //       Report size (8)
         0x95.toByte(),  0x01.toByte(), //       Report count (1)
         0x81.toByte(),  0x01.toByte(), //       Input (Constant)                 ; Reserved byte
         0x75.toByte(),  0x08.toByte(), //       Report size (8)
         0x95.toByte(),  0x06.toByte(), //       Report count (6)
         0x15.toByte(),  0x00.toByte(), //       Logical Minimum (0)
         0x25.toByte(),  0x65.toByte(), //       Logical Maximum (101)
         0x05.toByte(),  0x07.toByte(), //       Usage page (Key Codes)
         0x19.toByte(),  0x00.toByte(), //       Usage Minimum (0)
         0x29.toByte(),  0x65.toByte(), //       Usage Maximum (101)
         0x81.toByte(),  0x00.toByte(), //       Input (Data, Array)              ; Key array (6 keys)
         0xC0.toByte(),              // End Collection
    )

}