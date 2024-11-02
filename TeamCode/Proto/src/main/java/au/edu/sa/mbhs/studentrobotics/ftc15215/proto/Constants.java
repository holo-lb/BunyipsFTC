package au.edu.sa.mbhs.studentrobotics.ftc15215.proto;

import com.acmerobotics.dashboard.config.Config;

@Config
final class Constants {
    public static double a_kP = 0.01;
    public static double cl_kP = 0.02;
    public static double cl_kG = 0.17;
    public static double cl_TPS = 250;
    public static double cr_v = 2;
    public static double cr_a = 2;

    private Constants() {}
}