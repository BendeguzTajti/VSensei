package com.example.vsensei.camera

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.vsensei.viewmodel.GroupShareViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val groupShareViewModel: GroupShareViewModel
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val img = image.image
        if (img != null) {
            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
            val scanner = BarcodeScanning.getClient(options)
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val valueType = barcode.valueType
                        if (valueType == Barcode.TYPE_TEXT) {
                            barcode.rawBytes?.let {
                                groupShareViewModel.scanBarCode(it)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    // Handle error
                }.addOnCompleteListener {
                    image.close()
                }
        }
    }
}