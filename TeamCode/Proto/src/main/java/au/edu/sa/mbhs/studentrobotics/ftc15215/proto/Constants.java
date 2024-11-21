package au.edu.sa.mbhs.studentrobotics.ftc15215.proto;

import com.acmerobotics.dashboard.config.Config;

@Config
@SuppressWarnings("MissingJavadoc")
public final class Constants {
    public static double cl_kP = 0.004;
    public static double cl_kG = 0.01;
    public static double cl_TPS = 2000;
    public static long cl_MAX = 5700;
    public static double cr_v = 2;
    public static double cr_a = 6;

    private Constants() {
    }
}