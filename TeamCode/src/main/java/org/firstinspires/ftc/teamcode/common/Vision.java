package org.firstinspires.ftc.teamcode.common;


import android.util.Size;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.common.vision.Processor;
import org.firstinspires.ftc.teamcode.common.vision.data.VisionData;
import org.firstinspires.ftc.vision.VisionPortal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Component wrapper to support the v8.2+ SDK's included libraries for Camera operation.
 * This is an expansible system to run Processor components using the VisionPortal.
 *
 * @author Lucas Bubner, 2023
 */
public class Vision extends BunyipsComponent {
    @SuppressWarnings("rawtypes")
    private final List<Processor> processors = new ArrayList<>();
    private final WebcamName webcam;
    private VisionPortal visionPortal;

    public Vision(@NonNull BunyipsOpMode opMode, WebcamName webcam) {
        super(opMode);
        this.webcam = webcam;
    }

    /**
     * Builds the VisionPortal after the VisionPortal has been constructed.
     *
     * @param builder Processor-rich builder pattern for the VisionPortal
     * @return VisionPortalImpl
     */
    private VisionPortal constructVisionPortal(VisionPortal.Builder builder) {
        return builder
                .setCamera(webcam)
                .setCameraResolution(new Size(1280, 720))
                .enableLiveView(true)
                .setAutoStopLiveView(true)
                // Set any additional VisionPortal settings here
                .build();
    }

    /**
     * Initialises the Vision class with the specified processors.
     * This method should only be called once per OpMode. Additional calls will internally
     * terminate the VisionPortal and reinitialise it with the new processors (this is a highly expensive operation).
     * Processors will be STOPPED by default, you must call {@code start()} after initialising.
     *
     * @param processors TFOD and/or AprilTag
     */
    @SuppressWarnings("rawtypes")
    public void init(Processor... processors) {
        if (visionPortal != null) {
            getOpMode().log("WARNING: Vision already initialised! Tearing down...");
            visionPortal.close();
            visionPortal = null;
        }

        if (processors.length == 0) {
            throw new IllegalArgumentException("Must initialise at least one integrated processor!");
        }

        // Hand over instance control to the VisionPortal
        this.processors.addAll(Arrays.asList(processors));

        // Initialise the VisionPortal with our newly created processors
        VisionPortal.Builder builder = new VisionPortal.Builder();
        for (Processor processor : processors) {
            if (processor == null) {
                throw new IllegalStateException("Processor is not instantiated!");
            }
            builder.addProcessor(processor);
        }

        visionPortal = constructVisionPortal(builder);

        // Disable the vision processors by default. The OpMode must call start() to enable them.
        for (Processor processor : this.processors) {
            visionPortal.setProcessorEnabled(processor, false);
        }

        // Disable live view by default
        visionPortal.stopLiveView();
    }

    /**
     * Start desired processors. This method must be called before trying to extract data from
     * the cameras, and must be already initialised with the init() method.
     *
     * @param processors TFOD and/or AprilTag
     */
    @SuppressWarnings("rawtypes")
    public void start(Processor... processors) {
        if (visionPortal == null) {
            throw new IllegalStateException("VisionPortal is not initialised from init()!");
        }

        // Resume the stream if it was previously stopped or is not running
        if (visionPortal.getCameraState() == VisionPortal.CameraState.CAMERA_DEVICE_READY ||
                visionPortal.getCameraState() == VisionPortal.CameraState.STOPPING_STREAM) {
            // Note if the camera state is STOPPING_STREAM, it will block the thread until the
            // stream is resumed. This is a documented operation in the SDK.
            visionPortal.resumeStreaming();
        }

        for (Processor processor : processors) {
            if (processor == null) {
                throw new IllegalStateException("Processor is not instantiated!");
            }
            if (!this.processors.contains(processor)) {
                throw new IllegalStateException("Tried to start a processor that was not initialised!");
            }
            visionPortal.setProcessorEnabled(processor, true);
        }
    }

    /**
     * Stop desired processors (Level 2).
     * <p>
     * This method should be called when hardware resources no longer
     * need to be allocated to operating the cameras, and should have the option to be re-enabled
     * with start().
     * <p>
     * Note: The VisionPortal is automatically closed at the end of the OpMode's run time, calling
     * stop() or terminate() is not required at the end of an OpMode.
     * <p>
     * Passing no arguments will pause the Camera Stream (Level 3). Pausing
     * the camera stream will automatically disable any running processors. Note this may
     * take some very small time to resume the stream if start() is called again. If you don't plan
     * on using the camera stream again, it is recommended to call terminate() instead.
     *
     * @param processors TFOD and/or AprilTag
     */
    @SuppressWarnings("rawtypes")
    public void stop(Processor... processors) {
        if (visionPortal == null) {
            throw new IllegalStateException("VisionPortal is not initialised from init()!");
        }

        // Disable processors without pausing the stream
        for (Processor processor : processors) {
            if (processor == null) {
                throw new IllegalStateException("Processor is not instantiated!");
            }
            if (!this.processors.contains(processor)) {
                throw new IllegalStateException("Tried to stop a processor that was not initialised!");
            }
            visionPortal.setProcessorEnabled(processor, false);
        }
    }

    /**
     * Stop all processors and pause the camera stream (Level 3).
     */
    public void stop() {
        if (visionPortal == null) {
            throw new IllegalStateException("VisionPortal is not initialised from init()!");
        }
        // Pause the processor, this will also auto-close any VisionProcessors
        visionPortal.stopStreaming();
    }

    /**
     * Tick all processor camera streams and extract data from the processors.
     * This can optionally be done per processor by calling processor.update()
     * This data is stored in the processor instance and can be accessed with the getters.
     */
    @SuppressWarnings("rawtypes")
    public void tickAll() {
        for (Processor processor : processors) {
            processor.tick();
        }
    }

    /**
     * Get data from all processors.
     * This can optionally can be done per processor by calling processor.getData().
     * This data is stored in the processor instance and can be accessed with getters.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<List<VisionData>> getAllData() {
        List<List<VisionData>> data = new ArrayList<>();
        for (Processor processor : processors) {
            data.add(processor.getData());
        }
        return data;
    }

    /**
     * Terminate all VisionPortal resources (Level 4).
     * <p>
     * Use this method when you are completely done with the VisionPortal and want to free up
     * all available resources. This method will automatically disable all processors and close
     * the VisionPortal, and cannot be undone without calling init() again.
     * <p>
     * It is strongly discouraged to reinitialise the VisionPortal in the same OpMode, as this
     * takes significant time and may cause the OpMode to hang or become unresponsive. Instead,
     * use the {@code start()} and {@code stop()} methods to enable/disable the VisionPortal.
     * Repeated calls to {@code init()} will also cause a termination of the VisionPortal.
     */
    public void terminate() {
        if (visionPortal == null) {
            throw new IllegalStateException("VisionPortal is not initialised from init()!");
        }
        visionPortal.close();
        visionPortal = null;
    }

    /**
     * Get the current status of the camera attached to the VisionPortal.
     */
    public VisionPortal.CameraState getStatus() {
        if (visionPortal == null) {
            throw new IllegalStateException("VisionPortal is not initialised from init()!");
        }
        return visionPortal.getCameraState();
    }

    /**
     * Get the current Frames Per Second of the VisionPortal.
     */
    public double getFps() {
        if (visionPortal == null) {
            throw new IllegalStateException("VisionPortal is not initialised from init()!");
        }
        return visionPortal.getFps();
    }

    /**
     * Start or stop the live camera view (Level 1).
     * When initialised, live view is disabled by default.
     */
    public void setLiveView(boolean enabled) {
        if (visionPortal == null) {
            throw new IllegalStateException("VisionPortal is not initialised from init()!");
        }
        if (enabled) {
            visionPortal.resumeLiveView();
        } else {
            visionPortal.stopLiveView();
        }
    }
}