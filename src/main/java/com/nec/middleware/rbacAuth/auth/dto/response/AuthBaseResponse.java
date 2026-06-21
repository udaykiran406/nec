package com.nec.middleware.rbacAuth.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"success", "errorCode", "message", "data", "errorMessage", "errorDescription", "errorTraceMethod"})
public class AuthBaseResponse<T> {
    /** HTTP status code mirrored in the response body. */
    private Integer success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorCode;

    private String message;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorDescription;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorTraceMethod;
}
