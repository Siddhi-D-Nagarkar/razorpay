package org.sdn.razorpay_clone.common.enums;

public enum PaymentEvent {
    AUTHORIZE_ATTEMPT,
    AUTHORIZE_SUCCESS,
    AUTHORIZE_FAIL,
    CAPTURE_REQUEST,
    CAPTURE_SUCCESS,
    CAPTURE_FAIL,
    REFUND_INITIATED,
    REFUND_COMPLETED,
    SETTLE,
    CANCEL,
    CAPTURE_TIMEOUT
}
