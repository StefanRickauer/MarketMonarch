package com.rickauer.marketmonarch.api.enums;

public enum OrderStatus {
	
	PENDING_SUBMIT(	"PendingSubmit", 	"Indicates that you have transmitted the order, but have not yet received confirmation that it has been accepted by the order destination."),
	PENDING_CANCEL(	"PendingCancel", 	"Indicates that you have sent a request to cancel the order but have not yet received cancel confirmation from the order destination. At this point, your order is not confirmed canceled. It is not guaranteed that the cancellation will be successful."),
	PRE_SUBMITTED(	"PreSubmitted",		"Indicates that a simulated order type has been accepted by the IB system and that this order has yet to be elected. The order is held in the IB system until the election criteria are met. At that time the order is transmitted to the order destination as specified."),
	SUBMITTED(		"Submitted", 		"Indicates that your order has been accepted by the system."),
	API_CANCELLED(	"ApiCancelled", 	"After an order has been submitted and before it has been acknowledged, an API client client can request its cancelation, producing this state."),
	CANCELLED(		"Cancelled", 		"Indicates that the balance of your order has been confirmed canceled by the IB system. This could occur unexpectedly when IB or the destination has rejected your order."),
	FILLED(			"Filled", 			"Indicates that the order has been completely filled. Market orders executions will not always trigger a Filled status."),
	INACTIVE(		"Inactive", 		"Indicates that the order was received by the system but is no longer active because it was rejected or canceled.");
	
	
	String orderStatus;
	String description;
	
	private OrderStatus(String status, String desc) {
		orderStatus = status;
		description = desc;
	}
	
	public String getOrderStatus() {
		return orderStatus;
	}
	
	public String getDescription() {
		return description;
	}
}
