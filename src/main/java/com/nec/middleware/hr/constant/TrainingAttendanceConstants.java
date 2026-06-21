package com.nec.middleware.hr.constant;

public final class TrainingAttendanceConstants {

    private TrainingAttendanceConstants() {
    }

    public static final String ATTENDANCE_CREATED =
            "Training attendance created successfully";


    public static final String ATTENDANCE_LIST_FETCHED =
            "Training attendance list fetched successfully";

    public static final String ATTENDANCE_STATUS_UPDATED =
            "Training attendance status updated successfully";

    public static final String ATTENDANCE_NOT_FOUND =
            "Training attendance not found";

    public static final String ATTENDANCE_ALREADY_EXISTS =
            "Attendance already marked for this trainee on the selected date";

    public static final String TRAINING_CLASS_NOT_FOUND =
            "Training class not found";

    public static final String TRAINEE_NOT_FOUND =
            "Trainee not found";
    public static final String CODE_PREFIX = "ATD";

    public static final String TRAINEE_NOT_ALLOCATED_TO_CLASS =
            "Trainee is not allocated to this training class";

    public static final String TRAINING_SCHEDULE_NOT_FOUND =
            "Training schedule not found for this class";

    public static final String ATTENDANCE_DATE_OUT_OF_SCHEDULE =
            "Attendance date must be within training schedule date range";

}