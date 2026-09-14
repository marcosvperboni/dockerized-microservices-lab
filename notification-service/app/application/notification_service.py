from __future__ import annotations

from app.domain.notification import Notification
from app.infrastructure.notification_repository import NotificationRepository


class NotificationNotFoundError(Exception):
    def __init__(self, notification_id: int):
        super().__init__(f"Notification not found with id: {notification_id}")


class NotificationService:
    def __init__(self, repository: NotificationRepository):
        self._repository = repository

    def register_order_created(self, order_id: int, customer_name: str, product_name: str, quantity: int) -> Notification:
        notification = Notification.for_order_created(
            notification_id=self._repository.next_id(),
            order_id=order_id,
            customer_name=customer_name,
            product_name=product_name,
            quantity=quantity,
        )
        self._repository.save(notification)
        return notification

    def list_all(self) -> list[Notification]:
        return self._repository.find_all()

    def get_by_id(self, notification_id: int) -> Notification:
        notification = self._repository.find_by_id(notification_id)
        if notification is None:
            raise NotificationNotFoundError(notification_id)
        return notification
