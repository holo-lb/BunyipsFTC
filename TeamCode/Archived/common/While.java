package org.murraybridgebunyips.bunyipslib;

import static org.murraybridgebunyips.bunyipslib.external.units.Units.Nanoseconds;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.murraybridgebunyips.bunyipslib.external.units.Measure;
import org.murraybridgebunyips.bunyipslib.external.units.Time;
import org.murraybridgebunyips.bunyipslib.util.Threads;

import java.util.function.BooleanSupplier;

/**
 * Perform non-blocking loops using an evaluator callback to run the loop.
 * This should be paired with an update(), activeLoop() or onInitLoop(),
 * and this runs synchronously with the main loop.
 * <pre>{@code
 *   public void activeLoop() {
 *       whileLoop.run();
 *       // Your other active loop code
 *       // You can choose to block the entire loop until the while loop is done by using a guard clause
 *       // as the run method will return a boolean indicating if the loop was run.
 *   }
 * }</pre>
 *
 * @author Lucas Bubner, 2023
 * @since 1.0.0-pre
 */
public class While {
    private final BooleanSupplier condition;
    private final Runnable runThis;
    private final Runnable callback;
    private Measure<Time> timeout;
    private ElapsedTime timer;

    private volatile boolean evalLatch;

    /**
     * @param condition The condition or function to evaluate as an exit. Return false to exit the loop.
     * @param runThis   The function to run on each loop iteration.
     * @param callback  The callback to run once [condition] is met.
     * @param timeout   Optional timeout. If the timeout is reached, the loop will exit.
     */
    public While(BooleanSupplier condition, Runnable runThis, Runnable callback, Measure<Time> timeout) {
        this.condition = condition;
        this.runThis = runThis;
        this.callback = callback;
        this.timeout = timeout;
        timer = null;
        evalLatch = false;
    }

    /**
     * Set a new timeout for the loop dynamically.
     */
    public void setTimeout(Measure<Time> timeout) {
        this.timeout = timeout;
    }

    /**
     * Notify the loop that it should run.
     * This will be the method you call to start the loop.
     */
    public void engage() {
        evalLatch = true;
    }

    /**
     * Notify the loop that it should stop now.
     */
    public void terminate() {
        evalLatch = false;
    }

    /**
     * Run the evaluator loop if required.
     *
     * @return True if the loop was run, false if it was not.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean run() {
        if (!evalLatch) {
            return false;
        }
        if (timer == null) {
            timer = new ElapsedTime();
        }

        if (condition.getAsBoolean() && timer.nanoseconds() < timeout.in(Nanoseconds)) {
            runThis.run();
            return true;
        }

        evalLatch = false;
        timer = null;

        callback.run();
        return false;
    }

    /**
     * Get the last status of the evaluator without running it.
     * This is useful for checking if the loop is running.
     *
     * @return True if the loop is running, false if it is not.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean running() {
        return evalLatch;
    }
}
