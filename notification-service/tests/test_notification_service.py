from __future__ import annotations

import pytest

from app.application.notification_service import NotificationNotFoundError, NotificationService
from tests.fakes import InMemoryNotificationRepository


@pytest.fixture
def notification_service() -> NotificationService:
    return NotificationService(InMemoryNotificationRepository())


def test_register_order_created_persists_and_builds_message(notification_service: NotificationService):
    notification = notification_service.register_order_created(
        order_id=1, customer_name="Alice", product_name="Keyboard", quantity=2
    )

    assert notification.id == 1
    assert notification.order_id == 1
    assert "Alice" in notification.message
    assert "Keyboard" in notification.message


def test_list_all_returns_every_registered_notification(notification_service: NotificationService):
    notification_service.register_order_created(1, "Alice", "Keyboard", 2)
    notification_service.register_order_created(2, "Bob", "Mouse", 1)

    result = notification_service.list_all()

    assert len(result) == 2


def test_get_by_id_raises_when_missing(notification_service: NotificationService):
    with pytest.raises(NotificationNotFoundError):
        notification_service.get_by_id(999)


def test_get_by_id_returns_matching_notification(notification_service: NotificationService):
    created = notification_service.register_order_created(1, "Alice", "Keyboard", 2)

    found = notification_service.get_by_id(created.id)

    assert found == created
