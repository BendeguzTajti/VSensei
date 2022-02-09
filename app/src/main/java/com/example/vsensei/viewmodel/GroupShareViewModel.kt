package com.example.vsensei.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vsensei.camera.ScannerState
import com.example.vsensei.data.WordGroupWithWords
import com.example.vsensei.repository.Repository
import com.example.vsensei.util.Resource
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GroupShareViewModel(private val repository: Repository) : ViewModel() {

    private var _qrCode: MutableLiveData<Resource<Bitmap>>? = null

    private val _scannerState = MutableLiveData(ScannerState.DETECTING)
    val scannerState: LiveData<ScannerState> = _scannerState

    fun qrCode(
        wordGroupWithWords: WordGroupWithWords,
        qrCodeSize: Int
    ): LiveData<Resource<Bitmap>> {
        if (_qrCode == null) {
            _qrCode = MutableLiveData(Resource.Loading())
            generateQrCode(wordGroupWithWords, qrCodeSize)
        }
        return _qrCode!!
    }

    private fun generateQrCode(wordGroupWithWords: WordGroupWithWords, qrCodeSize: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(
                repository.compressWordGroup(wordGroupWithWords),
                BarcodeFormat.QR_CODE,
                qrCodeSize,
                qrCodeSize,
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            _qrCode?.postValue(Resource.Success(bitmap))
        }
    }

    fun scanBarCode(byteArray: ByteArray) {
        if (_scannerState.value == ScannerState.DETECTING) {
            viewModelScope.launch {
                _scannerState.value = ScannerState.CONFIRMING
                try {
                    val sharedGroup = repository.decompressSharedGroup(byteArray)
                    repository.saveSharedGroup(sharedGroup)
                    _scannerState.value = ScannerState.DETECTED
                } catch (e: Exception) {
                    _scannerState.value = ScannerState.INVALID_QR_CODE
                    delay(2000)
                    _scannerState.value = ScannerState.DETECTING
                }
            }
        }
    }
}