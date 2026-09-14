from __future__ import annotations

import pytest
from fastapi import FastAPI
from fastapi.testclient import TestClient

from app.api.routes import health_router, router
from app.application.notification_service import NotificationService
from tests.fakes import InMemoryNotificationRepository


@pytest.fixture
def client() -> TestClient:
    app = FastAPI()
    app.include_router(router)
    app.include_router(health_router)
    app.state.notification_service = NotificationService(InMemoryNotificationRepository())
    return TestClient(app)


def test_health_returns_up(client: TestClient):
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "UP"}


def test_list_notifications_returns_empty_list_initially(client: TestClient):
    response = client.get("/api/notifications")

    assert response.status_code == 200
    assert response.json() == []


def test_get_notification_returns_404_when_missing(client: TestClient):
    response = client.get("/api/notifications/42")

    assert response.status_code == 404


def test_list_notifications_after_registering_one(client: TestClient):
    service: NotificationService = client.app.state.notification_service
    service.register_order_created(1, "Alice", "Keyboard", 2)

    response = client.get("/api/notifications")

    assert response.status_code == 200
    body = response.json()
    assert len(body) == 1
    assert body[0]["customer_name"] == "Alice"

    detail = client.get(f"/api/notifications/{body[0]['id']}")
    assert detail.status_code == 200
