package fr.uge.structsure.bluetooth.cs108

import com.csl.cslibrary4a.Cs108Library4A
import com.csl.cslibrary4a.RfidReaderChipData

/**
 * Represent a RFID chip read by a scanner.
 * @param id the name or id of the chip, such as 'E2806F1200000002208FFB9E'
 * @param attenuation strength of the signal in negative dB
 */
data class RfidChip(val id: String, val attenuation: Int) {
    /**
     * Converts the given Rx00pkgData (raw data from the CS108Lib) to
     * a RfidChip.
     * @param csLibrary4A access to the library to convert the data
     * @param data the non-null data read by the scanner
     */
    constructor(csLibrary4A: Cs108Library4A, data: RfidReaderChipData.Rx000pkgData): this(
        csLibrary4A.byteArrayToString(data.decodedEpc),
        (data.decodedRssi - csLibrary4A.dBuV_dBm_constant).toInt()
    )
}