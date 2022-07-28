package com.example.vsensei.view.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.*
import android.transition.Slide
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.vsensei.R
import com.example.vsensei.camera.QrCodeAnalyzer
import com.example.vsensei.camera.ScannerState
import com.example.vsensei.databinding.ActivityScannerBinding
import com.example.vsensei.viewmodel.GroupShareViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerBinding
    private lateinit var cameraExecutor: ExecutorService
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val groupShareViewModel: GroupShareViewModel by viewModel()

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            showRationaleDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindowAnimations()
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.closeButton.setOnClickListener {
            finish()
        }
        binding.flashButton.setOnClickListener { flashButton ->
            flashButton.let { view ->
                if (view.isSelected) {
                    view.isSelected = false
                    camera?.let {
                        if (it.cameraInfo.hasFlashUnit()) {
                            it.cameraControl.enableTorch(false)
                        }
                    }
                } else {
                    view.isSelected = true
                    camera?.let {
                        if (it.cameraInfo.hasFlashUnit()) {
                            it.cameraControl.enableTorch(true)
                        }
                    }
                }
            }
        }
        groupShareViewModel.scannerState.observe(this) { state ->
            when (state) {
                ScannerState.DETECTING -> {
                    checkCameraPermission()
                    binding.scannerHelpChip.text = getString(R.string.scanner_help)
                }
                ScannerState.CONFIRMING -> {
                    cameraProvider?.unbindAll()
                }
                ScannerState.INVALID_QR_CODE -> {
                    binding.scannerHelpChip.text = getString(R.string.invalid_qr_code)
                }
                else -> {
                    vibrate()
                    finish()
                }
            }
        }
    }

    private fun setupWindowAnimations() {
        val slide = Slide().apply {
            duration = resources.getInteger(R.integer.material_motion_duration_medium_2).toLong()
        }
        window.enterTransition = slide
        window.returnTransition = slide
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun checkCameraPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> startCamera()
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.cameraPreview.surfaceProvider) }
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(cameraExecutor, QrCodeAnalyzer(groupShareViewModel)) }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider?.unbindAll()
                camera = cameraProvider?.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
                binding.scannerToolbar.isVisible = true
                binding.qrCodeFrame.isVisible = true
                binding.scannerHelpChip.isVisible = true
            } catch (e: Exception) {
                showCameraErrorDialog()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun showCameraErrorDialog() {

    }

    private fun showRationaleDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_required)
            .setMessage(R.string.camera_rationale_message)
            .setPositiveButton(R.string.ok) { _, _ ->
                checkCameraPermission()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .create()
            .apply {
                setCanceledOnTouchOutside(false)
                show()
            }
    }

    @Suppress("DEPRECATION")
    private fun vibrate(){
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            else -> {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(50)
            }
        }
    }
}