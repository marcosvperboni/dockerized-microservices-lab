from __future__ import annotations

from datetime import datetime, timezone

from pydantic import BaseModel, Field


class Notification(BaseModel):
    id: int
    order_id: int
    customer_name: str
    product_name: str
    quantity: int
    message: str
    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))

    @staticmethod
    def for_order_created(notification_id: int, order_id: int, customer_name: str, product_name: str, quantity: int) -> "Notification":
        message = f"Order #{order_id} created for {customer_name}: {quantity}x {product_name}"
        return Notification(
            id=notification_id,
            order_id=order_id,
            customer_name=customer_name,
            product_name=product_name,
            quantity=quantity,
            message=message,
        )
