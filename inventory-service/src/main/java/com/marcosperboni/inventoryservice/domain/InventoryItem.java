package com.marcosperboni.inventoryservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "inventory_items")
public class InventoryItem implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String productName;

	@Column(nullable = false)
	private Integer quantityAvailable;

	protected InventoryItem() {
	}

	public InventoryItem(String productName, Integer quantityAvailable) {
		this.productName = productName;
		this.quantityAvailable = quantityAvailable;
	}

	public Long getId() {
		return id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQuantityAvailable() {
		return quantityAvailable;
	}

	public void setQuantityAvailable(Integer quantityAvailable) {
		this.quantityAvailable = quantityAvailable;
	}

	public void reserve(int quantity) {
		this.quantityAvailable = Math.max(0, this.quantityAvailable - quantity);
	}
}
