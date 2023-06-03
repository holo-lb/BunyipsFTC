package org.firstinspires.ftc.teamcode.common

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.Telemetry.Item

/**
 * OpMode Abstract class that offers additional abstraction for opMode developers
 * including catch-all error handling and phased code execution.
 * Small modifications made by Lucas Bubner, FTC 15215
 */
abstract class BunyipsOpMode : LinearOpMode() {
    private var movingAverageTimer: MovingAverageTimer? = null
    private var loopCount: Long = 0
    private var operationsCompleted = false
    private val stickyTelemetryObjects = mutableListOf<Pair<Int, Item>>()

    /**
     * Implement this method to define the code to run when the Init button is pressed on the Driver Station.
     */
    protected abstract fun onInit()

    /**
     * Override to this method to allow code to run in a loop AFTER onInit has completed, until
     * start is pressed on the Driver Station or true is returned to this method.
     * If not implemented, the opMode will continue on as normal and wait for start.
     */
    @Throws(InterruptedException::class)
    protected open fun onInitLoop(): Boolean {
        return true
    }

    /**
     * Override to this method to allow code to execute once after all initialisation has finished.
     * Note if a task is running in an onInitLoop and start is pressed, this code will still be executed.
     */
    @Throws(InterruptedException::class)
    protected open fun onInitDone() {
    }

    /**
     * Override to this method to perform one time operations after start is pressed.
     * Unlike onInitDone, this will only execute once play is hit and not when initialisation is done.
     */
    @Throws(InterruptedException::class)
    protected fun onStart() {
    }

    /**
     * Override to this method to perform one time operations after the activeLoop finishes
     */
    @Throws(InterruptedException::class)
    protected fun onStop() {
    }

    /**
     * Implement this method to define the code to run when the Start button is pressed on the Driver station.
     * This method will be called on each hardware cycle.
     *
     * @throws InterruptedException
     */
    @Throws(InterruptedException::class)
    protected abstract fun activeLoop()

    /**
     * Override this method only if you need to do something outside of onInit() and activeLoop()
     *
     * @throws InterruptedException
     */
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        try {
            try {
                setup()
                onInit()
                while (opModeInInit()) {
                    if (onInitLoop()) break
                }
                onInitDone()
                telemetry.addData("BUNYIPSOPMODE", "INIT COMPLETE -- PLAY WHEN READY.")
                telemetry.update()
            } catch (e: Throwable) {
                ErrorUtil.handleCatchAllException(e, telemetry)
            }
            waitForStart()
            clearTelemetryData()
            movingAverageTimer?.reset()
            onStart()
            while (opModeIsActive() && !operationsCompleted) {
                try {
                    activeLoop()
                    loopCount++
                } catch (ie: InterruptedException) {
                    throw ie
                } catch (e: Throwable) {
                    ErrorUtil.handleCatchAllException(e, telemetry)
                }
                movingAverageTimer?.update()
                telemetry.update()
                idle()
            }

            // Wait for user to hit stop
            while (opModeIsActive()) {
                idle()
            }
        } finally {
            onStop()
        }
    }

    // One-time setup for operations that need to be done for the opMode
    private fun setup() {
        movingAverageTimer = MovingAverageTimer(100)
    }

    /**
     * Clear data from the telemetry cache
     */
    fun clearTelemetryData() {
        if (telemetry.isAutoClear) {
            telemetry.clear()
        } else {
            telemetry.clearAll()
        }
        if (opModeIsActive()) {
            idle()
        }
    }

    /**
     * Add data to the telemetry object
     * @param value A string to add to telemetry
     * @param retained Optional parameter to retain the data on the screen
     */
    fun addTelemetry(value: String, retained: Boolean = false) {
        val item = telemetry.addData(movingAverageTimer?.elapsedTime().toString(), value)
        if (retained) {
            item.setRetained(true)
            stickyTelemetryObjects.add(Pair(stickyTelemetryObjects.size + 1, item))
        }
    }

    /**
     * Remove an entry from the telemetry object if it is sticky
     */
    fun removeTelemetry(index: Int) {
        if (index > 0 && index <= stickyTelemetryObjects.size) {
            telemetry.removeItem(stickyTelemetryObjects[index - 1].second)
            stickyTelemetryObjects.removeAt(index - 1)
        }
        // Update indexes of sticky telemetry objects
        for (i in stickyTelemetryObjects.indices) {
            stickyTelemetryObjects[i] = Pair(i + 1, stickyTelemetryObjects[i].second)
        }
    }

    /**
     * Reset telemetry data, including retention
     */
    fun resetTelemetry() {
        telemetry.clearAll()
        stickyTelemetryObjects.clear()
    }

    /**
     * Clear telemetry on screen, not including retention
     */
    fun clearTelemetry() {
        telemetry.clear()
    }

    /**
     * Set telemetry auto clear status
     */
    fun setTelemetryAutoClear(autoClear: Boolean) {
        telemetry.isAutoClear = autoClear
    }

    /**
     * Get auto-clear status of telemetry
     */
    fun getTelemetryAutoClear(): Boolean {
        return telemetry.isAutoClear
    }

    /**
     * Convenience method to add a hardware error to OpMode telemetry
     */
    fun logHardwareErrors(failures: ArrayList<String>) {
        for (failure in failures) {
            val item =
                telemetry.addData("BUNYIPSOPMODE", "Failed to initialise device of name: $failure")
            // Make the item sticky to keep the error on the screen
            item.setRetained(true)
            stickyTelemetryObjects.add(Pair(stickyTelemetryObjects.size + 1, item))
        }
    }

    /**
     * Call to prevent hardware loop from calling activeLoop(), indicating an OpMode that is finished.
     */
    protected fun setOperationsCompleted() {
        operationsCompleted = true
        clearTelemetryData()
        telemetry.addData("BUNYIPSOPMODE", "ActiveLoop halted. All operations completed.")
        telemetry.update()
    }
}