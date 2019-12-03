package com.maint.system.model;

public class MaterialAidBean {
	 private String value; //相当于materialId

	 private String label; //相当于materialName

	 private String materialNumber;

	 private String description;

	 private Double price;

	 private String unit;

	 private String materialBrand;

	 private String category;

	 private String ggxh;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getMaterialBrand() {
		return materialBrand;
	}

	public void setMaterialBrand(String materialBrand) {
		this.materialBrand = materialBrand;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGgxh() {
		return ggxh;
	}

	public void setGgxh(String ggxh) {
		this.ggxh = ggxh;
	}

	@Override
	public String toString() {
		return "MaterialAidBean [value=" + value + ", label=" + label + ", materialNumber=" + materialNumber
				+ ", description=" + description + ", price=" + price + ", unit=" + unit + ", materialBrand="
				+ materialBrand + ", category=" + category + ", ggxh=" + ggxh + "]";
	}
	 
}
