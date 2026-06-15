package com.nec.middleware.hr.constant;

public class TrainingScheduleConstants {

    private TrainingScheduleConstants() {
    }

    public static final String SCHEDULE_NOT_FOUND =
            "Training schedule not found with code: ";

    public static final String SCHEDULE_CREATED =
            "Training schedule created successfully";

    public static final String SCHEDULE_UPDATED =
            "Training schedule updated successfully";

    public static final String SCHEDULE_FETCHED =
            "Training schedule fetched successfully";

    public static final String SCHEDULE_LIST_FETCHED =
            "Training schedules fetched successfully";

    public static final String SCHEDULE_STATUS_CHANGED =
            "Training schedule status updated successfully";

    public static final String SCHEDULE_ALREADY_EXISTS =
            "Schedule already exists for the selected training class";

    public static final String CODE_PREFIX = "TS";

    public static final int CODE_PAD = 4;
}