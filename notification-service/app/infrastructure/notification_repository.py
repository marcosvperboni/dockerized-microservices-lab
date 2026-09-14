from __future__ import annotations

from typing import Protocol

from redis import Redis

from app.domain.notification import Notification

NOTIFICATIONS_KEY = "notifications"
NOTIFICATIONS_SEQ_KEY = "notifications:seq"


class NotificationRepository(Protocol):
    def next_id(self) -> int: ...

    def save(self, notification: Notification) -> None: ...

    def find_all(self) -> list[Notification]: ...

    def find_by_id(self, notification_id: int) -> Notification | None: ...


class RedisNotificationRepository:
    """Stores notifications as a Redis list of JSON documents."""

    def __init__(self, redis_client: Redis):
        self._redis = redis_client

    def next_id(self) -> int:
        return int(self._redis.incr(NOTIFICATIONS_SEQ_KEY))

    def save(self, notification: Notification) -> None:
        self._redis.rpush(NOTIFICATIONS_KEY, notification.model_dump_json())

    def find_all(self) -> list[Notification]:
        raw_items = self._redis.lrange(NOTIFICATIONS_KEY, 0, -1)
        return [Notification.model_validate_json(item) for item in raw_items]

    def find_by_id(self, notification_id: int) -> Notification | None:
        for notification in self.find_all():
            if notification.id == notification_id:
                return notification
        return None
