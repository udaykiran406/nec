package com.nec.middleware.hr.constant;

public class TrainingTraineeAllocationConstants {

    private TrainingTraineeAllocationConstants() {}

    public static final String ALLOCATION_NOT_FOUND =
            "Training trainee allocation not found with code: ";

    public static final String ALLOCATION_CREATED =
            "Training trainee allocation created successfully";

    public static final String ALLOCATION_UPDATED =
            "Training trainee allocation updated successfully";

    public static final String ALLOCATION_STATUS_CHANGED =
            "Training trainee allocation status updated successfully";

    public static final String ALLOCATION_LIST_FETCHED =
            "Training trainee allocations fetched successfully";

    public static final String ALLOCATION_FETCHED =
            "Training trainee allocation fetched successfully";

    public static final String TRAINEE_ALREADY_ALLOCATED =
            "This trainee is already allocated to a training class";

    public static final String CODE_PREFIX = "TA";

    public static final int CODE_PAD = 4;
}