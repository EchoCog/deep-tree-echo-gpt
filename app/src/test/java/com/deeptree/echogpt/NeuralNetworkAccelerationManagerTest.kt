package com.deeptree.echogpt

import android.content.Context
import com.deeptree.echogpt.manager.NeuralNetworkAccelerationManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for NeuralNetworkAccelerationManager
 */
@RunWith(MockitoJUnitRunner::class)
class NeuralNetworkAccelerationManagerTest {

    @Mock
    private lateinit var context: Context

    private lateinit var accelerationManager: NeuralNetworkAccelerationManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        accelerationManager = NeuralNetworkAccelerationManager(context)
    }

    @Test
    fun `initialize should detect hardware capabilities`() {
        // Given
        whenever(context.packageName).thenReturn("com.deeptree.echogpt")
        
        // When
        val result = accelerationManager.initialize()
        
        // Then
        assertTrue(result) // Should initialize successfully even without hardware acceleration
    }

    @Test
    fun `getOptimizedInterpreterOptions should return valid options`() {
        // Given
        accelerationManager.initialize()
        
        // When
        val options = accelerationManager.getOptimizedInterpreterOptions()
        
        // Then
        assertNotNull(options)
    }

    @Test
    fun `getCurrentAccelerationType should return valid type`() {
        // Given
        accelerationManager.initialize()
        
        // When
        val accelerationType = accelerationManager.getCurrentAccelerationType()
        
        // Then
        assertNotNull(accelerationType)
        assertTrue(accelerationType in listOf("CPU", "CPU_OPTIMIZED", "GPU", "NNAPI"))
    }

    @Test
    fun `getAccelerationInfo should return complete information`() {
        // Given
        accelerationManager.initialize()
        
        // When
        val info = accelerationManager.getAccelerationInfo()
        
        // Then
        assertNotNull(info)
        assertNotNull(info.type)
        assertNotNull(info.supportedOperations)
        assertNotNull(info.performanceMetrics)
        assertTrue(info.supportedOperations.isNotEmpty())
    }

    @Test
    fun `getHardwareCapabilities should return system information`() {
        // Given
        accelerationManager.initialize()
        
        // When
        val capabilities = accelerationManager.getHardwareCapabilities()
        
        // Then
        assertNotNull(capabilities)
        assertTrue(capabilities.cpuCores > 0)
        assertNotNull(capabilities.supportedDelegates)
        assertTrue(capabilities.supportedDelegates.isNotEmpty())
    }

    @Test
    fun `benchmarkAccelerationMethods should return performance metrics`() {
        // Given
        accelerationManager.initialize()
        
        // When
        val benchmarks = accelerationManager.benchmarkAccelerationMethods()
        
        // Then
        assertNotNull(benchmarks)
        assertTrue(benchmarks.containsKey("CPU"))
        assertTrue(benchmarks["CPU"]!! > 0f) // CPU benchmark should return positive time
    }

    @Test
    fun `optimizeForPerformance should not throw exceptions`() {
        // Given
        accelerationManager.initialize()
        
        // When & Then - Should not throw
        accelerationManager.optimizeForPerformance()
    }

    @Test
    fun `cleanup should not throw exceptions`() {
        // When & Then - Should not throw
        accelerationManager.cleanup()
    }
}