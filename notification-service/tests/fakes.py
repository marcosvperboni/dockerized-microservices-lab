from __future__ import annotations

from app.domain.notification import Notification


class InMemoryNotificationRepository:
    def __init__(self):
        self._items: dict[int, Notification] = {}
        self._seq = 0

    def next_id(self) -> int:
        self._seq += 1
        return self._seq

    def save(self, notification: Notification) -> None:
        self._items[notification.id] = notification

    def find_all(self) -> list[Notification]:
        return list(self._items.values())

    def find_by_id(self, notification_id: int) -> Notification | None:
        return self._items.get(notification_id)
