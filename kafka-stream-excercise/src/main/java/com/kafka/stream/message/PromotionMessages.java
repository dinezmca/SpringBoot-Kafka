package com.kafka.stream.message;
 
public class PromotionMessages {

	private String promotionCode;

	public PromotionMessages() {

	}

	public PromotionMessages(String promotionCode) {
		super();
		this.promotionCode = promotionCode;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	@Override
	public String toString() {
		return "PromotionMessage [promotionCode=" + promotionCode + "]";
	}

}
