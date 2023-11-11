package org.firstinspires.ftc.teamcode.common;

import static org.firstinspires.ftc.teamcode.common.Text.formatString;

import java.util.ArrayList;
import java.util.List;

/**
 * Null safety utilities for robot components.
 *
 * @author Lucas Bubner, 2023
 */
public class NullSafety {
    public static final List<String> unusableComponents = new ArrayList<>();

    /**
     * Ensure that all objects are not null.
     *
     * @param objs Objects to check for null
     * @return Whether all objects are not null
     */
    public static boolean assertNotNull(Object... objs) {
        for (Object o : objs) {
            if (o == null) {
                Dbg.INSTANCE.error("Assertion by NullSafety.assertNotNull() failed.");
                return false;
            }
        }
        return true;
    }

    public static boolean assertNotNull(List<Object> objs) {
        return assertNotNull(objs.toArray());
    }

    /**
     * Ensure a component is safe to instantiate by checking for null objects.
     * Errors caused by null objects are logged and the component is added to the unusable components list.
     * Components in the unusable components list will not have their errors logged.
     *
     * @param opMode BunyipsOpMode overhead instance
     * @param objs   Objects to check for null
     * @return Whether the component is safe to instantiate
     */
    public static boolean assertComponentArgs(BunyipsOpMode opMode, Class<?> T, Object... objs) {
        boolean safe = true;
        for (Object o : objs) {
            if (o == null) {
                opMode.addTelemetry(formatString("! COM_FAULT: % failed to instantiate due to null constructor arguments", T.getSimpleName()), true);
                opMode.log("error: % is null. additional errors from this component are being ignored.", T.getSimpleName());
                Dbg.INSTANCE.error(formatString("% is null, adding to unusable components...", T.getSimpleName()));
                unusableComponents.add(T.getSimpleName());
                safe = false;
            }
        }
        return safe;
    }
}
